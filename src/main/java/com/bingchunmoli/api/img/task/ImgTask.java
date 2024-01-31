package com.bingchunmoli.api.img.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.app.DeviceService;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.img.service.ImgService;
import com.bingchunmoli.api.properties.ApiConfig;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.enums.AppMessageEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * 随即图定时任务 每月刷新
 *
 * @author BingChunMoLi
 */
@Component
@RequiredArgsConstructor
public class ImgTask {
    private final ApiConfig apiConfig;
    private final ImgService imgService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DeviceService deviceService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void saveImg() {
        List<Path> pcPath = null;
        List<Path> mobilePath = null;
        if (StrUtil.isNotBlank(apiConfig.getPcPath())) {
            try {
                pcPath = imgService.getImgListByFileSystem(apiConfig.getPcPath());
            } catch (IOException e) {
                applicationEventPublisher.publishEvent(new MessageEven(this, new AppMessage()
                        .setTitle("pc定时任务失败")
                        .setBody("异常信息:  " + e.getMessage())
                        .setTopic("api")
                        .setAppMessageEnum(AppMessageEnum.TOPIC)));
            }
            if (CollectionUtil.isNotEmpty(pcPath)) {
                redisTemplate.opsForList().trim(ApiConstant.PC_IMG, -2, -1);
                redisTemplate.opsForList().leftPushAll(ApiConstant.PC_IMG, pcPath.stream().map(Path::toString).toArray());
            }
        }
        if (StrUtil.isNotBlank(apiConfig.getMobilePath())) {
            try {
                mobilePath = imgService.getImgListByFileSystem(apiConfig.getMobilePath());
            } catch (IOException e) {
                applicationEventPublisher.publishEvent(new MessageEven(this, new AppMessage()
                        .setTitle("mobile定时任务失败")
                        .setBody("异常信息:  " + e.getMessage())
                        .setTopic("api")
                        .setAppMessageEnum(AppMessageEnum.TOPIC)));
            }
            if (CollectionUtil.isNotEmpty(mobilePath)) {
                redisTemplate.opsForList().trim(ApiConstant.MOBILE_IMG, -1, -2);
                redisTemplate.opsForList().leftPushAll(ApiConstant.MOBILE_IMG, mobilePath.stream().map(Path::toString).toArray());
            }
        }
        String body = "更新PC图片:" +
                (pcPath == null ? 0 : pcPath.size()) +
                "  更新移动图片:" +
                (mobilePath == null ? 0 : mobilePath.size());
        AppMessage appMessage = new AppMessage()
                .setTitle("随机图定时任务 更新成功")
                .setBody(body)
                .setAppMessageEnum(AppMessageEnum.TOPIC);
        deviceService.getDefaultToken().ifPresentOrElse(v -> appMessage.setDeviceToken(v), () -> appMessage.setDefaultTopic());
        applicationEventPublisher.publishEvent(new MessageEven(this, appMessage));
    }
}
