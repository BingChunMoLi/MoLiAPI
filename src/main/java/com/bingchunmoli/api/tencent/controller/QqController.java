package com.bingchunmoli.api.tencent.controller;


import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.tencent.bean.enums.QQSizeEnum;
import com.bingchunmoli.api.tencent.bean.enums.QZSizeEnum;
import com.bingchunmoli.api.tencent.service.QqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Tag(name = "qq头像和qq空间头像")
public class QqController {
    private final QqService qqService;

    /**
     * 返回QQ头像
     * @param qq qq号码|3239720020
     * @param size 大小(默认140)|140
     * @return qq头像
     */
    @GetMapping(value = "qq", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "根据qq号获取qq头像图片")
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
    @GetMapping(value = "qz", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "根据qq获取qq空间头像")
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
    @GetMapping("qq/json")
    @Operation(summary = "通过qq号通过加密形式获取qq头像地址")
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
    @GetMapping("qz/json")
    @Operation(summary = "根据qq获取qq空间头像的json链接形式")
    public String getQzImageForJson(String qq) {
        return qqService.getQzImageForJson(qq);
    }

    /**
     * 加密的qq头像地址
     * @param qq qq号码|3239720020
     * @param size 大小(默认100)|100
     * @return qq头像地址
     */
    @Operation(summary = "根据qq号获取加密的qq头像地址,推荐使用post")
    @RequestMapping(value = "qq/json/encrypt", method = {RequestMethod.GET, RequestMethod.POST})
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
    @Operation(summary = "根据qq号加密获取qq头像,推荐使用post")
    @RequestMapping(value = "qq/encrypt", produces = MediaType.IMAGE_JPEG_VALUE, method = {RequestMethod.GET, RequestMethod.POST})
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