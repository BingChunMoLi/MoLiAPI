package com.bingchunmoli.api.utils.push;

import com.bingchunmoli.api.bean.HttpMessage;
import com.bingchunmoli.api.bean.Message;
import com.bingchunmoli.api.bean.enums.HttpMessageType;
import com.bingchunmoli.api.exception.ApiMessageException;
import com.bingchunmoli.api.properties.ApiConfig;
import com.bingchunmoli.api.utils.push.bean.SendParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author moli
 */
@Service
@RequiredArgsConstructor
public class PushHttpByServerSauce extends PushHttp{
    private final ApiConfig apiConfig;
    @Override
    public boolean support(Message message) {
        if (message instanceof HttpMessage httpMessage) {
            return httpMessage.getHttpMessageType().equals(HttpMessageType.SERVER_SAUCE);
        }
        return false;
    }

    @Override
    protected SendParam buildSend(Message message) {
        if (message instanceof HttpMessage httpMessage) {
            String url = "https://sctapi.ftqq.com/" +
                    apiConfig.getServerSauceKey() +
                    ".send?title=" +
                    URLEncoder.encode(httpMessage.getTitle(), StandardCharsets.UTF_8) +
                    "&desp=" +
                    URLEncoder.encode(httpMessage.getBody(), StandardCharsets.UTF_8);
            return new SendParam()
                    .setMethod(HttpMethod.GET.name())
                    .setUrl(url);
        }
        throw new ApiMessageException("错误的httpMessage: " + message.getClass());
    }
}
