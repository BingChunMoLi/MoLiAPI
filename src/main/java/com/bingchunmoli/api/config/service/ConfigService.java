package com.bingchunmoli.api.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.config.bean.Config;

/**
 * @author MoLi
 */
public interface ConfigService extends IService<Config> {

    void updateApiConfig(ApiConfig apiConfig);
}
