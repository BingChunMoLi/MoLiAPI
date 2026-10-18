# 天气

天气接口要求服务端配置和风天气 Key；未配置时接口不会启用。JSON 响应使用统一结构。

## 未来天气

`GET /weather/byDay`

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| location | string | 否 | 城市名、和风天气 location ID 或 `经度,纬度`；省略时按请求 IP 定位 |
| day | integer | 否 | 预报天数，支持 `3`（默认）或 `7` |

```text
GET https://api.bingchunmoli.com/weather/byDay?day=3&location=北京
```

响应的 `data` 是和风天气返回的 JSON 数据。

## 当前天气

`GET /weather/now`，根据请求 IP 定位，无参数。响应的 `data` 是和风天气实时天气 JSON。

## 订阅天气邮件

`POST /weather/sub`，请求体为 JSON：

```json
{"email":"user@example.com","location":"北京"}
```

`email` 需为有效邮箱；成功时返回 `data: true`。同一邮箱在 15 分钟内重复申请会返回业务码 `A0001`。确认邮件中的回调链接完成订阅。

## 确认及取消订阅

邮件中的 `param` 是服务端签名令牌，不需要手动构造。确认链接对应 `GET /weather/callback?param=...`；取消链接对应 `GET /weather/unSub?param=...`。令牌有效时 `data` 表示数据库操作是否成功，无效令牌返回 `false`。
