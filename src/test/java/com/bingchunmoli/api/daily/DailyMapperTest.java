package com.bingchunmoli.api.daily;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.bingchunmoli.api.daily.bean.DailyLogPO;
import com.bingchunmoli.api.daily.bean.DailyQuery;
import com.bingchunmoli.api.daily.mapper.DailyLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

/**
 * @author moli
 */
@Slf4j
@MybatisPlusTest
@TestPropertySource(properties = {"spring.sql.init.data-locations=classpath*:dailyData.sql", "spring.sql.init.schema-locations=classpath*:init/db/ddl.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DailyMapperTest {
    @Autowired
    private DailyLogMapper dailyLogMapper;

    @Test
    void getStartAndEndTimeTest() {
        getStartAndEndTimeAllUrlTest();
        String urlKey = "test1";
        getStartAndEndTimeUrlTest(urlKey);
    }

    private void getStartAndEndTimeUrlTest(String urlKey) {
        DailyQuery test1Query = dailyLogMapper.getStartAndEndTime(urlKey, (short) 1);
        Assert.isTrue(test1Query.getUrls().size() == 1, "daily数据查询异常");
        Assert.isTrue(urlKey.equals(test1Query.getUrls().get(0)), "daily数据查询异常");
        Assert.isTrue(LocalDate.now().minusDays(1).equals(test1Query.getStartDate()), "daily数据查询异常");
        Assert.isTrue(LocalDate.now().equals(test1Query.getEndDate()), "daily数据查询异常");
    }

    private void getStartAndEndTimeAllUrlTest() {
        DailyQuery allQuery = dailyLogMapper.getStartAndEndTime(null, (short) 1);
        Assert.isTrue(allQuery.getUrls().size() == 2, "daily数据查询异常");
        Assert.isTrue(LocalDate.now().minusDays(2).equals(allQuery.getStartDate()), "daily数据查询异常");
        Assert.isTrue(LocalDate.now().equals(allQuery.getEndDate()), "daily数据查询异常");
    }

    @Test
    void querySignTest() {
        querySignNoQueryTest();
        querySignByUrlTest();
        querySignByStartDateTest();
        querySignByEndDateTest();
    }


    private void querySignNoQueryTest() {
        DailyQuery query = DailyQuery.builder()
                .build();
        List<DailyLogPO> res = dailyLogMapper.querySign(query, 0);
        Assert.notEmpty(res, "dailyLog查询不应为空");
        Assert.isTrue(res.size() == 5, "dailyLog查询结果不正确");
    }

    private void querySignByUrlTest() {
        DailyQuery query = DailyQuery.builder()
                .urls(List.of("test3"))
                .build();
        List<DailyLogPO> res = dailyLogMapper.querySign(query, 0);
        Assert.notEmpty(res, "dailyLog查询不应为空");
        Assert.isTrue(res.size() == 4, "dailyLog查询结果不正确");
    }

    private void querySignByStartDateTest() {
        DailyQuery query = DailyQuery.builder()
                .startDate(LocalDate.now().minusDays(1))
                .build();
        List<DailyLogPO> res = dailyLogMapper.querySign(query, 0);
        Assert.notEmpty(res, "dailyLog查询不应为空");
        Assert.isTrue(res.size() == 4, "dailyLog查询结果不正确");
    }

    private void querySignByEndDateTest() {
        DailyQuery query = DailyQuery.builder()
                .endDate(LocalDate.now().minusDays(1))
                .build();
        List<DailyLogPO> res = dailyLogMapper.querySign(query, 0);
        Assert.notEmpty(res, "dailyLog查询不应为空");
        Assert.isTrue(res.size() == 1, "dailyLog查询结果不正确");
    }

}
