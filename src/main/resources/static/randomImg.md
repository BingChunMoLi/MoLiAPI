# 随机图片

随机图库接口需要配置服务端 `pcPath`、`mobilePath` 和 `path1080` 图片目录；未配置时接口不会启用。接口返回图片二进制内容，不经过 JSON 包装。

| 方法 | 说明 |
| --- | --- |
| `GET /img` | 根据 `User-Agent` 自动选择移动端或电脑端图片 |
| `GET /img/mobile` | 获取移动端随机图片 |
| `GET /img/pc` | 获取电脑端随机图片 |
| `GET /img/1080` | 获取随机 1080P 图片 |

```html
<img src="https://api.bingchunmoli.com/img" alt="随机图片">
```

`/img` 需要带有 `User-Agent` 请求头；图片响应的 Content-Type 按实际图片类型返回。`/img/1080` 当前返回 WebP 图片。
