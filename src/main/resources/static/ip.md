## 当前公网Ip查询

1. 当前公网Ip查询 
请求示例:
```javascript
fetch('https://api.bingchunmoli.com/ip')
    .then(res=>res.json())
    .then(res=>console.log(res))
```

请求方法: Get

请求参数: 无

请求成功实例:

```json
{"code":"00000","msg":"一切OK","data":"36.*.*.*"}
```