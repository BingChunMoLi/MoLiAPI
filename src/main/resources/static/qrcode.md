# 二维码

接口默认支持 `/v1` 前缀，例如 `/v1/qrcode/generate`。二维码生成返回 JPEG 图片，解码返回统一 JSON。

## 生成二维码

`GET /qrcode/generate`

| 参数 | 类型 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- | --- |
| text | string | 否 | `https://api.bingchunmoli.com` | 二维码内容 |
| width | integer | 否 | 300 | 图片宽度 |
| height | integer | 否 | 300 | 图片高度 |

```text
https://api.bingchunmoli.com/qrcode/generate?text=https%3A%2F%2Fexample.com&width=300&height=300
```

响应为 `image/jpeg` 图片，可用 `<img src="...">` 展示。

## 解码二维码

`GET /qrcode/decode`，以 `multipart/form-data` 上传名为 `file` 的图片。

```bash
curl 'https://api.bingchunmoli.com/qrcode/decode' \
  --form 'file=@qrcode.png'
```

成功响应：

```json
{"code":"00000","msg":"一切OK","data":"https://example.com"}
```
