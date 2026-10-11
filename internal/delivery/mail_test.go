package delivery

import (
	"bufio"
	"context"
	"encoding/base64"
	"fmt"
	"net"
	"strings"
	"testing"
	"time"
)

func smtpServer(t *testing.T) (string, <-chan string) {
	t.Helper()
	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatal(err)
	}
	captured := make(chan string, 1)
	t.Cleanup(func() { listener.Close() })
	go func() {
		defer close(captured)
		conn, err := listener.Accept()
		if err != nil {
			return
		}
		defer conn.Close()
		conn.SetDeadline(time.Now().Add(5 * time.Second))
		fmt.Fprint(conn, "220 localhost ESMTP\r\n")
		reader := bufio.NewReader(conn)
		for {
			line, err := reader.ReadString('\n')
			if err != nil {
				return
			}
			switch {
			case strings.HasPrefix(line, "EHLO"):
				fmt.Fprint(conn, "250-localhost\r\n250 8BITMIME\r\n")
			case strings.HasPrefix(line, "HELO"), strings.HasPrefix(line, "MAIL"), strings.HasPrefix(line, "RCPT"):
				fmt.Fprint(conn, "250 OK\r\n")
			case strings.HasPrefix(line, "DATA"):
				fmt.Fprint(conn, "354 send message\r\n")
				var message strings.Builder
				for {
					part, err := reader.ReadString('\n')
					if err != nil {
						return
					}
					if part == ".\r\n" {
						break
					}
					message.WriteString(part)
				}
				captured <- message.String()
				fmt.Fprint(conn, "250 accepted\r\n")
			case strings.HasPrefix(line, "QUIT"):
				fmt.Fprint(conn, "221 bye\r\n")
				return
			default:
				fmt.Fprint(conn, "500 unsupported\r\n")
			}
		}
	}()
	return listener.Addr().String(), captured
}

func TestSMTPTransportAndTLSRequirement(t *testing.T) {
	address, captured := smtpServer(t)
	host, port, _ := net.SplitHostPort(address)
	mailer := &SMTP{Host: host, Port: port, From: "sender@example.com", AllowInsecure: true}
	if err := mailer.Send(context.Background(), "reader@example.com", "天气提醒", "记得带伞"); err != nil {
		t.Fatal(err)
	}
	message := <-captured
	parts := strings.SplitN(message, "\r\n\r\n", 2)
	if len(parts) != 2 || !strings.Contains(parts[0], "Subject: =?UTF-8?b?") {
		t.Fatal("missing MIME headers", message)
	}
	decoded, err := base64.StdEncoding.DecodeString(strings.Join(strings.Fields(parts[1]), ""))
	if err != nil || string(decoded) != "记得带伞" {
		t.Fatal("invalid mail encoding", err)
	}
	address, _ = smtpServer(t)
	host, port, _ = net.SplitHostPort(address)
	secure := &SMTP{Host: host, Port: port, From: "sender@example.com"}
	if err = secure.Send(context.Background(), "reader@example.com", "title", "body"); err == nil {
		t.Fatal("cleartext transport accepted without explicit configuration")
	}
	if err = mailer.Send(context.Background(), "reader@example.com", "title\r\nInjected: value", "body"); err == nil {
		t.Fatal("mail header injection accepted")
	}
}
