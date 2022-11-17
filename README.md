# [MoLiAPI](https://api.bingchunmoli.com)

![](https://badgen.net/github/release/bingchunmoli/MoLiApi/stable)
![](https://badgen.net/github/stars/bingchunmoli/MoLiApi)
![](https://badgen.net/github/forks/bingchunmoli/MoLiApi)
![](https://badgen.net/github/open-issues/bingchunmoli/MoLiApi)
![](https://badgen.net/github/prs/bingchunmoli/MoLiApi)
![](https://badgen.net/github/commits/bingchunmoli/MoLiApi)
![](https://badgen.net/github/releases/bingchunmoli/MoLiApi)
![](https://badgen.net/github/license/bingchunmoli/MoLiApi)
![](https://badgen.net/github/contributors/bingchunmoli/MoLiApi)


## 快速开始:
1. docker(v3.8.1):
```shell
docker run -d --name api -p 8090:8090 -v /tmp:/root/.api/ bingyantang/moliapi:h2-alpha
```
3. fatjar:
使用h2(部分功能)
```shell
java -jar --spring.profiles.active=h2 moliapi.jar
```
使用mysql(需配置)
```shell
java -jar --spring.profiles.active=prod moliapi.jar
```
## 已支持接口:
1. Bing美图
2. emoji表情处理
3. 请求IP
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

## 启用所有功能
1. 添加配置文件(~/.api/目录下) application.yml
2. 下载application-template.yml 并修改文件名为application-prod.yml
3. 完善application-prod.yml配置(如果使用H2参考，application-h2.yml，如果使用mysql,参考application-template.yml)
4. application.yml内容添加
```yaml
spring:
  profiles:
    active: prod
```
