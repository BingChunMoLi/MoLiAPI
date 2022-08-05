package com.bingchunmoli.api.bing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author BingChunMoLi
 */
public interface IBingService extends IService<BingImage> {
    /**
     * 获取bing图片
     *
     * @return BingImage
     * @throws JsonProcessingException JSON转换异常
     */
    BingImage getAllBingImage() throws JsonProcessingException;

    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    String getRandomImg();

    /**
     * 通过选定枚举获取具体的BingImage
     * @param bingEnum 枚举仅限CN|EN
     * @return 具体的bing图片
     * @throws JsonProcessingException JSON转换异常
     */
    BingImageVO getBingImage(BingEnum bingEnum) throws JsonProcessingException;

    /**
     * 获取远程BingImage根据枚举获取地区的
     * @param bingEnum 枚举仅限CN|EN
     * @return BingImageVO
     * @throws JsonProcessingException JSON转换异常
     */
    BingImageVO getBingImageByRemote(BingEnum bingEnum) throws JsonProcessingException;

    /**
     * 获取两种远程BingImage
     * @return bingBingImageVO
     * @throws JsonProcessingException JSON转换异常
     */
    BingImage getBingImageByRemote() throws JsonProcessingException;
}
