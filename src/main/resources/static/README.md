这是一个简单的API

## 接口说明

目前仅支持https://api.bingchunmoli.com 并且使用了HSTS, 并且加入了 HSTS Preload List

# smart-doc接口文档
[https://api.bingchunmoi.com/doc/index.html](https://api.bingchunmoi.com/doc/index.html)
注: 此网站支持PWA

# eolink在线接口文档
[https://www.eolink.com/share/index?shareCode=9Jav6c](https://www.eolink.com/share/index?shareCode=9Jav6c)

fetch请求示例:
```javascript
fetch('https://api.bingchunmoli.com/bing/all')
    .then(res=>res.json())
    .then(res=>console.log(res))
```