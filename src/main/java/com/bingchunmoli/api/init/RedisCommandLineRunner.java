package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.shici.service.IShiCiService;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Component
public class RedisCommandLineRunner implements CommandLineRunner {

    @Autowired
    IYiYanService yiYanService;
    @Autowired
    IShiCiService shiCiService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        Long yiYanLen = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (yiYanLen == null || yiYanLen == 0) {
            log.info("yiYan数据初始化中");
            yiYanService.list().forEach((yiYan) -> redisTemplate.opsForList().leftPush(ApiConstant.YI_YAN, yiYan));
            log.info("yiYan初始化完成");
        }else {
            log.info("yiYan数据不需要初始化");
        }
        Long shiCiLen = redisTemplate.opsForList().size(ApiConstant.SHI_CI);
        if (shiCiLen == null || shiCiLen == 0) {
            log.info("shiCi数据初始化中");
            shiCiService.list().forEach((shiCi -> redisTemplate.opsForList().leftPush(ApiConstant.SHI_CI, shiCi)));
            log.info("shiCi初始化完成");
        }else {
            log.info("shiCi数据不需要初始化");
        }
    }
}
