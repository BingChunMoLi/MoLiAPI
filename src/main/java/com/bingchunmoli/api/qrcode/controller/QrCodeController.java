package com.bingchunmoli.api.qrcode.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.bingchunmoli.api.exception.ApiFileIsEmptyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 二维码
 * @author BingChunMoLi
 */
@RestController
public class QrCodeController {

    /**
     * 文件解码
     * @param file 文件
     * @return 解码后的字符|https://api.bingchunmoli.com
     * @throws IOException 文件IO异常
     */
    @GetMapping("decode")
    public String decode(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ApiFileIsEmptyException();
        }
        return QrCodeUtil.decode(file.getInputStream());
    }

    /**
     * 生成二维码
     * @param text 生成的数据|https://api.bingchunmoli.com
     * @param width 宽度(默认300)|300
     * @param height 高度(默认300)|300
     * @return 二维码图片
     */
    @GetMapping(value = "generate", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage generateQrCode(@RequestParam(defaultValue = "https://api.bingchunmoli.com") String text, @RequestParam(defaultValue = "300") Integer width, @RequestParam(defaultValue = "300") Integer height) {
        return QrCodeUtil.generate(text, width, height);
    }
}
