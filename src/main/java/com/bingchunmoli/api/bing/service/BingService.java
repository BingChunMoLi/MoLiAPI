package com.bingchunmoli.api.bing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;

import java.time.LocalDate;

/**
 * @author BingChunMoLi
 */
public interface BingService extends IService<BingImage> {
    /**
     * 获取bing图片
     *
     * @return BingImage
     */
    BingImage getAllBingImage();

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
     */
    BingImageVO getBingImage(BingEnum bingEnum);

    /**
     * 获取远程BingImage根据枚举获取地区的
     * @param bingEnum 枚举仅限CN|EN
     * @return BingImageVO
     */
    BingImageVO getBingImageByRemote(BingEnum bingEnum);

    /**
     * 获取两种远程BingImage
     * @return bingBingImageVO
     */
    BingImage getBingImageByRemote();

    /**
     * 根据日期获取bingImage
     * @param date
     * @return
     */
    BingImage getBingImageByDate(LocalDate date);
}
