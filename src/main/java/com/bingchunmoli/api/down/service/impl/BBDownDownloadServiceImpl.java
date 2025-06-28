package com.bingchunmoli.api.down.service.impl;

import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.bean.constant.OnlyAudioEnum;
import com.bingchunmoli.api.down.config.SystemConfig;
import com.bingchunmoli.api.down.service.DownloadService;
import com.bingchunmoli.api.down.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Service
public class BBDownDownloadServiceImpl implements DownloadService {

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public Boolean downloadMedia(MediaPO media) throws IOException, InterruptedException {
        log.info("下载开始 media: {}:", media);
//        String[] args = {"sh", "-c", SystemConfig.getBBDownPath() + " --work-dir " + SystemConfig.getDownloadPath() + " https://www.bilibili.com/video/av" + av + " > /usr/bili/BBDown.log 2>&1 &"};
        String[] args;
        if (OnlyAudioEnum.TRUE.getOnlyAudio() == media.getOnlyAudio()) {
            args = new String[]{"sh", "-c", SystemConfig.getBBDownPath() + " --audio-only --work-dir " + SystemConfig.getDownloadAudioPath() + " https://www.bilibili.com/video/av" + media.getId()};
        } else {
            args = new String[]{"sh", "-c", SystemConfig.getBBDownPath() + " --work-dir " + SystemConfig.getDownloadPath() + " https://www.bilibili.com/video/av" + media.getId()};
        }
        log.debug("BBDownDownloadServiceImpl args: {}", Arrays.toString(args));
        return ShellUtil.exec(args);
    }
}
