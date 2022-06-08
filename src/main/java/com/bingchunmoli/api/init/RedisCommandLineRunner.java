package com.bingchunmoli.api.init;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.bean.enums.ProfileEnum;
import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.img.task.ImgTask;
import com.bingchunmoli.api.shici.service.IShiCiService;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${spring.profiles.active}")
    String profile;
    @Override
    public void run(String... args) {
        Long yiYanLen = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (yiYanLen == null || yiYanLen == 0) {
            redisTemplate.opsForList().leftPushAll(ApiConstant.YI_YAN, yiYanService.list().toArray());
        }
        Long shiCiLen = redisTemplate.opsForList().size(ApiConstant.SHI_CI);
        if (shiCiLen == null || shiCiLen == 0) {
            redisTemplate.opsForList().leftPushAll(ApiConstant.SHI_CI, shiCiService.list().toArray());
        }
        if (! ProfileEnum.DEV.getProfile().equalsIgnoreCase(profile)) {
            imgTask.saveImg();
        }
        applicationEventPublisher.publishEvent(new MailMessageEven(MailMessage.builder().title("系统初始化完成").body("系统初始化完成, 初始化当前时间:" + LocalDateTime.now()).build()));
    }
}
