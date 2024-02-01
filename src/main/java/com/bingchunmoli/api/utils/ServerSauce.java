package com.bingchunmoli.api.utils;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.config.ApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        String params = "https://sctapi.ftqq.com/" +
                apiConfig.getServerSauceKey() +
                ".send?title=" +
                URLEncoder.encode(title, StandardCharsets.UTF_8) +
                "&desp=" +
                URLEncoder.encode(desp, StandardCharsets.UTF_8);
        HttpPost httpPost = new HttpPost(params);
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