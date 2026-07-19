package com.bingchunmoli.api.bing.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.mapper.BingImageMapper;
import com.bingchunmoli.api.bing.service.BingService;
import com.bingchunmoli.api.exception.ApiJsonProcessingException;
import com.bingchunmoli.api.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class BingServiceImpl extends ServiceImpl<BingImageMapper, BingImage> implements BingService {
    private final ObjectMapper om;
    private final RedisUtil redisUtil;

    @Override
    public BingImage getAllBingImage() {
        BingImage t = redisUtil.getObject(BingEnum.ALL_BING.getKey());
        if (t == null) {
            final Long id = getIdByCreateDate(LocalDate.now());
            if (id == null) {
                return getBingImageByRemote();
            }
            BingImage bingImage = getById(id);
            redisUtil.setObject(BingEnum.ALL_BING.getKey(), bingImage, 1, TimeUnit.DAYS);
            return bingImage;
        }
        return t;
    }


    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    @Override
    public String getRandomImg() {
        int i = new Random().nextInt(Math.toIntExact(count()));
        QueryWrapper<BingImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit " + i + ",1");
        return "https://bing.com" + getOne(queryWrapper).getUrl();
    }

    @Override
    public BingImageVO getBingImage(BingEnum bingEnum) {
        BingImageVO t = redisUtil.getObject(bingEnum.getKey());
        if (t == null) {
            getBingImageByRemote();
            return redisUtil.getObject(bingEnum.getKey());
        }
        return t;
    }

    @Override
    public BingImageVO getBingImageByRemote(final BingEnum bingEnum) {
        final String bingResult = HttpUtil.get(
                "https://www.bing.com/HPImageArchive.aspx?n=1&mkt=$PSCulture&idx=0&ensearch="
                        + bingEnum.getSearch() + "&format=js");
        try {
            return om.readValue(bingResult, BingImageVO.class);
        } catch (JacksonException e) {
            throw new ApiJsonProcessingException(e);
        }
    }

    @Override
    public BingImage getBingImageByRemote() {
        BingImageVO cnBingImageVO = getBingImageByRemote(BingEnum.CN_BING);
        BingImageVO enBingImageVO = getBingImageByRemote(BingEnum.EN_BING);
        BingImage bingImage = new BingImage(cnBingImageVO, enBingImageVO);
        synchronized (BingServiceImpl.class) {
            final Long id = getIdByCreateDate(LocalDate.now());
            if (id == null) {
                save(bingImage);
            } else {
                saveCache(cnBingImageVO, enBingImageVO, bingImage);
                return bingImage;
            }
        }
        saveCache(cnBingImageVO, enBingImageVO, bingImage);
        return bingImage;
    }

    @Override
    public BingImage getBingImageByDate(LocalDate date) {
        return getOne(Wrappers.lambdaQuery(BingImage.builder().build())
                .gt(BingImage::getCreateTime, date)
                .last("limit 1"));
    }

    @Async
    void saveCache(BingImageVO cnBingImageVO, BingImageVO enBingImageVO, BingImage bingImage) {
        redisUtil.setObject(BingEnum.CN_BING.getKey(), cnBingImageVO, 1, TimeUnit.DAYS);
        redisUtil.setObject(BingEnum.EN_BING.getKey(), enBingImageVO, 1, TimeUnit.DAYS);
        redisUtil.setObject(BingEnum.ALL_BING.getKey(), bingImage, 1, TimeUnit.DAYS);
    }

    private Long getIdByCreateDate(final LocalDate date) {
        final LocalDateTime startTime = date.atStartOfDay();
        return baseMapper.getIdByCreateTimeRange(startTime, startTime.plusDays(1));
    }

}
