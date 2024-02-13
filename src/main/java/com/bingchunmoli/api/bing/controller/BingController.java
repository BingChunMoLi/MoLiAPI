package com.bingchunmoli.api.bing.controller;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.service.BingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * bing每日美图
 *
 * @author BingChunMoLi
 */
@RestController
@Tag(name = "bing美图")
@RequiredArgsConstructor
@RequestMapping("bing")
public class BingController {
    private final BingService bingService;

    /**
     * 每日随机图国内版
     *
     * @return bing图片对象
     */
    @GetMapping("cn")
    @Operation(summary = "国内版的bing美图链接")
    public ResultVO<BingImageVO> cnBingImage() {
        return ResultVO.ok(bingService.getBingImage(BingEnum.CN_BING));
    }

    /**
     * 每日随机图国际版
     *
     * @return bing图片对象|
     */
    @GetMapping("en")
    @Operation(summary = "国际版的bing美图链接")
    public ResultVO<BingImageVO> enBingImage() {
        return ResultVO.ok(bingService.getBingImage(BingEnum.EN_BING));
    }

    /**
     * 每日随机图
     *
     * @return bing图片对象|
     */
    @GetMapping("all")
    @Operation(summary = "当天的bing美图,同时包括国内版和国际版")
    public ResultVO<BingImage> getAllBingImage() {
        return ResultVO.ok(bingService.getAllBingImage());
    }

    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    @GetMapping("random")
    @Operation(summary = "获取随机一天的bing美图")
    public ResultVO<String> getRandomBingImg() {
        return ResultVO.ok(bingService.getRandomImg());
    }

    /**
     * 获取指定日期的Bing随机图json
     * @param year 年
     * @param month 月
     * @param day 日
     * @return bing图片
     */
    @Valid
    @GetMapping("{year}/{month}/{day}")
    @Operation(summary = "根据日期获取bing美图")
    public ResultVO<BingImage> getImageByYearMonthDay(@PathVariable @NotNull Integer year, @PathVariable @NotNull Integer month, @PathVariable @NotNull Integer day){
        return ResultVO.ok(bingService.getBingImageByDate(LocalDate.of(year, month, day)));
    }

    /**
     * 获取指定日期的bing随机图
     *
     * @param date 日期
     * @return BingImage
     */
    @GetMapping("date")
    @Operation(summary = "根据日期获取bing美图", description = "日期格式为ISO标准格式 The most common ISO Date Format yyyy-MM-dd — for example, \"2000-10-31\".")
    public ResultVO<BingImage> getBingImageByDate(@Valid @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResultVO.ok(bingService.getBingImageByDate(date));
    }
}
