package com.bingchunmoli.api.netease;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("music")
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @GetMapping("playlist")
    public PlayListBO getPlayListInfo(String id, String cookie){
        return musicService.getPlayListInfo(id, cookie);
    }

}
