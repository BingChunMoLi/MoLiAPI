package com.bingchunmoli.api.down.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.down.bean.Media;
import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.bean.UserPO;
import com.bingchunmoli.api.down.mapper.MediaDao;
import com.bingchunmoli.api.down.service.BiliUserService;
import com.bingchunmoli.api.down.service.MediaService;
import com.bingchunmoli.api.down.util.BiliUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.bingchunmoli.api.down.bean.constant.Constants.SPECIAL_CHAR;


/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaDao, MediaPO> implements MediaService {

    private final BiliUserService biliUserService;
    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByFilterAlreadyExists(Collection<MediaPO> mediaList) {
        List<Integer> saveMediaIdList = mediaList.stream().map(MediaPO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<MediaPO> queryWrapper = new LambdaQueryWrapper<MediaPO>().select(MediaPO::getId).in(MediaPO::getId, saveMediaIdList);
        List<MediaPO> mediaPoList = list(queryWrapper);
        List<Integer> mediaPoIdList = mediaPoList.stream().map(MediaPO::getId).toList();
        List<MediaPO> needSaveUserList = mediaList.stream().filter(v -> !mediaPoIdList.contains(v.getId())).collect(Collectors.toList());
        needSaveUserList.forEach(v -> {
            String title = v.getTitle();
            for (char c1 : SPECIAL_CHAR) {
                title = StrUtil.replace(title, String.valueOf(c1), ".");
            }
            v.setTitle(title);
        });
        saveBatch(needSaveUserList);
        if ("dev".equalsIgnoreCase(profile)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<MediaPO> getNeedDownloadMediaList() {
        return list(new LambdaQueryWrapper<MediaPO>().eq(MediaPO::getDown, 0));
    }

    @Override
    public String downloadOne(String av, int onlyAudio) throws IOException {
        Media media = BiliUtil.getMediaDetail(av);
        Media.MediaDataDTO source = media.getData();
        MediaPO build = covertMediaPO(source);
        build.setOnlyAudio(onlyAudio);
        UserPO user = covertUserPO(source);
        ((MediaServiceImpl) AopContext.currentProxy()).saveByFilterAlreadyExists(Collections.singletonList(build));
        biliUserService.saveByFilterAlreadyExists(Collections.singletonList(user));
//        rocketMessageUtil.sendObject(build);
        return "提交成功";
    }

    /**
     * 转换MediaDataDTO为创建者用户
     *
     * @param source UserDetail接口的数据
     * @return 用户PO
     */
    private UserPO covertUserPO(Media.MediaDataDTO source) {
        Media.MediaDataDTO.OwnerDTO owner = source.getOwner();
        return UserPO.builder().id(owner.getMid())
                .name(owner.getName())
                .face(owner.getFace())
                .build();
    }

    /**
     * 转换MediaDataDTO为MediaPO
     *
     * @param source UserDetail接口的数据
     * @return MediaPO
     */
    private MediaPO covertMediaPO(Media.MediaDataDTO source) {
        return MediaPO.builder()
                .bvId(source.getBvid())
                .cover(source.getPic())
                .ctime(source.getCtime())
                .down(0)
                .favTime(null)
                .id(source.getAid())
                .intro(source.getDesc())
                .mid(source.getOwner().getMid())
                .invalidPush(0)
                .onedriveUpload(0)
                .pubtime(source.getPubdate())
                .title(source.getTitle())
                .page(source.getVideos())
                .upload(0)
                .type(null)
                .build();
    }

}
