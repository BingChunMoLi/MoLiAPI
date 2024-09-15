package com.bingchunmoli.api.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;

import java.util.List;

/**
* @author MoLi
*/
public interface DailyLogMapper extends BaseMapper<DailyLogPO> {

    DailyQuery getStartAndEndTime(String url, Short tenant);

    List<DailyLog> querySign(DailyQuery dailyQuery, int tenant);
}




