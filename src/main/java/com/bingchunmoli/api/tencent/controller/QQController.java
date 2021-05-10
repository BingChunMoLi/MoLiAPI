package com.bingchunmoli.api.tencent.controller;


import com.bingchunmoli.api.tencent.bean.enums.QQSizeEnum;
import com.bingchunmoli.api.tencent.bean.enums.QZSizeEnum;
import com.bingchunmoli.api.tencent.service.IQQService;
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
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description:
 * https://www.jianshu.com/p/42d0ada1c5cf
 * https://qqdie.com/archives/get-qq-avatar-no-qq-number.html
 * https://yaw.ee/1380.html
 * https://blog.csdn.net/qq_18298439/article/details/89315478
 * @Author 冰彦糖
 * @Data 2020/11/27 18:11
 * @ClassName QQController
 * @PackageName: com.bingchunmoli.api.controller
 * @Version 0.0.1-SNAPSHOT
 **/
@Slf4j
@RestController
@RequestMapping("tencent")
public class QQController {
    @Resource
    private IQQService qqService;
    @ApiOperation("返回QQ头像")
    @GetMapping(value = "qq",produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQQImage(String qq, @RequestParam(required = false,defaultValue = "140") Integer size){
        if (size <= 0){
            log.debug("非法参数值 负数");
            return null;
        }
        if (isQQImageSize(size)){
            return qqService.getQQImage(qq,size);
        }else {
            log.debug("非法参数值 size:" + size);
            return null;
        }
    }
    @ApiOperation("返回QQ空间头像")
    @GetMapping(value = "qz",produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQZImage(String qq, @RequestParam(required = false,defaultValue = "100") Integer size){
        if (size <= 0){
            return null;
        }
        if (isQZImageSize(size)){
            return qqService.getQZImage(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return null;
    }
    @ApiOperation("返回JSON的QQ头像地址,加密")
    @GetMapping("qq/json")
    @CrossOrigin
    public String getQQImageForJson(String qq, @RequestParam(required = false,defaultValue = "100") Integer size){
        if (size <= 0){
            return "没有这样的尺寸";
        }
        if (isQQImageSize(size)){
            return qqService.getQQImageForJson(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return "没有这样的尺寸";
    }
    @ApiOperation("返回JSON的QQ空间头像地址")
    @GetMapping("qz/json")
    @CrossOrigin
    public String getQZImageForJson(String qq){
        return qqService.getQZImageForJson(qq);
    }
    @ApiOperation("返回加密的QQ头像地址")
    @GetMapping("qq/json/encrypt")
    @PostMapping("qq/json/encrypt")
    @CrossOrigin
    public String getQQImageForJsonEncrypt(String qq, @RequestParam(required = false,defaultValue = "100") Integer size){
        if (size <= 0){
            return "没有这样的尺寸";
        }
        if (isQQImageSize(size)){
            return qqService.getQQImageForEncrypt(qq, size);
        }
        log.debug("非法参数值 size:" + size);
        return "没有这样的尺寸";
    }
    @ApiOperation("返回加密的QQ头像")
    @GetMapping(value = "qq/encrypt",produces = MediaType.IMAGE_JPEG_VALUE)
    @PostMapping(value = "qq/encrypt",produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public BufferedImage getQQImageForEncrypt(String qq, @RequestParam(required = false,defaultValue = "100") Integer size){
        if (size <= 0){
            return null;
        }
        if (isQQImageSize(size)){
            try {
                return ImageIO.read(new URL(qqService.getQQImageForEncrypt(qq, size)));
            } catch (IOException e) {
                log.error("获取QQ头像失败(加密版)");
            }
        }
        log.debug("非法参数值 size:" + size);
        return null;
    }
    private boolean isQQImageSize(Integer size){
        return QQSizeEnum.SMALL.getSize().equals(size) || QQSizeEnum.ZHONG_SMALL.getSize().equals(size) || QQSizeEnum.ZHONG_BIG.getSize().equals(size) || QQSizeEnum.ZHONG.getSize().equals(size) || QQSizeEnum.BIG.getSize().equals(size);
    }
    private boolean isQZImageSize(Integer size){
        return QZSizeEnum.SMALL.getSize().equals(size) || QZSizeEnum.ZHONG_SMALL.getSize().equals(size) || QZSizeEnum.ZHONG.getSize().equals(size) || QZSizeEnum.ZHONG_BIG.getSize().equals(size) || QZSizeEnum.BIG.getSize().equals(size);
    }
}