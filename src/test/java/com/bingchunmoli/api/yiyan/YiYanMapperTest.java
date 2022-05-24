package com.bingchunmoli.api.yiyan;

import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@Slf4j
@SpringBootTest
public class YiYanMapperTest {
    @Autowired
    private YiYanMapper yiYanMapper;

    @Test
    void testContext(){
        YiYan random = yiYanMapper.findRandom();
        log.info("yiYanRandom: {}",random);
        Assert.notNull(random, "yiYanMapper.findRandom is Null");
    }
}
