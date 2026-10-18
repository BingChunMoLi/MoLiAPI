# Hosts 订阅

支持统一响应的 JSON 字符串、纯文本预览和下载 hosts 文件。`type` 可重复传入：`0` 全部（默认）、`1` local、`2` myAd、`3` neohostsBase。

## JSON

`GET /host/json?type=1&type=2`

```json
{"code":"00000","msg":"一切OK","data":"127.0.0.1 example.com\\n..."}
```

`data` 是 hosts 文本字符串。

## 纯文本

`GET /host/raw?type=0`，响应正文为 `text/plain` hosts 内容，可直接作为订阅地址。

## 下载文件

`GET /host/file?type=0`，返回 UTF-8 hosts 文件，下载文件名为 `hosts`。
