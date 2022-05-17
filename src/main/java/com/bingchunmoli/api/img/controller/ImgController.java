package com.bingchunmoli.api.img.controller;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.img.service.IImgService;
import com.bingchunmoli.api.properties.ApiKeyProperties;
import lombok.RequiredArgsConstructor;
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
public class ImgController {

    private final IImgService imgService;
    private final ApiKeyProperties apiKeyProperties;

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
     * pc图片 备用写法，使用BufferedImage对象写出
     * @return BufferedImage|图片
     */
    @GetMapping(value = "pc1", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByPc() {
        return imgService.getRandomImageByPc();
    }

    /**
     * 返回随机1080P图片接口
     * @return 1080Pwebp图片
     */
    @GetMapping(value = "1080", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<FileSystemResource> get1080PImage(){
        int i = ThreadLocalRandom.current().nextInt(48);
        return ResponseEntity.ok(new FileSystemResource(apiKeyProperties.getPath1080() + "1080P" + i + ".webp"));
    }

}
