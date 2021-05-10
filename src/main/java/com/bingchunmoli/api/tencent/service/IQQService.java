package com.bingchunmoli.api.tencent.service;

import java.awt.image.BufferedImage;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO
 * @Author 冰彦糖
 * @Data 2020/11/27 18:12
 * @ClassName IQQService
 * @PackageName: com.bingchunmoli.api.service
 * @Version 0.0.1-SNAPSHOT
 **/
public interface IQQService {
    public BufferedImage getQQImage(String qq, Integer size);
    public BufferedImage getQZImage(String qq, Integer size);
    public String getQQImageForJson(String qq, Integer size);
    public String getQZImageForJson(String qq);
    public String getQQImageForEncrypt(String qq, Integer size);
}