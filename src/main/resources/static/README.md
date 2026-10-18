这是一个简单的API

## 接口说明

目前仅支持https://api.bingchunmoli.com 并且使用了HSTS, 并且加入了 HSTS Preload List

注: 此网站支持PWA

除图片、文件下载和纯文本接口外，JSON接口通常使用统一结构：`{"code":"00000","msg":"一切OK","data":...}`。
除特别说明外，接口支持在路径前增加 `/v1`（例如 `/ip` 和 `/v1/ip`）；版本接口另支持 `/v2/version`。
`/system` 接口需要先登录，浏览器会通过 Session Cookie 保持登录状态。

# eolink在线接口文档
[https://www.eolink.com/share/index?shareCode=9Jav6c](https://www.eolink.com/share/index?shareCode=9Jav6c)

# swagger3.0文档

[https://api.bingchunmoli.com/doc.html](https://api.bingchunmoli.com/doc.html)

fetch请求示例:
```javascript
fetch('https://api.bingchunmoli.com/bing/all')
    .then(res=>res.json())
    .then(res=>console.log(res))
```

## 快速开始:

```shell
docker run -d --name api -p 8090:8090 -v /tmp:/java/.api/ bingchunmoli/moliapi:latest
```

## 已支持接口:

1. Bing美图
2. emoji表情处理
3. 公网IP查询
4. UserAgent解析
5. 二维码处理
6. 随机诗词或指定
7. 随机一言或制定
8. QQ头像获取(支持多种方式)
9. QQ空间头像获取(支持多种方式)
10. 天气查询
11. Hosts订阅
12. 随机图
13. 迅雷链接转换
14. 每日签到网址
15. 请求地址
16. 自动更新腾讯CDN证书(定时任务, 暂仅支持单域名,如需多域名请提Iss)
17. 网易云音乐歌单收藏
18. b站收藏获取,通知失效视频
19. 导航管理、设备注册、系统配置
20. 租金计算、版本查询

