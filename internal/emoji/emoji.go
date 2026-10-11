package emoji

import (
	_ "embed"
	"encoding/json"
	"fmt"
	"html"
	"regexp"
	"sort"
	"strings"
	"unicode/utf8"
)

//go:embed data/emojis.json
var data []byte

type Emoji struct {
	Unicode         string   `json:"unicode"`
	Description     string   `json:"description"`
	Aliases         []string `json:"aliases"`
	Tags            []string `json:"tags"`
	HTMLDecimal     string   `json:"htmlDecimal"`
	HTMLHexadecimal string   `json:"htmlHexadecimal"`
}

var all []Emoji
var byAlias map[string]Emoji
var aliasPattern = regexp.MustCompile(`:([a-zA-Z0-9_+\-|]+):`)

func init() {
	var entries []struct {
		Unicode     string   `json:"emoji"`
		Description string   `json:"description"`
		Aliases     []string `json:"aliases"`
		Tags        []string `json:"tags"`
	}
	if err := json.Unmarshal(data, &entries); err != nil {
		panic(err)
	}
	byAlias = map[string]Emoji{}
	for _, e := range entries {
		emoji := Emoji{Unicode: e.Unicode, Description: e.Description, Aliases: e.Aliases, Tags: e.Tags}
		for _, r := range e.Unicode {
			emoji.HTMLDecimal += fmt.Sprintf("&#%d;", r)
			emoji.HTMLHexadecimal += fmt.Sprintf("&#x%x;", r)
		}
		all = append(all, emoji)
		for _, alias := range e.Aliases {
			byAlias[alias] = emoji
		}
	}
	sort.SliceStable(all, func(i, j int) bool { return len(all[i].Unicode) > len(all[j].Unicode) })
}

func ByAlias(alias string) *Emoji {
	e, exists := byAlias[strings.Trim(alias, ":")]
	if !exists {
		return nil
	}
	return &e
}
func ByTag(tag string) []Emoji {
	result := []Emoji{}
	for _, e := range all {
		for _, t := range e.Tags {
			if t == tag {
				result = append(result, e)
				break
			}
		}
	}
	return result
}

func transform(text string, fn func(Emoji, string) string) (string, []string) {
	var result strings.Builder
	extracted := []string{}
	for len(text) > 0 {
		found := false
		for _, e := range all {
			if !strings.HasPrefix(text, e.Unicode) {
				continue
			}
			match := e.Unicode
			rest := text[len(match):]
			r, n := utf8.DecodeRuneInString(rest)
			if r >= 0x1f3fb && r <= 0x1f3ff {
				match += rest[:n]
			}
			extracted = append(extracted, match)
			result.WriteString(fn(e, match))
			text = text[len(match):]
			found = true
			break
		}
		if found {
			continue
		}
		_, size := utf8.DecodeRuneInString(text)
		result.WriteString(text[:size])
		text = text[size:]
	}
	return result.String(), extracted
}

func Alias(text string) string {
	result, _ := transform(text, func(e Emoji, match string) string {
		alias := e.Aliases[0]
		if match != e.Unicode {
			r, _ := utf8.DecodeRuneInString(match[len(e.Unicode):])
			alias += fmt.Sprintf("|type_%d", r-0x1f3fb+2)
		}
		return ":" + alias + ":"
	})
	return result
}

func Unicode(text string) string {
	return aliasPattern.ReplaceAllStringFunc(html.UnescapeString(text), func(alias string) string {
		parts := strings.Split(strings.Trim(alias, ":"), "|")
		e, exists := byAlias[parts[0]]
		if !exists {
			return alias
		}
		result := e.Unicode
		if len(parts) == 2 {
			for n := 2; n <= 6; n++ {
				if parts[1] == fmt.Sprintf("type_%d", n) {
					result += string(rune(0x1f3fb + n - 2))
				}
			}
		}
		return result
	})
}

func HTML(text string) string {
	result, _ := transform(text, func(e Emoji, _ string) string { return e.HTMLDecimal })
	return result
}
func Extract(text string) []string {
	_, list := transform(text, func(_ Emoji, match string) string { return match })
	return list
}
func Remove(text string) string {
	result, _ := transform(text, func(_ Emoji, _ string) string { return "" })
	return result
}
func IsEmoji(text string) bool { list := Extract(text); return len(list) == 1 && list[0] == text }
