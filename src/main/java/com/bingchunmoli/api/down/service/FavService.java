package com.bingchunmoli.api.down.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.down.bean.FavPO;

/**
 * @author BingChunMoLi
 */
public interface FavService extends IService<FavPO> {

    /**
     * 保存未被保存的收藏夹
     *
     * @param saveFav 需要判断的收藏夹
     */
    void saveByFilterAlreadyExists(FavPO saveFav);
}
