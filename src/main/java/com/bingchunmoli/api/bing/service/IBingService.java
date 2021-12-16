package com.bingchunmoli.api.bing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;

/**
 * @author BingChunMoLi
 */
public interface IBingService extends IService<BingImage> {
    /**
     * 获取bing图片
     *
     * @return BingImage
     */
    BingImage getAllBingImage();

    /**
     * 获取国内版Bing每日美图
     *
     * @return 国内版Bing美图
     */
    BingImageVO getCnBingImage();

    /**
     * 获取国际版Bing美图美图
     *
     * @return 国际版Bing美图
     */
    BingImageVO getEnBingImage();
}
