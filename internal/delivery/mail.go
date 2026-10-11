package delivery

import (
	"context"
	"crypto/tls"
	"encoding/base64"
	"fmt"
	"mime"
	"net"
	"net/mail"
	"net/smtp"
	"os"
	"strings"
	"time"
)

type SMTP struct {
	Host, Port, Username, Password, From string
	ImplicitTLS, AllowInsecure           bool
}

func MailFromEnvironment() *SMTP {
	if os.Getenv("SMTP_HOST") == "" {
		return nil
	}
	port := os.Getenv("SMTP_PORT")
	if port == "" {
		port = "587"
	}
	from := os.Getenv("SMTP_FROM")
	if from == "" {
		from = os.Getenv("SMTP_USERNAME")
	}
	return &SMTP{Host: os.Getenv("SMTP_HOST"), Port: port, Username: os.Getenv("SMTP_USERNAME"),
		Password: os.Getenv("SMTP_PASSWORD"),
		From:     from, ImplicitTLS: port == "465", AllowInsecure: os.Getenv("SMTP_ALLOW_INSECURE") == "true"}
}

func (s *SMTP) Send(ctx context.Context, to, title, body string) error {
	from, err := mail.ParseAddress(s.From)
	if err != nil {
		return fmt.Errorf("invalid SMTP sender")
	}
	recipient, err := mail.ParseAddress(to)
	if err != nil {
		return fmt.Errorf("invalid mail recipient")
	}
	if strings.ContainsAny(title, "\r\n") {
		return fmt.Errorf("invalid mail subject")
	}
	dialer := &net.Dialer{Timeout: 20 * time.Second}
	var conn net.Conn
	tlsConfig := &tls.Config{ServerName: s.Host, MinVersion: tls.VersionTLS12}
	if s.ImplicitTLS {
		conn, err = tls.DialWithDialer(dialer, "tcp", net.JoinHostPort(s.Host, s.Port), tlsConfig)
	} else {
		conn, err = dialer.DialContext(ctx, "tcp", net.JoinHostPort(s.Host, s.Port))
	}
	if err != nil {
		return err
	}
	defer conn.Close()
	deadline := time.Now().Add(30 * time.Second)
	if limit, ok := ctx.Deadline(); ok && limit.Before(deadline) {
		deadline = limit
	}
	if err = conn.SetDeadline(deadline); err != nil {
		return err
	}
	stop := context.AfterFunc(ctx, func() { conn.Close() })
	defer stop()
	client, err := smtp.NewClient(conn, s.Host)
	if err != nil {
		return err
	}
	defer client.Close()
	if !s.ImplicitTLS {
		if supported, _ := client.Extension("STARTTLS"); supported {
			if err = client.StartTLS(tlsConfig); err != nil {
				return err
			}
		} else if !s.AllowInsecure {
			return fmt.Errorf("SMTP server requires TLS support")
		}
	}
	if s.Username != "" {
		if err = client.Auth(smtp.PlainAuth("", s.Username, s.Password, s.Host)); err != nil {
			return err
		}
	}
	if err = client.Mail(from.Address); err != nil {
		return err
	}
	if err = client.Rcpt(recipient.Address); err != nil {
		return err
	}
	writer, err := client.Data()
	if err != nil {
		return err
	}
	message := "From: " + from.String() + "\r\nTo: " + recipient.String() + "\r\nSubject: " +
		mime.BEncoding.Encode("UTF-8", title) +
		"\r\nMIME-Version: 1.0\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: base64\r\n\r\n"
	encoded := base64.StdEncoding.EncodeToString([]byte(body))
	for len(encoded) > 76 {
		message += encoded[:76] + "\r\n"
		encoded = encoded[76:]
	}
	message += encoded + "\r\n"
	if _, err = writer.Write([]byte(message)); err != nil {
		writer.Close()
		return err
	}
	if err = writer.Close(); err != nil {
		return err
	}
	return client.Quit()
}
