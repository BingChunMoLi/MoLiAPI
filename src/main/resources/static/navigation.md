# 导航

导航接口通过 `tenant` 请求头区分数据空间，默认值为 `public`。这些接口当前均未启用登录拦截，请按服务部署环境控制访问。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/navigation` | 获取导航；`tenant` 请求头可选，默认 `public` |
| PUT | `/navigation` | 新增导航，JSON 字段见下例 |
| POST | `/navigation/{id}` | 修改指定导航 |
| DELETE | `/navigation/{id}` | 删除指定导航 |
| POST | `/navigation/import` | 导入内置导航 |
| GET | `/navigation/export` | 导出 public 导航 |

新增示例：

```json
{
  "title": "示例站点",
  "des": "站点描述",
  "url": "https://example.com",
  "icon": "https://example.com/icon.png",
  "tenant": "public",
  "tagList": []
}
```

响应为统一结构；列表接口的 `data` 为导航数组，新增/修改/删除接口的 `data` 为布尔值。
