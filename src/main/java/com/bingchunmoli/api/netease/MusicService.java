package com.bingchunmoli.api.netease;

import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@NoArgsConstructor
public class MusicService {

    private final String urlHost = "https://music.163.com";
    private final HttpHost baseHost = HttpHost.create(urlHost);
    public String getPlayListInfo(String id, String cookie){
        Collection<BasicHeader> defaultHeader = List.of(new BasicHeader("cookie", cookie));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(defaultHeader)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build()){
            HttpRequest request = new HttpGet( "/api/playlist/detail?id=" + id);
            return httpClient.execute(baseHost, request, res -> {
                String string = EntityUtils.toString(res.getEntity());
                EntityUtils.consume(res.getEntity());
                return string;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
