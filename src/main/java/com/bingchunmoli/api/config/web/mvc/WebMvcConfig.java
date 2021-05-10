package com.bingchunmoli.api.config.web.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO 图片转换器
 * @Author 冰彦糖
 * @Data 2020/11/16 15:22
 * @ClassName WebMvcConfig
 * @PackageName: com.bingchunmoli.api.config
 * @Version 0.0.1-SNAPSHOT
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * description: 图片转换器
     * author: BingChunMoLi
     * date: 2020/11/16 15:24
     * version: 0.0.1-SNAPSHOT
     *  * @param converters
     * @return void
     */ 
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BufferedImageHttpMessageConverter());
    }
}