package com.bingchunmoli.api.bing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.bing.bean.BingImage;

import java.time.LocalDate;

/**
 * @author BingChunMoLi
 */
public interface BingImageMapper extends BaseMapper<BingImage> {

    /**
     * 获取当天的数据Id
     * @param date 时间
     * @return id
     */
    Long getIdByCreateDate(LocalDate date);
}
