<!--
 * @Author: 冰彦糖
 * @Date: 2020-11-07 00:16:41
 * @LastEditTime: 2020-11-28 20:51:12
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \undefinedd:\Github\JAVA\api\docs\README.md
-->

## 一言

1. 随机一言

> https://api.bingchunmoli.com/yiyan/random

请求方式: Get

请求参数: 无

请求示例:  
https://api.bingchunmoli.com/yiyan/random

成功返回示例:

```json
{"code":"00000","msg":"一切OK","data":{"id":182,"uuid":"984c59d0-0f74-4ce7-b9dd-2ecfb39dd490","hitokoto":"要改变别人的心真是件很难办的事，不过改变自己要容易一点。","type":"a","from":"XXXHolic","fromWho":null,"creator":"E聋魍魉","creatorUid":0,"reviewer":0,"commitFrom":"web","createdAt":"1468605911","length":28,"deleted":0,"gmtCreate":null,"gmtModified":null,"version":null}}
```

2. 指定一言

> https://api.bingchunmoli.com/yiyan/{id}

请求方式: Get

请求参数: id (路径变量)

请求示例:  
https://api.bingchunmoli.com/yiyan/1

成功返回示例:

```json
{"code":"00000","msg":"一切OK","data":{"id":1,"uuid":"9818ecda-9cbf-4f2a-9af8-8136ef39cfcd","hitokoto":"与众不同的生活方式很累人呢，因为找不到借口。","type":"a","from":"幸运星","fromWho":null,"creator":"跳舞的果果","creatorUid":0,"reviewer":0,"commitFrom":"web","createdAt":"1468605909","length":22,"deleted":0,"gmtCreate":null,"gmtModified":null,"version":null}}
```