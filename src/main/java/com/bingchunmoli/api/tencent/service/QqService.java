package com.bingchunmoli.api.tencent.service;

import java.awt.image.BufferedImage;

/**
 * @author bingchunmoli
 **/
public interface QqService {

    /**
     * 获取qq头像
     */
    BufferedImage getQqImage(String qq, Integer size);

    /**
     * 获取qq空间头像
     */
    BufferedImage getQzImage(String qq, Integer size);

    /**
     * 获取qq空间头像链接
     */
    String getQzImageForJson(String qq);
}
