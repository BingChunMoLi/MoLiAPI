# 应用设备

用于登记应用设备及推送令牌。JSON 接口使用统一响应结构。

## 根据设备信息获取设备 ID

`GET /app/{name}/{model}/{androidId}`

```text
GET https://api.bingchunmoli.com/app/MyApp/Pixel%207/abc123
```

```json
{"code":"00000","msg":"一切OK","data":1}
```

若设备不存在，接口会创建记录后返回 ID。

## 保存或更新设备推送信息

`POST /app`，成功的 HTTP 状态为 `201 Created`。

```json
{
  "id": 1,
  "token": "FCM_DEVICE_TOKEN",
  "hmsToken": "",
  "pushType": "fcm",
  "name": "MyApp",
  "model": "Pixel 7",
  "androidId": "abc123"
}
```

`id` 可省略以新增。`pushType` 可用 `fcm` 或 `hms`；未指定时，有 `hmsToken` 则使用 HMS，否则使用 FCM。
