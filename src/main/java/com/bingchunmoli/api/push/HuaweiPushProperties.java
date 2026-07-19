package com.bingchunmoli.api.push;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("moli.push.huawei")
public class HuaweiPushProperties {
    private static final String DEFAULT_OAUTH_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
    private static final String DEFAULT_PUSH_URL = "https://push-api.cloud.huawei.com/v1/%s/messages:send";

    private Boolean enabled = false;
    private String clientId;
    private String clientSecret;
    private String oauthUrl = DEFAULT_OAUTH_URL;
    private String pushUrl = DEFAULT_PUSH_URL;

    public boolean isConfigured() {
        final boolean hasClientId = clientId != null && !clientId.isBlank();
        final boolean hasClientSecret = clientSecret != null && !clientSecret.isBlank();
        return Boolean.TRUE.equals(enabled) && hasClientId && hasClientSecret;
    }

    public String getSendUrl() {
        return String.format(pushUrl, clientId);
    }
}
