package com.bingchunmoli.api.yiyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.yiyan.bean.YiYan;

/**
 * @author BingChunMoLi
 */
public interface IYiYanService extends IService<YiYan> {
    String findRandomYiYan();
}
