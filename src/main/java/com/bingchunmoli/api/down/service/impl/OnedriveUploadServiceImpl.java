package com.bingchunmoli.api.down.service.impl;


import com.bingchunmoli.api.down.config.SystemConfig;
import com.bingchunmoli.api.down.service.UploadService;
import com.bingchunmoli.api.down.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Service
public class OnedriveUploadServiceImpl implements UploadService {
    @Override
    public Boolean upload(String filePath) throws IOException, InterruptedException {
//            ShellUtil.exec("sh", "-c", SystemConfig.getRclonePath(), "copy", "onedrive:" + SystemConfig.getDownloadPath() + filePath, SystemConfig.getUploadPath() + filePath, ">", "/usr/bili/rclone.log", "2>&1", "&");
        log.info("{},{},{},{}", SystemConfig.getRclonePath(), "copy", SystemConfig.getDownloadPath() + filePath, "onedrive:" + SystemConfig.getUploadPath() + filePath);
        String[] args = {SystemConfig.getRclonePath(), "copy", SystemConfig.getDownloadPath() + filePath, "onedrive:" + SystemConfig.getUploadPath() + filePath};
        ShellUtil.exec(SystemConfig.getRclonePath(), "copy", SystemConfig.getDownloadPath() + filePath, "onedrive:" + SystemConfig.getUploadPath() + filePath);
        log.debug("OnedriveUploadServiceImpl args: {}", Arrays.toString(args));
        return true;
    }
}
