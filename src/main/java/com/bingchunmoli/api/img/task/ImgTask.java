package com.bingchunmoli.api.img.task;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.img.service.IImgService;
import com.bingchunmoli.api.properties.ApiKeyProperties;
import com.bingchunmoli.api.utils.SendMailUtil;
import com.bingchunmoli.api.utils.ServerSauce;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author BingChunMoLi
 */
@Component
@RequiredArgsConstructor
public class ImgTask {
    private final ApiKeyProperties apiKeyProperties;
    private final IImgService imgService;
    private final ServerSauce serverSauce;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SendMailUtil sendMailUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void saveImg() {
        List<Path> pcPath = null;
        List<Path> mobilePath = null;
        try {
            pcPath = imgService.getImgListByFileSystem(apiKeyProperties.getPcPath());
        } catch (IOException e) {
            e.printStackTrace();
            serverSauce.send("pc定时任务失败", "异常信息:  " + e.getMessage() + "            ");
            applicationEventPublisher.publishEvent(new MailMessageEven(MailMessage.builder().title("pc定时任务失败").body("异常信息:  " + e.getMessage() + "            ").build()));
        }
        try {
            mobilePath = imgService.getImgListByFileSystem(apiKeyProperties.getMobilePath());
        } catch (IOException e) {
            e.printStackTrace();
            serverSauce.send("mobile定时任务失败", "异常信息:  " + e.getMessage() + "            ");
            applicationEventPublisher.publishEvent(new MailMessageEven(MailMessage.builder().title("mobile定时任务失败").body("异常信息:  " + e.getMessage() + "            ").build()));
        }
        assert pcPath != null;
        assert mobilePath != null;
        redisTemplate.opsForList().trim(ApiConstant.PC_IMG, - 2, - 1);
        redisTemplate.opsForList().leftPushAll(ApiConstant.PC_IMG, pcPath.stream().map(Path :: toString).toArray());
        redisTemplate.opsForList().trim(ApiConstant.MOBILE_IMG, - 1, - 2);
        redisTemplate.opsForList().leftPushAll(ApiConstant.MOBILE_IMG, mobilePath.stream().map(Path :: toString).toArray());
        applicationEventPublisher.publishEvent(new MailMessageEven(MailMessage.builder().title("随机图定时任务 更新成功").body("更新PC图片:" + pcPath.size() + "     更新移动图片:" + mobilePath.size()).build()));
    }
}
