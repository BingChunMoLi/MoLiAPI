## Emoji
1. 将字符串中的Unicode Emoji字符转换为别名表现形式（两个":"包围的格式）
> https://api.bingchunmoli.com/emoji/alise

请求方式: Get

请求参数: emoji (emoji表情Unicode字符)

请求示例:
https://api.bingchunmoli.com/emoji/alise?emoji=😄

成功返回示例:
```json
{
    "code": "00000",
    "msg": "一切OK",
    "data": ":smile:"
}
```

2. 将子串中的Emoji别名（两个":"包围的格式）和其HTML表示形式替换为为Unicode Emoji符号
> https://api.bingchunmoli.com/emoji/unicode

请求方式: Get

请求参数: emoji (emoji表情Unicode字符)

请求示例:
https://api.bingchunmoli.com/emoji/unicode?emoji=:smile:

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": "😄"
}
```

3. 将字符串中的Unicode Emoji字符转换为HTML表现形式
> https://api.bingchunmoli.com/emoji/html

请求方式: Get

请求参数: emoji (emoji表情Unicode字符)

请求示例:
https://api.bingchunmoli.com/emoji/unicode?emoji=👦🏿

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": "&amp;#128102;"
}
```

4. 是否为Emoji表情的Unicode符
> https://api.bingchunmoli.com/emoji/isEmoji

请求方式: Get

请求参数: emoji (可能是emoji表情Unicode字符)

请求示例:
https://api.bingchunmoli.com/emoji/isEmoji?emoji=👦🏿

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": "true"
}
```

5. 是否含有emoji表情的Unicode符
> https://api.bingchunmoli.com/emoji/contains

请求方式: Get

请求参数: emoji (可能含有emoji表情Unicode字符)

请求示例:
https://api.bingchunmoli.com/emoji/contains?emoji=👦🏿

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": "true"
}
```

6. 根据tag获取对应所有的Emoji表情
> https://api.bingchunmoli.com/emoji/tag

请求方式: Get

请求参数: tag (tag标签)

请求示例:
https://api.bingchunmoli.com/emoji/tag?tag=happy

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": [
    {
      "description": "smiling face with open mouth and tightly-closed eyes",
      "aliases": [
        "laughing",
        "satisfied"
      ],
      "tags": [
        "happy",
        "haha"
      ],
      "unicode": "😆",
      "htmlHexadecimal": "&#x1f606;",
      "htmlDecimal": "&#128518;",
      "htmlHexidecimal": "&#x1f606;"
    },
    {
      "description": "grinning face",
      "aliases": [
        "grinning"
      ],
      "tags": [
        "smile",
        "happy"
      ],
      "unicode": "😀",
      "htmlHexadecimal": "&#x1f600;",
      "htmlDecimal": "&#128512;",
      "htmlHexidecimal": "&#x1f600;"
    },
    {
      "description": "smiling face with open mouth and smiling eyes",
      "aliases": [
        "smile"
      ],
      "tags": [
        "happy",
        "joy",
        "pleased"
      ],
      "unicode": "😄",
      "htmlHexadecimal": "&#x1f604;",
      "htmlDecimal": "&#128516;",
      "htmlHexidecimal": "&#x1f604;"
    },
    {
      "description": "smiling face with open mouth",
      "aliases": [
        "smiley"
      ],
      "tags": [
        "happy",
        "joy",
        "haha"
      ],
      "unicode": "😃",
      "htmlHexadecimal": "&#x1f603;",
      "htmlDecimal": "&#128515;",
      "htmlHexidecimal": "&#x1f603;"
    }
  ]
}
```

7. 通过别名获取Emoji
> https://api.bingchunmoli.com/emoji/getByAlise

请求方式: Get

请求参数: alise (别名)

请求示例:
https://api.bingchunmoli.com/emoji/getByAlise?alise=happy

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": {
    "description": "smiling face with open mouth and smiling eyes",
    "aliases": [
      "smile"
    ],
    "tags": [
      "happy",
      "joy",
      "pleased"
    ],
    "unicode": "😄",
    "htmlHexadecimal": "&#x1f604;",
    "htmlDecimal": "&#128516;",
    "htmlHexidecimal": "&#x1f604;"
  }
}
```

8. removeAllEmojis
> https://api.bingchunmoli.com/emoji/getByAlise

请求方式: Get

请求参数: emojiStr (可能含有Emoji Unicode字符的字符串)

请求示例:
https://api.bingchunmoli.com/emoji/removeAllEmojis?emojiStr=123😄321

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": "123321"
}
```

9. 提取字符串中所有的Emoji Unicode
> https://api.bingchunmoli.com/emoji/extractEmojis

请求方式: Get

请求参数: emojiStr (可能含有Emoji Unicode字符的字符串)

请求示例:
https://api.bingchunmoli.com/emoji/extractEmojis?emojiStr=123😄321

成功返回示例:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": [
    "😄"
  ]
}
```
