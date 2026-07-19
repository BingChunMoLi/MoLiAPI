package com.bingchunmoli.api.bing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.bing.bean.BingImage;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @author BingChunMoLi
 */
public interface BingImageMapper extends BaseMapper<BingImage> {

    /**
     * 获取当天的数据Id
     * @param date 时间
     * @return id
     */
    Long getIdByCreateTimeRange(@Param("startTime") final LocalDateTime startTime,
                                @Param("endTime") final LocalDateTime endTime);
}
