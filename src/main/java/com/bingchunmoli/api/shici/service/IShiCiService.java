package com.bingchunmoli.api.shici.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.shici.bean.ShiCi;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
public interface IShiCiService extends IService<ShiCi> {
    ShiCi findRandomShiCi();
}
