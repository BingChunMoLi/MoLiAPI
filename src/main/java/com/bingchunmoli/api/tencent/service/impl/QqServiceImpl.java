package com.bingchunmoli.api.tencent.service.impl;

import com.bingchunmoli.api.tencent.service.IQqService;
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
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author 冰彦糖
 * @version 0.0.1-SNAPSHOT
 **/
@Slf4j
@Service
public class QqServiceImpl implements IQqService {

    @Override
    public BufferedImage getQqImage(String qq, Integer size) {
        try {
            return ImageIO.read(new URL("https://q1.qlogo.cn/g?b=qq&nk=" + qq + "&s=" + size));
        } catch (IOException e) {
            log.debug("第一次获取QQ头像失败\tQQ:" + qq + "\t size:" + size);
            try {
                return ImageIO.read(new URL("https://q2.qlogo.cn/headimg_dl?dst_uin=" + qq + "&spec=" + size));
            } catch (IOException ioException) {
                log.debug("第二次获取QQ头像失败\tQQ:" + qq + "\t size:" + size);
                log.error("-------------第一次堆栈信息------------");
                e.printStackTrace();
                log.error("-------------第二次堆栈信息------------");
                ioException.printStackTrace();
                try {
                    return ImageIO.read(new File("/api/cache/g.jpg"));
                } catch (IOException exception) {
                    log.error("缓存QQ头像丢失");
                    return null;
                }
            }
        }
    }

    @Override
    public BufferedImage getQzImage(String qq, Integer size) {
        try {
            return ImageIO.read(new URL("https://qlogo1.store.qq.com/qzone/" + qq + "/" + qq + "/" + size));
        } catch (IOException e) {
            log.debug("获取QQ空间头像失败QQ:" + qq + "\t size:" + size);
            e.printStackTrace();
            try {
                return ImageIO.read(new File("/api/cache/g.jpg"));
            } catch (IOException exception) {
                log.error("缓存QQ头像丢失");
                return null;
            }
        }
    }

    @Override
    @Cacheable("getQqImageForJson")
    public String getQqImageForJson(String qq, Integer size) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("https://ptlogin2.qq.com/getface?appid=1006102&imgtype=4&uin=" + qq);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "UTF8");
            }
        } catch (IOException e) {
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
                log.debug("关闭客户端和响应失败");
                e.printStackTrace();
            }
        }
        return "没有响应，error";
    }

    @Override
    @Cacheable(value = "getQzImageForJsonGetQz")
    public String getQzImageForJson(String qq) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("https://users.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins=" + qq);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "UTF8");
            }
        } catch (IOException e) {
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
                log.debug("关闭客户端和响应失败");
                e.printStackTrace();
            }
        }
        return "没有响应，error";
    }

    @Override
    @Cacheable("getQqImageForEncrypt")
    public String getQqImageForEncrypt(String qq, Integer size) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("https://ptlogin2.qq.com/getface?appid=1006102&imgtype=4&uin=" + qq);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String s = EntityUtils.toString(response.getEntity(), "UTF8");
                String[] split = s.split("\"");
                s = split[split.length - 2];
                return s;
            }
        } catch (IOException e) {
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
                log.debug("关闭客户端和响应失败");
                e.printStackTrace();
            }
        }
        return "没有响应，error";
    }
}