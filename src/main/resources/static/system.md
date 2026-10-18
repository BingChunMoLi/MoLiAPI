# 用户与系统配置

`/system` 下的接口要求登录。登录成功后服务端通过 Session Cookie 维持登录态；后续请求需携带该 Cookie。接口支持 `/v1` 前缀。

## 初始化状态

`GET /user/init` 返回 `data: true` 表示尚无用户，可进行首次注册。

## 注册

`PUT /user/register`

```json
{"name":"admin","password":"your-password"}
```

成功后建立登录 Session。

## 登录

`POST /user/login`，请求体同注册。成功响应：

```json
{"code":"00000","msg":"一切OK","data":true}
```

## 读取和更新系统配置

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/system` | 获取当前 API 配置 |
| POST | `/system` | 提交配置 JSON 并更新配置 |

```bash
curl -b cookies.txt -c cookies.txt -X GET 'https://api.bingchunmoli.com/system'
```

配置包含服务端集成和路径信息。不要将真实密钥或敏感配置发布到公开客户端。
