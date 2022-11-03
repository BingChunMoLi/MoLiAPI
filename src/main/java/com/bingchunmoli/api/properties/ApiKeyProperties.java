package com.bingchunmoli.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bingchunmoli
 */
@Data
@Component
@ConfigurationProperties("moli.api-config")
public class ApiKeyProperties {
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

}
