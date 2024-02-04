package com.bingchunmoli.api.img.controller;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.img.service.ImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机图
 * @author BingChunMoLi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("img")
@ConditionalOnProperty(prefix = "moli.apiConfig", name = {"pcPath", "mobilePath", "path1080"})
public class ImgController {

    private final ImgService imgService;
    private final ApiConfig apiConfig;

    /**
     * 手机版
     * @return BufferedImage|图片
     */
    @GetMapping(value = "mobile", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByMobile() {
        return imgService.getRandomImageMobile();
    }

    /**
     * pc图片 使用ResponseEntity写出
     * @return ResponseEntity<FileSystemResource>|ResponseEntity<FileSystemResource>
     */
    @GetMapping(value = "pc",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<FileSystemResource> randomImgByPc1(){
        return ResponseEntity.ok(new FileSystemResource(imgService.getRandomImgByRedis(ApiConstant.PC_IMG)));
    }

    /**
     * 返回随机1080P图片接口
     * @return 1080Pwebp图片
     */
    @GetMapping(value = "1080", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<FileSystemResource> get1080PImage(){
        int i = ThreadLocalRandom.current().nextInt(48);
        return ResponseEntity.ok(new FileSystemResource(apiConfig.getPath1080() + "1080P" + i + ".webp"));
    }

}
