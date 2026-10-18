# 其他接口

## API 版本

`GET /version` 或 `GET /v2/version` 获取当前服务版本；`/version` 也支持 `/v1/version`。

```json
{"code":"00000","msg":"一切OK","data":"版本号"}
```

## 租金计算

`GET /rent`，所有参数可省略，默认按 `0` 计算。

| 参数 | 说明 |
| --- | --- |
| month | 月数 |
| rentPrice | 月租金 |
| waterConsumption | 用水量 |
| waterCostRate | 水费单价 |
| electricityConsumption | 用电量 |
| powerRate | 电费单价 |

```text
GET https://api.bingchunmoli.com/rent?month=1&rentPrice=2000&waterConsumption=3&waterCostRate=5&electricityConsumption=100&powerRate=0.6
```

```json
{
  "code":"00000",
  "msg":"一切OK",
  "data":{"rentPriceCalc":"2000","waterPriceCalc":"15","powerPriceCalc":"60","totalPrice":"2075"}
}
```

## IP 地址信息

`GET /ip` 获取当前请求的公网 IP；`GET /ip/address` 获取按请求 IP 解析的地址信息。两者均无需参数，返回统一 JSON。
