package com.bingchunmoli.api.bing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.mapper.BingImageMapper;
import com.bingchunmoli.api.bing.service.IBingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class BingServiceImpl extends ServiceImpl<BingImageMapper, BingImage> implements IBingService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public BingImage getAllBingImage() {
        return (BingImage) redisTemplate.opsForValue().get(BingEnum.ALLBING.getKey());
    }

    @Override
    public BingImageVO getCnBingImage() {
        return (BingImageVO) redisTemplate.opsForValue().get(BingEnum.CNBING.getKey());
    }

    @Override
    public BingImageVO getEnBingImage() {
        return (BingImageVO) redisTemplate.opsForValue().get(BingEnum.ENBING.getKey());
    }

}
