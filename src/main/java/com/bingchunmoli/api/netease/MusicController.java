package com.bingchunmoli.api.netease;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        String playListInfo = musicService.getPlayListInfo(id, cookie);
        System.out.println(playListInfo);
        try {
            return new ObjectMapper().readValue(playListInfo, PlayListBO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
