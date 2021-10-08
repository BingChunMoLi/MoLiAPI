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
    public Object getAllBingImage(){
        return bingService.getAllBingImage();
    }

    @GetMapping("task")
    public String invokeBingTask(){
        bingTask.getBingImage();
        return "已执行";
    }
}
