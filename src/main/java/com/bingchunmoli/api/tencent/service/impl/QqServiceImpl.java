package com.bingchunmoli.api.tencent.service.impl;

import com.bingchunmoli.api.tencent.service.QqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author 冰彦糖
 * @version 3.15.10
 **/
@Slf4j
@Service
public class QqServiceImpl implements QqService {

    @Override
    public BufferedImage getQqImage(String qq, Integer size) {
        try {
            return ImageIO.read(new URL("https://q1.qlogo.cn/g?b=qq&nk=" + qq + "&s=" + size));
        } catch (IOException e) {
            log.error("q1获取头像失败: {}, {}", qq, size, e);
            try {
                return ImageIO.read(new URL("https://q2.qlogo.cn/headimg_dl?dst_uin=" + qq + "&spec=" + size));
            } catch (IOException ioException) {
                log.error("q2获取头像失败: {}, {}", qq, size, ioException);
            }
        }
        throw new IllegalArgumentException("获取头像失败");
    }

    @Override
    public BufferedImage getQzImage(String qq, Integer size) {
        try {
            return ImageIO.read(new URL("https://qlogo1.store.qq.com/qzone/" + qq + "/" + qq + "/" + size));
        } catch (IOException e) {
            log.error("获取qq空间头像失败: {}, {}", qq, size);
            throw new IllegalArgumentException("获取qq空间头像失败");
        }
    }

    @Override
    @Cacheable(value = "getQzImageForJsonGetQz")
    public String getQzImageForJson(String qq) {
        HttpGet httpGet = new HttpGet("https://users.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins=" + qq);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(httpGet)){
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("qq空间头像获取失败: {}", qq, e);
        }
        return "获取qq头像失败";
    }

}
