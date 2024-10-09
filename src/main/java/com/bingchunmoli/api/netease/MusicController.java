package com.bingchunmoli.api.netease;

import com.bingchunmoli.api.bean.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Tag(name = "音乐")
@RequestMapping("music")
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @GetMapping("playlist")
    @Operation(summary = "根据歌单id和cookie获取歌单", description = "如果没有cookie由于网易云限制，只能获取前十首,且不支持我喜欢")
    public PlayListBO getPlayListInfo(String id, String cookie){
        return musicService.getPlayListInfo(id, cookie);
    }

    /**
     * 获取歌单详情
     * @param id 歌单id
     * @return 歌单详情的接口链接
     */
    @GetMapping("{id}")
    public ResultVO<String> getMusicLink(@PathVariable String id){
        return ResultVO.ok("https://music.163.com/api/playlist/detail?id=" + id + "&offset=0&total=true&limit=1001");
    }

    /**
     * 获取随机歌曲
     * @return
     */
    @GetMapping("random")
    public ResponseEntity<ResultVO<String>> getRandomMusicLink(){
        String url = "https://music.163.com/song/media/outer/url?id=" + musicService.getRandomMusicId() + ".mp3";
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .location(URI.create(url))
                .body(ResultVO.ok(url));
    }
}
