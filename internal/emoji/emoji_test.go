package emoji

import "testing"

func TestEmojiOperations(t *testing.T) {
	if Alias("hello 😄") != "hello :smile:" {
		t.Fatal(Alias("hello 😄"))
	}
	if Unicode(":smile: &#128516;") != "😄 😄" {
		t.Fatal("alias or HTML conversion failed")
	}
	if Unicode(Alias("👦🏿")) != "👦🏿" {
		t.Fatal("skin tone round trip failed")
	}
	if HTML("a😄") != "a&#128516;" {
		t.Fatal("HTML conversion failed")
	}
	if !IsEmoji("😄") || IsEmoji("a😄") || IsEmoji("") {
		t.Fatal("incorrect emoji classification")
	}
	if Remove("a😄👦🏿b") != "ab" {
		t.Fatal("emoji removal failed")
	}
	extracted := Extract("a👨‍👩‍👧‍👦🇨🇳b")
	if len(extracted) != 2 || extracted[0] != "👨‍👩‍👧‍👦" || extracted[1] != "🇨🇳" {
		t.Fatalf("split emoji sequence: %#v", extracted)
	}
	if ByAlias("smile") == nil || len(ByTag("happy")) == 0 || ByAlias("missing") != nil {
		t.Fatal("metadata lookup failed")
	}
}
