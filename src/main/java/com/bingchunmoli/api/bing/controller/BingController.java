package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.service.IBingService;
import com.bingchunmoli.api.bing.task.BingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BingChunMoLi
 */
@RestController
@RequestMapping("bing")
public class BingController {
    @Autowired
    private BingTask bingTask;

    @Autowired
    private IBingService bingService;

    @GetMapping("cn")
    public BingImageVO cnBingImage() {
        return bingService.getCnBingImage();
    }

    @GetMapping("en")
    public BingImageVO enBingImage() {
        return bingService.getEnBingImage();
    }

    @GetMapping("all")
    public Object getAllBingImage() {
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
    /**
     * 执行定时任务补充需要
     * @return 已执行
     * //    @GetMapping("task")
     */
    public String invokeBingTask() {
        bingTask.getBingImage();
        return "已执行";
    }
}
