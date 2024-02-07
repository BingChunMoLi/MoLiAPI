package com.bingchunmoli.api.down.service.impl;

import com.bingchunmoli.api.down.bean.MediaPO;
import com.bingchunmoli.api.down.config.SystemConfig;
import com.bingchunmoli.api.down.service.DownloadService;
import com.bingchunmoli.api.down.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author bingchunmoli
 */
@Slf4j
@Service
public class YouGetDownloadServiceImpl implements DownloadService {

    @Override
    public Boolean downloadMedia(MediaPO media) throws IOException, InterruptedException {
        log.info("下载开始 media: {}:", media);
        return ShellUtil.exec(SystemConfig.getYouGetPath(), "-o", SystemConfig.getUploadPath(), "https://www.bilibili.com/video/av" + media.getId());
    }

}
