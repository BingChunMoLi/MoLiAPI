package com.bingchunmoli.api.push;

import com.bingchunmoli.api.app.bean.PushTypeEnum;
import com.bingchunmoli.api.exception.ApiAppMessageException;
import com.bingchunmoli.api.push.bean.AppMessage;
import com.bingchunmoli.api.push.bean.Message;
import com.bingchunmoli.api.push.bean.enums.PushMessageEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushHuaweiApp implements Push {
    private static final String GRANT_TYPE = "client_credentials";
    private static final String SUCCESS_CODE = "80000000";
    private static final long TOKEN_EXPIRE_BUFFER_SECONDS = 120L;

    private final HuaweiPushProperties huaweiPushProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder().build();
    private String accessToken;
    private Instant accessTokenExpiresAt = Instant.EPOCH;

    @Override
    public String send(final Message message) {
        if (!(message instanceof AppMessage appMessage)) {
            throw new ApiAppMessageException("错误的 message 类型");
        }
        final String deviceToken = appMessage.getDeviceToken();
        if (deviceToken == null || deviceToken.isBlank()) {
            throw new ApiAppMessageException("华为推送 token 为空");
        }
        final String responseBody = restClient.post()
                .uri(huaweiPushProperties.getSendUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAccessToken())
                .body(buildPushBody(appMessage))
                .retrieve()
                .body(String.class);
        validatePushResponse(responseBody);
        return responseBody;
    }

    @Override
    public boolean support(final Message message) {
        if (!PushMessageEnum.APP_MESSAGE.equals(message.getType())) {
            return false;
        }
        if (message instanceof AppMessage appMessage) {
            return PushTypeEnum.HMS.equals(appMessage.getPushType());
        }
        return false;
    }

    @Override
    public boolean isEnable() {
        return huaweiPushProperties.isConfigured();
    }

    private synchronized String getAccessToken() {
        if (accessToken != null && Instant.now().isBefore(accessTokenExpiresAt)) {
            return accessToken;
        }
        final MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE);
        requestBody.add("client_id", huaweiPushProperties.getClientId());
        requestBody.add("client_secret", huaweiPushProperties.getClientSecret());
        final String responseBody = restClient.post()
                .uri(huaweiPushProperties.getOauthUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body(String.class);
        try {
            final JsonNode jsonNode = objectMapper.readTree(responseBody);
            final JsonNode accessTokenNode = jsonNode.get("access_token");
            if (accessTokenNode == null || accessTokenNode.asText().isBlank()) {
                throw new ApiAppMessageException("获取华为推送 access token 失败");
            }
            final long expiresIn = jsonNode.path("expires_in").asLong(3600L);
            accessToken = accessTokenNode.asText();
            accessTokenExpiresAt = Instant.now().plusSeconds(expiresIn - TOKEN_EXPIRE_BUFFER_SECONDS);
            return accessToken;
        } catch (JacksonException e) {
            log.error("解析华为推送 access token 响应失败", e);
            throw new ApiAppMessageException("解析华为推送 access token 响应失败");
        }
    }

    private Map<String, Object> buildPushBody(final AppMessage appMessage) {
        final Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("title", appMessage.getTitle());
        notification.put("body", appMessage.getBody());

        final Map<String, Object> androidNotification = new LinkedHashMap<>();
        androidNotification.put("click_action", Map.of("type", 3));

        final Map<String, Object> android = new LinkedHashMap<>();
        android.put("notification", androidNotification);

        final Map<String, Object> message = new LinkedHashMap<>();
        message.put("notification", notification);
        message.put("android", android);
        message.put("token", List.of(appMessage.getDeviceToken()));

        final Map<String, Object> root = new LinkedHashMap<>();
        root.put("validate_only", false);
        root.put("message", message);
        return root;
    }

    private void validatePushResponse(final String responseBody) {
        try {
            final JsonNode jsonNode = objectMapper.readTree(responseBody);
            final String code = jsonNode.path("code").asText();
            if (!SUCCESS_CODE.equals(code)) {
                log.error("华为推送失败: {}", responseBody);
                throw new ApiAppMessageException("华为推送失败");
            }
        } catch (JacksonException e) {
            log.error("解析华为推送响应失败", e);
            throw new ApiAppMessageException("解析华为推送响应失败");
        }
    }
}
