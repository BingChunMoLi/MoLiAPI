package com.bingchunmoli.api.img.controller;

import cn.hutool.http.useragent.UserAgentUtil;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.img.service.ImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
@Tag(name = "随机图", description = "配置中含有moli.apiConfig.[pcPath, mobilePath, path1080]才会生效")
@ConditionalOnProperty(prefix = "moli.apiConfig", name = {"pcPath", "mobilePath", "path1080"})
public class ImgController {

    private final ImgService imgService;
    private final ApiConfig apiConfig;

    /**
     * 手机版
     * @return BufferedImage|图片
     */
    @GetMapping(value = "mobile", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "移动端随机图,图片来源: 熙宝")
    public BufferedImage randomImgByMobile() {
        return imgService.getRandomImageMobile();
    }

    /**
     * pc图片 使用ResponseEntity写出
     * @return ResponseEntity<FileSystemResource>|ResponseEntity<FileSystemResource>
     */
    @GetMapping(value = "pc",produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "电脑端随机图,图片来源: 熙宝")
    public ResponseEntity<FileSystemResource> randomImgByPc1(){
        return ResponseEntity.ok(new FileSystemResource(imgService.getRandomImgByRedis(ApiConstant.REDIS_PC_IMG_KEY)));
    }

    /**
     * 返回随机1080P图片接口
     * @return 1080Pwebp图片
     */
    @Operation(summary = "自用1080p随机图")
    @GetMapping(value = "1080", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<FileSystemResource> get1080PImage(){
        int i = ThreadLocalRandom.current().nextInt(48);
        return ResponseEntity.ok(new FileSystemResource(apiConfig.getPath1080() + "1080P" + i + ".webp"));
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "根据useragent自动判断使用pc还是移动端, 图库来源: 熙宝")
    public ResponseEntity<FileSystemResource> getRandomImage(@RequestHeader String userAgent) {
        if (UserAgentUtil.parse(userAgent).isMobile()) {
            return ResponseEntity.ok(new FileSystemResource(imgService.getRandomImgByRedis(ApiConstant.REDIS_MOBILE_IMG_KEY)));
        } else {
            return ResponseEntity.ok(new FileSystemResource(imgService.getRandomImgByRedis(ApiConstant.REDIS_PC_IMG_KEY)));
        }
    }
}
