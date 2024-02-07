package com.bingchunmoli.api.down.task;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bingchunmoli.api.down.bean.Fav;
import com.bingchunmoli.api.down.bean.FavPO;
import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.bean.UserPO;
import com.bingchunmoli.api.down.service.BiliUserService;
import com.bingchunmoli.api.down.service.FavService;
import com.bingchunmoli.api.down.service.MediaService;
import com.bingchunmoli.api.down.util.BiliUtil;
import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.push.bean.MailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshFav {

    private static final String FAV_ID = "56939296";
    private final MediaService mediaService;
    private final FavService favService;
    private final BiliUserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;
    @Value("${spring.profiles.active}")
    private String profile;
    @Value("${spring.mail.defaultTo:mailDefaultTo}")
    private String defaultTo;
    @Value("${spring.mail.username}")
    private String mailFrom;

    //    @Scheduled(cron = "* * 1 * * ?")
//    public void refreshRSS() {
//
//    }
    @Scheduled(cron = "0 0 0 1/3 * ?")
    public synchronized void refreshInterface() throws JsonProcessingException {
        //获取收藏
        Fav fav;
        try {
            fav = BiliUtil.getBiliFavBy(FAV_ID);
        } catch (IOException e) {
            if (log.isInfoEnabled()) {
                log.info("定时任务错误, 获取收藏夹失败.", e);
            }
            throw new RuntimeException(e);
        }
        // 转UserPO  MediaPO FavPO 对象 查库新增保存; 没有下载过已保存,没有保存过 -> 需要下载。
        // 委托 UserService处理UserPO
        // 委托 FavService处理FavPO
        // 委托 MediaService处理MediaPO (需要数据库数据判断是否存储)
        // 主要业务 下载处理media(需要数据库数据判断是否需要续传) 委托rocketmq 异步处理

        //解析信息
        FavPO saveFav = BiliUtil.favToFavParse(fav);
        Collection<UserPO> saveUserList = BiliUtil.favToUserListParse(fav);
        Collection<MediaPO> mediaList = BiliUtil.favToMediaListParse(fav);
        //委托service处理存储
        favService.saveByFilterAlreadyExists(saveFav);
        userService.saveByFilterAlreadyExists(saveUserList);
        mediaService.saveByFilterAlreadyExists(mediaList);

        List<MediaPO> needDownloadMediaList = mediaService.getNeedDownloadMediaList();
        log.info("需要下载的media: {}", needDownloadMediaList);

        if (CollectionUtil.isNotEmpty(mediaList)) {
            List<MediaPO> invalidPushMediaList = mediaList.stream().filter(v -> v.getTitle().contains("已失效视频")).toList();
            List<MediaPO> mediaPOList = mediaService.list(new LambdaQueryWrapper<MediaPO>().in(MediaPO::getId, invalidPushMediaList.stream().map(MediaPO::getId).collect(Collectors.toList())));
            List<MediaPO> needInvalidPushMediaList = mediaPOList.stream().filter(v -> v.getInvalidPush() == 0).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(needInvalidPushMediaList)) {
//                    invalidMessageBuilder.append("视频AV: ")
//                            .append(v.getId())
//                            .append("  BV: ")
//                            .append(v.getBvId())
//                            .append("   video av: [av链接]( https://www.bilibili.com/video/av")
//                            .append(v.getId())
//                            .append(")   video bv: [BV链接](https://www.bilibili.com/video/")
//                            .append(v.getBvId())
//                            .append("失效标题: ");
//                ServerSauce.send("发现已失效视频", "失效视频: " + JSON.toJSONString(needInvalidPushMediaList));
//                mailUtil.sendMail("发现已失效视频", "失效视频: " + JSON.toJSONString(needInvalidPushMediaList));
//                    ServerSauce.send("发现已失效视频", "视频AV: " + v.getId() + "  BV: " + v.getBvId() + "   video av: [av链接](" + "https://www.bilibili.com/video/av" + v.getId() + ")" + "   video bv: [BV链接](" + "https://www.bilibili.com/video/" + v.getBvId() + ") ,失效视频标题" + invalidMedia.getTitle() + " 失效视频对象:" + JSON.toJSONString(invalidMedia));
                for (MediaPO v : needInvalidPushMediaList) {
                    applicationEventPublisher.publishEvent(new MessageEven(this,
                            MailMessage.builder()
                                    .from(mailFrom)
                                    .to(defaultTo)
                                    .title("发现已失效视频")
                                    .body("视频AV: " + v.getId() + "  BV: " + v.getBvId() + "   video av: [av链接](" + "https://www.bilibili.com/video/av" + v.getId() + ")" + "   video bv: [BV链接](" + "https://www.bilibili.com/video/" + v.getBvId() + ") ,失效视频标题" + v.getTitle() + " 失效视频对象:" + objectMapper.writeValueAsString(v))
                                    .build()));
                }
                needInvalidPushMediaList.forEach(v -> v.setInvalidPush(1));
                mediaService.updateBatchById(needInvalidPushMediaList);
            }
        }


    }

}
