package com.bingchunmoli.api.daily.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
* @author MoLi
*/
public interface DailyLogService extends IService<DailyLogPO> {


    /**
     * 获取查询的请求参数, 比如开始时间与结束时间，以及可以查询的url
     *
     * @param url    可以根据url查询出最早的开始时间与最晚的结束时间
     * @param tenant 租户
     * @return 查询参数
     */
    DailyQuery getQueryParam(String url, String tenant);

    Map<LocalDate, List<DailyLog>> querySign(DailyQuery dailyQuery, int tenant);
}
