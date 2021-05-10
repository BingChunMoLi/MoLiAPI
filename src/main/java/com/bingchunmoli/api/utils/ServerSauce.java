package com.bingchunmoli.api.utils;

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
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO Server酱
 * @Author 冰彦糖
 * @Data 2020/11/16 22:24
 * @ClassName ServerSauce
 * @PackageName: com.bingchunmoli.api.com.bingchunmoli.api.utils
 * @Version 0.0.1-SNAPSHOT
 **/
@Slf4j
@Component
public class ServerSauce {

    private static String url;
//    @Value("${server-sauce.uri}")
    public void setUrl(String url) {
        this.url = url;
    }
    public static void send(String title, String desp){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        StringBuffer params = new StringBuffer();
        try {
            params.append(url);
            params.append("?title=");
            params.append(URLEncoder.encode(title,"utf-8"));
            params.append("&");
            params.append("desp=");
            params.append(URLEncoder.encode(desp,"utf-8"));
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
        }finally {
            try {
                if (httpClient != null){
                    httpClient.close();
                }
                if (response != null){
                    response.close();
                }
            } catch (IOException e) {
                log.debug("IO异常");
                e.printStackTrace();
            }
        }
    }
}