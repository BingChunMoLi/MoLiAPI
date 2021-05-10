<!--
 * @Author: 冰彦糖
 * @Date: 2020-11-17 22:53:18
 * @LastEditTime: 2020-11-28 20:45:50
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \undefinedd:\Github\JAVA\api_v2\src\main\resources\templates\shici.md
-->
## 诗词
1. 随机诗词  
> https://api.bingchunmoli.com/shici/random  

请求方式: Get  

请求参数: 无

请求示例:
https://api.bingchunmoli.com/shici/random  

成功返回示例:
{"code":"00000","msg":"一切OK","data":{"id":88,"content":"春山烟欲收，天淡星稀小。","origin":"生查子·春山烟欲收","author":"牛希济","category":"古诗文-天气-星星","deleted":0,"gmtCreate":null,"gmtModified":null,"version":null}}

1. 指定诗词  
> https://api.bingchunmoli.com/shici/{id}  

请求方式: Get  

请求参数: id (路径变量)

请求示例:
https://api.bingchunmoli.com/shici/1  

成功返回示例:
{"code":"00000","msg":"一切OK","data":{"id":1,"content":"天阶夜色凉如水，卧看牵牛织女星。","origin":"秋夕","author":"杜牧","category":"古诗文-天气-星星","deleted":0,"gmtCreate":null,"gmtModified":null,"version":null}}