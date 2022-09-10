package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.service.IBingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * bing每日美图
 * @author BingChunMoLi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("bing")
public class BingController {
    private final IBingService bingService;

    /**
     * //@ApiNote 每日随机图国内版
     * @return bing图片对象|
     */
    @GetMapping("cn")
    public BingImageVO cnBingImage() throws JsonProcessingException {
        return bingService.getBingImage(BingEnum.CN_BING);
    }

    /**
     * //@ApiNote 每日随机图国际版
     * @return bing图片对象|
     */
    @GetMapping("en")
    public BingImageVO enBingImage() throws JsonProcessingException {
        return bingService.getBingImage(BingEnum.EN_BING);
    }

    /**
     * //@ApiNote 每日随机图
     * @return bing图片对象|
     */
    @GetMapping("all")
    public BingImage getAllBingImage() throws JsonProcessingException {
        return bingService.getAllBingImage();
    }

    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    @GetMapping("random")
    public String getRandomBingImg(){
        return bingService.getRandomImg();
    }
}
