package com.bingchunmoli.api.img.controller;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.img.service.IImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;

/**
 * @author BingChunMoLi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("img")
public class ImgController {

    private final IImgService imgService;

    @GetMapping("json")
    public String randomImgUrl() {
        return "http://test";
    }

    @GetMapping(value = "mobile", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByMobile() {
        return imgService.getRandomImageMobile();
    }

    @GetMapping(value = "pc",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<FileSystemResource> randomImgByPc1(){
        return ResponseEntity.ok(new FileSystemResource(imgService.getRandomImgByRedis(ApiConstant.PC_IMG)));
    }

    @GetMapping(value = "pc1", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByPc() {
        return imgService.getRandomImageByPc();
    }


}
