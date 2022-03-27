package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.enums.ProfileEnum;
import com.bingchunmoli.api.img.task.ImgTask;
import com.bingchunmoli.api.shici.service.IShiCiService;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
@ConditionalOnBean(value = {IYiYanService.class, IShiCiService.class, RedisTemplate.class})
public class RedisCommandLineRunner implements CommandLineRunner {
    private final IYiYanService yiYanService;
    private final IShiCiService shiCiService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ImgTask imgTask;
    @Value("${spring.profiles.active}")
    String profile;

    @Override
    public void run(String... args) {
        Long yiYanLen = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (yiYanLen == null || yiYanLen == 0) {
            log.info("yiYan数据初始化中");
            redisTemplate.opsForList().leftPushAll(ApiConstant.YI_YAN, yiYanService.list().toArray());
            log.info("yiYan初始化完成");
        } else {
            log.info("yiYan数据不需要初始化");
        }
        Long shiCiLen = redisTemplate.opsForList().size(ApiConstant.SHI_CI);
        if (shiCiLen == null || shiCiLen == 0) {
            log.info("shiCi数据初始化中");
            redisTemplate.opsForList().leftPushAll(ApiConstant.SHI_CI, shiCiService.list().toArray());
            log.info("shiCi初始化完成");
        } else {
            log.info("shiCi数据不需要初始化");
        }
        if (ProfileEnum.DEV.getProfile().equalsIgnoreCase(profile)) {
            log.info("dev环境，不初始化随即图");
        }else {
            imgTask.saveImg();
            log.info("随机图初始化完成");
        }
    }
}
