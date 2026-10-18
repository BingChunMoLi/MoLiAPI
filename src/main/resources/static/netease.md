## <span id="获取网易云歌单">获取网易云歌单</spsn>

> https://api.bingchunmoli.com/music/playlist

请求方式: Get

请求参数: id,cookie


|   名称   |       类型        |   是否必须    |      描述      |
|:------:|:---------------:|:---------:|:------------:|
|   id   | number(Integer) |     是     |    歌单的id     |
| cookie |     String      | 否(私有歌单必填) | 最好携带cookies, |

请求示例:
https://api.bingchunmoli.com/music/playlist?id=***&cookie=***

成功返回示例（歌单详情字段会随歌单内容变化）:
```json
{
  "code": "00000",
  "msg": "一切OK",
  "data": {
    "code": 200,
    "msg": "成功",
    "result": {
      "id": 123456,
      "name": "示例歌单",
      "trackCount": 1,
      "tracks": [
        {"id": 10001, "name": "示例歌曲"}
      ]
    }
  }
}
```

若不提供 cookie，受网易云限制通常只能获取前十首，且不支持“我喜欢”歌单。

## 获取歌单歌曲列表

`GET /music/{id}` 根据已收集的歌单 ID 获取歌曲列表。

```text
GET https://api.bingchunmoli.com/music/123456
```

响应 `data` 为歌曲对象数组，包含歌曲名称及播放信息。

## <span id="获取随机歌曲">获取随机歌曲</spsn>

> https://api.bingchunmoli.com/music/random

请求方式: Get

请求参数: 无

请求示例:
https://api.bingchunmoli.com/music/random

成功时服务端返回 `302 Found`，`Location` 指向网易云歌曲 MP3 地址，并附带 JSON 响应体：

```json
{"code":"00000","msg":"一切OK","data":"https://music.163.com/song/media/outer/url?id=123456.mp3"}
```
