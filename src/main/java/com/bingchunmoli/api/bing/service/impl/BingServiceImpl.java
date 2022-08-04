package com.bingchunmoli.api.bing.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.mapper.BingImageMapper;
import com.bingchunmoli.api.bing.service.IBingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class BingServiceImpl extends ServiceImpl<BingImageMapper, BingImage> implements IBingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper om;

    @Override
    public BingImage getAllBingImage() throws JsonProcessingException {
        Object t = redisTemplate.opsForValue().get(BingEnum.ALL_BING.getKey());
        if (t == null) {
            t = this.getOne(new LambdaQueryWrapper<BingImage>().eq(BingImage :: getCreateTime, LocalDate.now()));
        }
        if (t == null) {
            getBingImageByRemote();
            return (BingImage) redisTemplate.opsForValue().get(BingEnum.ALL_BING.getKey());
        }
        return (BingImage) t;
    }


    /**
     * 获取随机一张图的url
     *
     * @return 随即Bing图的url
     */
    @Override
    public String getRandomImg() {
        long count = count();
        int i = new Random().nextInt(Math.toIntExact(count));
        QueryWrapper<BingImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit " +  i + ",1");
        return "https://bing.com" + getOne(queryWrapper).getUrl();
    }

    @Override
    public BingImageVO getBingImage(BingEnum bingEnum) throws JsonProcessingException {
        Object t = redisTemplate.opsForValue().get(bingEnum.getKey());
        if (t == null) {
            getBingImageByRemote();
            return (BingImageVO) redisTemplate.opsForValue().get(bingEnum.getKey());
        }
        return (BingImageVO) t;
    }

    @Override
    public BingImageVO getBingImageByRemote(BingEnum bingEnum) throws JsonProcessingException {
        String bingResult = HttpUtil.get("https://www.bing.com/HPImageArchive.aspx?n=1&mkt=$PSCulture&idx=0&ensearch=" + bingEnum.getSearch() + "&format=js");
        return om.readValue(bingResult, BingImageVO.class);
    }

    @Override
    public BingImage getBingImageByRemote() throws JsonProcessingException {
        BingImageVO cnBingImageVO = getBingImageByRemote(BingEnum.CN_BING);
        BingImageVO enBingImageVO = getBingImageByRemote(BingEnum.EN_BING);
        BingImage bingImage = new BingImage(cnBingImageVO, enBingImageVO);
        synchronized (BingServiceImpl.class) {
            Long id = baseMapper.getIdByCreateDate(LocalDate.now());
            if (id == null) {
                save(bingImage);
            }else {
                return bingImage;
            }
        }
        redisTemplate.opsForValue().set(BingEnum.CN_BING.getKey(), cnBingImageVO, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(BingEnum.EN_BING.getKey(), enBingImageVO, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(BingEnum.ALL_BING.getKey(), bingImage, 1, TimeUnit.DAYS);
        return bingImage;
    }

}
