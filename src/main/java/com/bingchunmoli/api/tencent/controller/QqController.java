package com.bingchunmoli.api.tencent.controller;


import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.tencent.bean.enums.QQSizeEnum;
import com.bingchunmoli.api.tencent.bean.enums.QZSizeEnum;
import com.bingchunmoli.api.tencent.service.IQqService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


/**
 * qq、qz头像
 * @author bingchunmoli
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("tencent")
public class QqController {
    private final IQqService qqService;

    /**
     * 返回QQ头像
     * @param qq qq号码|3239720020
     * @param size 大小(默认140)|140
     * @return qq头像
     */
    @ApiOperation("返回QQ头像")
    @GetMapping(value = "qq", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage getQqImage(String qq, @RequestParam(required = false, defaultValue = "140") Integer size) {
        if (size <= 0) {
            log.debug("非法参数值 负数");
            throw new ApiParamException("非法参数值 负数");
        }
        if (isQqImageSize(size)) {
            return qqService.getQqImage(qq, size);
        } else {
            log.debug("非法参数值 size:" + size);
            return null;
        }
    }

    /**
     * 返回qq空间头像
     * @param qq qq号码|3239720020
     * @param size 大小(默认100)|100
     * @return QQ空间头像
     */
    @ApiOperation("返回QQ空间头像")
    @GetMapping(value = "qz", produces = MediaType.IMAGE_JPEG_VALUE)
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

    /**
     * 加密形式获取qq头像地址
     * @param qq qq号码|3239720020
     * @param size 大小(默认100)|100
     * @return QQ空间头像
     */
    @ApiOperation("返回JSON的QQ头像地址,加密")
    @GetMapping("qq/json")
    public String getQqImageForJson(String qq, @RequestParam(required = false, defaultValue = "v") Integer size) {
        if (size <= 0) {
            return "没有这样的尺寸";
        }
        if (isQqImageSize(size)) {
            return qqService.getQqImageForJson(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return "没有这样的尺寸";
    }

    /**
     * QQ空间头像json形式
     * @param qq qq号码|3239720020
     * @return json
     */
    @ApiOperation("返回JSON的QQ空间头像地址")
    @GetMapping("qz/json")
    public String getQzImageForJson(String qq) {
        return qqService.getQzImageForJson(qq);
    }

    /**
     * 加密的qq头像地址
     * @param qq qq号码|3239720020
     * @param size 大小(默认100)|100
     * @return qq头像地址
     */
    @ApiOperation("返回加密的QQ头像地址")
    @GetMapping("qq/json/encrypt")
    @PostMapping("qq/json/encrypt")
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

    /**
     * 通过加密接口返回qq头像
     * @param qq qq号码|3239720020
     * @param size 大小(默认100)|100
     * @return qq头像
     */
    @ApiOperation("返回加密的QQ头像")
    @GetMapping(value = "qq/encrypt", produces = MediaType.IMAGE_JPEG_VALUE)
    @PostMapping(value = "qq/encrypt", produces = MediaType.IMAGE_JPEG_VALUE)
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