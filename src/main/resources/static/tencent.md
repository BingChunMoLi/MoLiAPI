## <span id="QQ头像">QQ头像</spsn>

> https://api.bingchunmoli.com/tencent/qq

请求方式: Get

请求参数: qq,size

名称|类型|是否必须|描述
:-:|:-:|:-:|:-:
qq|number(Intger)|是|QQ账号 size|number(Intger)|否|图片大小,默认值140 限定可选值(仅支持):

名称|值|描述
:-:|:-:|:-:
size|40|40*40px size|100|100*100px size|140|140*140px size|160|160*160px size|640|640*640px

请求示例:
https://api.bingchunmoli.com/tencent/qq?qq=3239720020

成功返回示例:
![成功返回QQ头像](https://api.bingchunmoli.com/tencent/qq?qq=3239720020)

## <span id="QQ空间头像">QQ空间头像</spsn>

> https://api.bingchunmoli.com/tencent/qz

请求方式: Get

请求参数: qq,size

名称|类型|是否必须|描述
:-:|:-:|:-:|:-:
qq|number(Intger)|是|QQ账号 size|number(Intger)|否|图片大小,默认值140 限定可选值(仅支持):

名称|值|描述
:-:|:-:|:-:
size|40|40*40px size|100|100*100px size|140|140*140px size|160|160*160px size|640|640*640px

请求示例:
https://api.bingchunmoli.com/tencent/qz?qq=3239720020

成功返回示例:  
![成功返回QQ空间头像](https://api.bingchunmoli.com/tencent/qz?qq=3239720020)

### <span id="QQ空间头像JSON版">QQ空间头像JSON</spsn>

> https://api.bingchunmoli.com/tencent/qz/json

请求方式: Get

请求参数: qq,size

名称|类型|是否必须|描述
:-:|:-:|:-:|:-:
qq|number(Intger)|是|QQ账号 size|number(Intger)|否|图片大小,默认值140 限定可选值(仅支持):

名称|值|描述
:-:|:-:|:-:
size|40|40*40px size|100|100*100px size|140|140*140px size|160|160*160px size|640|640*640px

请求示例:
https://api.bingchunmoli.com/tencent/qz/json?qq=3239720020

成功返回示例:

```json
{"code":"00000","msg":"一切OK","data":"portraitCallBack({\"3239720020\":[\"http://qlogo1.store.qq.com/qzone/3239720020/3239720020/100\",52,-1,0,0,0,\"��һ���ӡ����\",0]})"}
```

PS:乱码是官方接口就乱码了。。。
