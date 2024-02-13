package com.bingchunmoli.api.netease;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
