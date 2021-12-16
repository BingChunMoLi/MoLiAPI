package com.bingchunmoli.api.tencent.controller;


import com.bingchunmoli.api.tencent.bean.enums.QQSizeEnum;
import com.bingchunmoli.api.tencent.bean.enums.QZSizeEnum;
import com.bingchunmoli.api.tencent.service.IQqService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


/**
 * @author bingchunmoli
 **/
@Slf4j
@RestController
@RequestMapping("tencent")
public class QQController {
    @Resource
    private IQqService qqService;

    @ApiOperation("返回QQ头像")
    @GetMapping(value = "qq", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQqImage(String qq, @RequestParam(required = false, defaultValue = "140") Integer size) {
        if (size <= 0) {
            log.debug("非法参数值 负数");
            return null;
        }
        if (isQqImageSize(size)) {
            return qqService.getQqImage(qq, size);
        } else {
            log.debug("非法参数值 size:" + size);
            return null;
        }
    }

    @ApiOperation("返回QQ空间头像")
    @GetMapping(value = "qz", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQzImage(String qq, @RequestParam(required = false, defaultValue = "100") Integer size) {
        if (size <= 0) {
            return null;
        }
        if (isQzImageSize(size)) {
            return qqService.getQzImage(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return null;
    }

    @ApiOperation("返回JSON的QQ头像地址,加密")
    @GetMapping("qq/json")
    @CrossOrigin
    public String getQqImageForJson(String qq, @RequestParam(required = false, defaultValue = "100") Integer size) {
        if (size <= 0) {
            return "没有这样的尺寸";
        }
        if (isQqImageSize(size)) {
            return qqService.getQqImageForJson(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return "没有这样的尺寸";
    }

    @ApiOperation("返回JSON的QQ空间头像地址")
    @GetMapping("qz/json")
    @CrossOrigin
    public String getQzImageForJson(String qq) {
        return qqService.getQzImageForJson(qq);
    }

    @ApiOperation("返回加密的QQ头像地址")
    @GetMapping("qq/json/encrypt")
    @PostMapping("qq/json/encrypt")
    @CrossOrigin
    public String getQqImageForJsonEncrypt(String qq, @RequestParam(required = false, defaultValue = "100") Integer size) {
        if (size <= 0) {
            return "没有这样的尺寸";
        }
        if (isQqImageSize(size)) {
            return qqService.getQqImageForEncrypt(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return "没有这样的尺寸";
    }

    @ApiOperation("返回加密的QQ头像")
    @GetMapping(value = "qq/encrypt", produces = MediaType.IMAGE_JPEG_VALUE)
    @PostMapping(value = "qq/encrypt", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQqImageForEncrypt(String qq, @RequestParam(required = false, defaultValue = "100") Integer size) {
        if (size <= 0) {
            return null;
        }
        if (isQqImageSize(size)) {
            try {
                return ImageIO.read(new URL(qqService.getQqImageForEncrypt(qq, size)));
            } catch (IOException e) {
                log.error("获取QQ头像失败(加密版)");
            }
        }
        log.debug("非法参数值 size:" + size);
        return null;
    }

    private boolean isQqImageSize(Integer size) {
        return QQSizeEnum.SMALL.getSize().equals(size) || QQSizeEnum.ZHONG_SMALL.getSize().equals(size) || QQSizeEnum.ZHONG_BIG.getSize().equals(size) || QQSizeEnum.ZHONG.getSize().equals(size) || QQSizeEnum.BIG.getSize().equals(size);
    }

    private boolean isQzImageSize(Integer size) {
        return QZSizeEnum.SMALL.getSize().equals(size) || QZSizeEnum.ZHONG_SMALL.getSize().equals(size) || QZSizeEnum.ZHONG.getSize().equals(size) || QZSizeEnum.ZHONG_BIG.getSize().equals(size) || QZSizeEnum.BIG.getSize().equals(size);
    }
}