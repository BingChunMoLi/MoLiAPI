package com.bingchunmoli.api.down.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bingchunmoli
 */
@Component
@ConfigurationProperties("moli.downbili")
public class SystemConfig {
    /**
     * server酱key
     */
    private static String serverSauceKey;

    private static String BBDownPath;

    private static String downloadPath;

    private static String downloadAudioPath;

    private static String baiduPCSPath;

    private static String uploadPath;

    private static String rclonePath;

    private static String youGetPath;

    private static String utilPath;

    private static String proxy;

    public static String getDownloadAudioPath() {
        return downloadAudioPath;
    }

    public void setDownloadAudioPath(String downloadAudioPath) {
        SystemConfig.downloadAudioPath = downloadAudioPath;
    }

    public static String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        SystemConfig.proxy = proxy;
    }

    public static String getServerSauceKey() {
        return serverSauceKey;
    }

    public void setServerSauceKey(String serverSauceKey) {
        SystemConfig.serverSauceKey = serverSauceKey;
    }

    public static String getBBDownPath() {
        return BBDownPath;
    }

    public void setBBDownPath(String BBDownPath) {
        SystemConfig.BBDownPath = BBDownPath;
    }

    public static String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        SystemConfig.downloadPath = downloadPath;
    }

    public static String getBaiduPCSPath() {
        return baiduPCSPath;
    }

    public void setBaiduPCSPath(String baiduPCSPath) {
        SystemConfig.baiduPCSPath = baiduPCSPath;
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        SystemConfig.uploadPath = uploadPath;
    }

    public static String getRclonePath() {
        return rclonePath;
    }

    public void setRclonePath(String rclonePath) {
        SystemConfig.rclonePath = rclonePath;
    }

    public static String getYouGetPath() {
        return youGetPath;
    }

    public void setYouGetPath(String youGetPath) {
        SystemConfig.youGetPath = youGetPath;
    }

    public static String getUtilPath() {
        return utilPath;
    }


    public void setUtilPath(String utilPath) {
        SystemConfig.utilPath = utilPath;
    }


}
