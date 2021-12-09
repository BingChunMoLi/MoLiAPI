package com.bingchunmoli.api.tencent.service;

import java.awt.image.BufferedImage;

/**
 * @author bingchunmoli
 **/
public interface IQqService {
    BufferedImage getQqImage(String qq, Integer size);

    BufferedImage getQzImage(String qq, Integer size);

    String getQqImageForJson(String qq, Integer size);

    String getQzImageForJson(String qq);

    String getQqImageForEncrypt(String qq, Integer size);
}