package com.bingchunmoli.api.utils;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.properties.ApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author bingchunmoli
 **/
@Slf4j
@Component
@Deprecated
@RequiredArgsConstructor
public class ServerSauce {
    private final ApiConfig apiConfig;

    public void send(String title, String desp) {
        if (StrUtil.isEmpty(apiConfig.getServerSauceKey())) {
            return;
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuilder params = new StringBuilder();
        try {
            params.append("https://sctapi.ftqq.com/");
            params.append(apiConfig.getServerSauceKey());
            params.append(".send?title=");
            params.append(URLEncoder.encode(title, "utf-8"));
            params.append("&desp=");
            params.append(URLEncoder.encode(desp, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.debug("URL编码异常");
            e.printStackTrace();
        }
        HttpPost httpPost = new HttpPost(params.toString());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            log.info("Server酱推送成功");
        } catch (IOException e) {
            log.debug("Server酱推送失败");
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.debug("IO异常");
                e.printStackTrace();
            }
        }
    }
}