package com.bingchunmoli.api.down.service.impl;

import com.bingchunmoli.api.down.config.SystemConfig;
import com.bingchunmoli.api.down.service.UploadService;
import com.bingchunmoli.api.down.util.ShellUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author BingChunMoLi
 */
@Slf4j
@Service
public class BaiduUploadServiceImpl implements UploadService {

    @Override
    public Boolean upload(String filePath) throws IOException, InterruptedException {
        log.info("{},{},{},{}", SystemConfig.getBaiduPCSPath(), "upload", SystemConfig.getDownloadPath() + filePath, SystemConfig.getUploadPath() + filePath);
        String[] args = {SystemConfig.getBaiduPCSPath(), "upload", SystemConfig.getDownloadPath() + filePath, SystemConfig.getUploadPath() + filePath};
        ShellUtil.exec(SystemConfig.getBaiduPCSPath(), "upload", SystemConfig.getDownloadPath() + filePath, SystemConfig.getUploadPath() + filePath);
        log.debug("BaiduUploadServiceImpl args: {}", args);
        return true;
    }
}
