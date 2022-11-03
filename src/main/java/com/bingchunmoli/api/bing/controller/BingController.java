package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.service.IBingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * bing每日美图
 *
 * @author BingChunMoLi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("bing")
public class BingController {
    private final IBingService bingService;

    /**
     * //@ApiNote 每日随机图国内版
     *
     * @return bing图片对象|
     */
    @GetMapping("cn")
    public ResultVO<BingImageVO> cnBingImage() {
        return ResultVO.ok(bingService.getBingImage(BingEnum.CN_BING));
    }

    /**
     * //@ApiNote 每日随机图国际版
     *
     * @return bing图片对象|
     */
    @GetMapping("en")
    public ResultVO<BingImageVO> enBingImage() {
        return ResultVO.ok(bingService.getBingImage(BingEnum.EN_BING));
    }

    /**
     * //@ApiNote 每日随机图
     *
     * @return bing图片对象|
     */
    @GetMapping("all")
    public ResultVO<BingImage> getAllBingImage() {
        return ResultVO.ok(bingService.getAllBingImage());
    }

    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    @GetMapping("random")
    public ResultVO<String> getRandomBingImg() {
        return ResultVO.ok(bingService.getRandomImg());
    }
}
