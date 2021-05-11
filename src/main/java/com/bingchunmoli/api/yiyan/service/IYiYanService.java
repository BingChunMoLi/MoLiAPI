package com.bingchunmoli.api.yiyan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.yiyan.bean.YiYan;

/**
 * @author BingChunMoLi
 */
public interface IYiYanService extends IService<YiYan> {
    /**
     * 查询一条随机一言数据
     *
     * @return 一言对象
     */
    Object findRandomYiYan();
}
