package com.bingchunmoli.api.yiyan;

import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import java.util.Objects;

@Slf4j
@Rollback
@SpringBootTest
public class YiYanMapperTest {
    @Autowired
    private YiYanMapper yiYanMapper;

    @Test
    void testContext(){
        YiYan random = yiYanMapper.findRandom();
        YiYan random2 = yiYanMapper.findRandom();
        Assert.notNull(random, "一言基础数据应该已初始化, 不能查询为空");
        Assert.isTrue(!Objects.equals(random, random2), "random接口未随机");
    }
}
