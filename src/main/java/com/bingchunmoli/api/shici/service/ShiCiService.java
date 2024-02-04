package com.bingchunmoli.api.shici.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.shici.bean.ShiCi;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
public interface ShiCiService extends IService<ShiCi> {
    /**
     * 从缓存中查出随即一条诗词，如果没有从数据库查询
     *
     * @return 一条诗词数据
     */
    ShiCi findRandomShiCi();

    /**
     * 初始化service中的shiciList
     */
    void setShiCiList(List<ShiCi> list);
}
