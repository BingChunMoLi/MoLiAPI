<!--
 * @Author: 冰彦糖
 * @Date: 2020-11-17 22:53:59
 * @LastEditTime: 2021-10-9 23:06:21
 * @LastEditors: 修改以Json形式返回
 * @Description: In User Settings Edit
-->
## Bing今日美图(默认原画)
1. Bing今日美图

请求方式: Get  

请求参数: 无

请求示例:  
https://api.bingchunmoli.com/bing/all

成功返回示例:
> [https://api.bingchunmoli.com/bing/all](https://api.bingchunmoli.com/bing/all)
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": {
    "id": 95,
    "startDate": "20211008",
    "startDateEn": "20211008",
    "fullStartDate": "202110081600",
    "fullStartDateEn": "202110080700",
    "endDate": "20211009",
    "url": "/th?id=OHR.SandhillApache_ZH-CN3021579142_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
    "urlEn": "/th?id=OHR.FriendlyOctopus_EN-CN1968014630_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
    "urlBase": "/th?id=OHR.SandhillApache_ZH-CN3021579142",
    "urlBaseEn": "/th?id=OHR.FriendlyOctopus_EN-CN1968014630",
    "copyright": "野生动物保护区中的沙丘鹤和野鸭，美国新墨西哥州 (© Cathy & Gordon Illg/Jaynes Gallery/DanitaDelimont.com)",
    "copyrightEn": "Common octopus in sea grass off the coast of France in the Gulf of Lion in the Mediterranean Sea (© BIOSPHOTO/Alamy)",
    "copyrightLink": "https://www.bing.com/search?q=%E6%B2%99%E4%B8%98%E9%B9%A4&form=hpcapt&mkt=zh-cn",
    "copyrightLinkEn": "https://www.bing.com/search?q=common+octopus&form=hpcapt&filters=HpDate%3a%2220211008_0700%22",
    "headlineEn": "An uncommonly cool critter",
    "createTime": "2021-10-09T00:23:21.769986",
    "urlUhd": "/th?id=OHR.SandhillApache_ZH-CN3021579142_UHD.jpg",
    "urlUhdEn": "/th?id=OHR.FriendlyOctopus_EN-CN1968014630_UHD.jpg",
    "obsUrlCn": null,
    "obsUrlEn": null
  }
}
```
> [https://api.bingchunmoli.com/bing/cn](https://api.bingchunmoli.com/bing/cn)
```json
{
    "code": "00000",
    "msg": "一切OK",
    "data": {
        "images": [
            {
                "startDate": "20211008",
                "fullStartDate": "202110081600",
                "endDate": "20211009",
                "url": "/th?id=OHR.SandhillApache_ZH-CN3021579142_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
                "urlBase": "/th?id=OHR.SandhillApache_ZH-CN3021579142",
                "copyright": "野生动物保护区中的沙丘鹤和野鸭，美国新墨西哥州 (© Cathy & Gordon Illg/Jaynes Gallery/DanitaDelimont.com)",
                "copyrightLink": "https://www.bing.com/search?q=%E6%B2%99%E4%B8%98%E9%B9%A4&form=hpcapt&mkt=zh-cn",
                "title": "",
                "quiz": "/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20211008_SandhillApache%22&FORM=HPQUIZ",
                "wp": true,
                "hsh": "5c83ea662bca58fd366cebc11a1b34f7",
                "drk": 1,
                "top": 1,
                "bot": 1,
                "hs": []
            }
        ],
        "tooltips": {
            "loading": "正在加载...",
            "previous": "上一个图像",
            "next": "下一个图像",
            "walle": "此图片不能下载用作壁纸。",
            "walls": "下载今日美图。仅限用作桌面壁纸。"
        }
    }
}
```
> [https://api.bingchunmoli.com/bing/en](https://api.bingchunmoli.com/bing/en)
```json
{
    "code": "00000",
    "msg": "一切OK",
    "data": {
        "images": [
            {
                "startDate": "20211008",
                "fullStartDate": "202110080700",
                "endDate": "20211009",
                "url": "/th?id=OHR.FriendlyOctopus_EN-CN1968014630_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
                "urlBase": "/th?id=OHR.FriendlyOctopus_EN-CN1968014630",
                "copyright": "Common octopus in sea grass off the coast of France in the Gulf of Lion in the Mediterranean Sea (© BIOSPHOTO/Alamy)",
                "copyrightLink": "https://www.bing.com/search?q=common+octopus&form=hpcapt&filters=HpDate%3a%2220211008_0700%22",
                "title": "An uncommonly cool critter",
                "quiz": "/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20211008_FriendlyOctopus%22&FORM=HPQUIZ",
                "wp": true,
                "hsh": "a3ac93310ab9e3a2e2a93b6a784cfe25",
                "drk": 1,
                "top": 1,
                "bot": 1,
                "hs": []
            }
        ],
        "tooltips": {
            "loading": "Loading...",
            "previous": "Previous image",
            "next": "Next image",
            "walle": "This image is not available to download as wallpaper.",
            "walls": "Download this image. Use of this image is restricted to wallpaper only."
        }
    }
}
```