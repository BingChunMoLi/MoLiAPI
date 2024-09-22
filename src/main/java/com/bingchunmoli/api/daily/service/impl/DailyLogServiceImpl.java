package com.bingchunmoli.api.daily.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;
import com.bingchunmoli.api.daily.mapper.DailyLogMapper;
import com.bingchunmoli.api.daily.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author MoLi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyLogServiceImpl extends ServiceImpl<DailyLogMapper, DailyLogPO> implements DailyLogService {

    @Override
    public DailyQuery getQueryParam(String url, String tenant) {
        return baseMapper.getStartAndEndTime(url, (short) ("moli".equalsIgnoreCase(tenant) ? 1 : 0));
    }

    @Override
    public Map<LocalDate, List<DailyLog>> querySign(DailyQuery dailyQuery, int tenant) {
        List<DailyLogPO> dailyLogs = baseMapper.querySign(dailyQuery, tenant);
        return dailyLogs.stream()
                .map(v -> DailyLog.builder()
                        .id(v.getId())
                        .url(v.getUrl())
                        .type(v.getType())
                        .tenant(v.getTenant())
                        .createTime(v.getCreateTime().toLocalDate())
                        .build()
                ).collect(Collectors.groupingBy(DailyLog::getCreateTime));
    }
}
