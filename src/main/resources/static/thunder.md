# 迅雷链接转换

## 迅雷链接转原始链接

`GET /thunder/toRaw`

| 参数 | 必填 | 说明 |
| --- | --- | --- |
| thunderURL | 是 | 以 `thunder://` 开头的迅雷协议链接 |

```text
GET https://api.bingchunmoli.com/thunder/toRaw?thunderURL=thunder%3A%2F%2FQUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa
```

```json
{"code":"00000","msg":"一切OK","data":"http://tool.lu/test.zip"}
```

## 原始链接转迅雷链接

`GET /thunder/toThunder?rawURL=http%3A%2F%2Ftool.lu%2Ftest.zip`

```json
{"code":"00000","msg":"一切OK","data":"thunder://QUFodHRwOi8vdG9vbC5sdS90ZXN0LnppcFpa"}
```
