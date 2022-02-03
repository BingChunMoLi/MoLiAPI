package com.bingchunmoli.api.img.controller;

import com.bingchunmoli.api.img.service.IImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public String randomImgUrl(){
        return "http://test";
    }

    @GetMapping(value = "pc", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByPc(){
        return imgService.getRandomImageByPc();
    }


    @GetMapping(value = "mobile", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage randomImgByMobile(){
        return imgService.getRandomImageMobile();
    }
}
