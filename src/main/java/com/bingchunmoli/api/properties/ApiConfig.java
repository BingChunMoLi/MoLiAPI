package com.bingchunmoli.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ApiConfig
 * @author bingchunmoli
 */
@Data
@Component
@ConfigurationProperties("moli.api-config")
public class ApiConfig {
    /**
     * 和风天气key
     */
    private String weatherKey;
    /**
     * 和风天气请求uri
     */
    private String weatherUri = "devapi.qweather.com";
    /**
     * 和风天气请求地区ApiUri
     */
    private String weatherGeoUri = "geoapi.qweather.com";
    /**
     * server酱key
     */
    private String serverSauceKey;

    /**
     * pc图片路径
     */
    private String pcPath;
    /**
     * mobile图片路径
     */
    private String mobilePath;
    /**
     * 1080p
     */
    private String path1080;

    /**
     * 上传临时文件的路径
     */
    private String uploadTempPath;

    /**
     * 上传临时文件的密钥
     */
    private String uploadTempSecret;
    /**
     * 证书路径(为腾讯CDN自动更新)
     */
    private String certificatePath;
    /**
     * 私钥路径(为腾讯CDN自动更新)
     */
    private String privateKeyPath;
    /**
     * 域名
     */
    private String domain;

    /**
     * 定时任务歌单id
     */
    private List<String> playListId;

    /**
     * 歌单用的cookies，
     */
    private String cookies;
}
