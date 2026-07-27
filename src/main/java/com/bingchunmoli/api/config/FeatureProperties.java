package com.bingchunmoli.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("moli.features")
public class FeatureProperties {
    private boolean startupInitialization;
    private boolean yiYanPreload;
    private boolean firebase;
    private boolean scheduling;
    private final Tasks tasks = new Tasks();

    @Getter
    @Setter
    public static class Tasks {
        private boolean bing;
        private boolean weatherNotification;
        private boolean daily;
        private boolean image;
        private boolean host;
        private boolean bilibili;
        private boolean neteaseMusic;
        private boolean tencentCdnCertificate;
    }
}
