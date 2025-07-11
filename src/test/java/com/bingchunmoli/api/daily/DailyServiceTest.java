package com.bingchunmoli.api.daily;

import com.bingchunmoli.api.daily.bean.DailyLog;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;
import com.bingchunmoli.api.daily.mapper.DailyLogMapper;
import com.bingchunmoli.api.daily.service.DailyLogService;
import com.bingchunmoli.api.daily.service.impl.DailyLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author moli
 */

@SpringBootTest
@ContextConfiguration(classes = {DailyLogService.class, DailyLogServiceImpl.class})
public class DailyServiceTest {

    @Autowired
    private DailyLogService dailyLogService;
    @MockitoBean
    private DailyLogMapper dailyLogMapper;

    @Test
    void querySignTest() {
        Mockito.when(dailyLogMapper.querySign(DailyQuery.builder().build(), 0))
                .thenReturn(List.of(
                        new DailyLogPO().setCreateTime(LocalDateTime.now()),
                        new DailyLogPO().setCreateTime(LocalDateTime.now()),
                        new DailyLogPO().setCreateTime(LocalDateTime.now().minusDays(1))));
        Map<LocalDate, List<DailyLog>> map = dailyLogService.querySign(DailyQuery.builder().build(), 0);
        Assert.notEmpty(map, "查询不为空");
        Assert.isTrue(map.size() == 2, "查询不正确");
        Assert.isTrue(map.get(LocalDate.now()).size() == 2, "查询不正确");
        Assert.isTrue(map.get(LocalDate.now().minusDays(1)).size() == 1, "查询不正确");
    }
}
