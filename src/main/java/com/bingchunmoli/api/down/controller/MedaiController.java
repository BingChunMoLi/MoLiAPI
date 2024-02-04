package com.bingchunmoli.api.down.controller;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.down.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author MoLi
 */

@RestController
@RequiredArgsConstructor
public class MedaiController {

    private final MediaService mediaService;

    public String putMedia(@RequestParam String av, @RequestParam(defaultValue = "0") int onlyAudio) throws IOException {
        if (StrUtil.isBlank(av)) {
            throw new RuntimeException("url参数为空");
        }
        return mediaService.downloadOne(av, onlyAudio);
    }
}
