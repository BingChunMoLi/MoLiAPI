package com.bingchunmoli.api.img.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author BingChunMoLi
 */
public interface ImgService {

    /**
     * 随机PC随机图
     * @return 随机图PC
     */
    @Deprecated
    BufferedImage getRandomImageByPc();

    /**
     * 获取随机移动随机图
     * @return 随机图mobile
     */
    @Deprecated
    BufferedImage getRandomImageMobile();

    /**
     * 获取随机图从文件系统
     * @param path 文件系统路径
     * @return 文件对象
     * @throws IOException 可能触发IO异常
     */
    File getRandomImgByFileSystem(String path) throws IOException;

    /**
     * 获取随机图从redis
     * @param key redisKey
     * @return 文件对象
     */
    File getRandomImgByRedis(String key);

    /**
     * 获取imgList从文件系统
     * @param path 路径
     * @return Path列表
     * @throws IOException IO异常
     */
    List<Path> getImgListByFileSystem(String path) throws IOException;
}
