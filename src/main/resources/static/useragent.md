## UserAgent

1. 根据请求的请求头解析UserAgent

> https://api.bingchunmoli.com/ua/userAgentInfo

请求方式: Get

请求参数: 无

请求示例:
https://api.bingchunmoli.com/ua/userAgentInfo

成功返回示例:

```json
{
    "code": "00000",
    "msg": "一切OK",
    "data": {
        "mobile": false,
        "browser": {
            "name": "Chrome",
            "pattern": "chrome",
            "mobile": false,
            "unknown": false
        },
        "version": "96.0.4664.45",
        "platform": {
            "name": "Linux",
            "pattern": "linux",
            "mobile": false,
            "ipad": false,
            "ios": false,
            "iphoneOrIPod": false,
            "android": false,
            "unknown": false
        },
        "os": {
            "name": "Linux",
            "pattern": "linux",
            "unknown": false
        },
        "osVersion": null,
        "engine": {
            "name": "Webkit",
            "pattern": "webkit",
            "unknown": false
        },
        "engineVersion": "537.36"
    }
}
```

2. 根据请求参数的UserAgent字符串解析UserAgent

> https://api.bingchunmoli.com/ua/userAgentInfoByParam

请求方式: Get

请求参数: userAgent

请求示例:
https://api.bingchunmoli.com/ua/userAgentInfoByParam?userAgent=Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (
KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36

成功返回示例:

```json
{
    "code": "00000",
    "msg": "一切OK",
    "data": {
        "mobile": false,
        "browser": {
            "name": "Chrome",
            "pattern": "chrome",
            "mobile": false,
            "unknown": false
        },
        "version": "50.0.2661.87",
        "platform": {
            "name": "Windows",
            "pattern": "windows",
            "mobile": false,
            "ipad": false,
            "ios": false,
            "iphoneOrIPod": false,
            "android": false,
            "unknown": false
        },
        "os": {
            "name": "Windows 7 or Windows Server 2008R2",
            "pattern": "windows nt 6\\.1",
            "unknown": false
        },
        "osVersion": "6.1",
        "engine": {
            "name": "Webkit",
            "pattern": "webkit",
            "unknown": false
        },
        "engineVersion": "537.36"
    }
}
```