# 每日签到与便签

普通 JSON 接口使用统一响应结构。租户通过 `tenant` 请求头指定，省略时默认为 `moli`。

## 签到网址

| 方法 | 说明 | 示例 |
| --- | --- | --- |
| `GET /daily?key=moli` | 获取指定 key 的网址列表 | `GET /daily?key=moli` |
| `PUT /daily` | 添加一个网址，JSON `{ "key":"moli", "value":"https://example.com" }` | `PUT /daily` |
| `PUT /daily/all` | 批量添加，JSON 数组 `[{"key":"moli","value":"https://example.com"}]` | `PUT /daily/all` |
| `POST /daily/signed` | 提交本次签到成功的网址数组 | `POST /daily/signed` |

签到回调请求体示例：

```json
["https://example.com"]
```

成功返回 `data: true`，并触发签到通知。列表保存在内存中，服务重启后恢复初始值。

## 签到记录

| 方法 | 参数 | 说明 |
| --- | --- | --- |
| `GET /daily/check` | `tenant` 请求头 | 查询当天已签到的网址 |
| `GET /daily/param` | 可选 `url` 查询参数、`tenant` 请求头 | 获取可查询网址和日期范围 |
| `GET /daily/query` | `startDate`、`endDate`（ISO 日期，如 `2026-10-01`）、可选 `urls` | 按日期范围和网址查询签到日历 |

```text
GET https://api.bingchunmoli.com/daily/query?startDate=2026-10-01&endDate=2026-10-07&urls=https%3A%2F%2Fexample.com
```

`/daily/query` 的结果按日期映射到签到记录数组。若查询多条网址，可重复传入 `urls`。
