package com.bingchunmoli.api.qrcode.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.bingchunmoli.api.qrcode.exception.FileIsEmptyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author BingChunMoLi
 */
@RestController
public class QrCodeController {

    @GetMapping("decode")
    public String decode(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileIsEmptyException();
        }
        return QrCodeUtil.decode(file.getInputStream());
    }

    @GetMapping(value = "generate", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage generateQrCode(@RequestParam(defaultValue = "https://api.bingchunmoli.com") String text, @RequestParam(defaultValue = "300") Integer width, @RequestParam(defaultValue = "300") Integer height) {
        return QrCodeUtil.generate(text, width, height);
    }
}
