package com.bingchunmoli.api.bing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bing.bean.BingImage;
import com.bingchunmoli.api.bing.bean.BingImageVO;
import com.bingchunmoli.api.bing.bean.enums.BingEnum;
import com.bingchunmoli.api.bing.mapper.BingImageMapper;
import com.bingchunmoli.api.bing.service.IBingService;
import com.bingchunmoli.api.bing.task.BingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Random;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class BingServiceImpl extends ServiceImpl<BingImageMapper, BingImage> implements IBingService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Lazy
    @Resource
    private  BingTask bingTask;

    @Override
    public BingImage getAllBingImage() {
        Object t = redisTemplate.opsForValue().get(BingEnum.ALLBING.getKey());
        if (t == null) {
            t = this.getOne(new LambdaQueryWrapper<BingImage>().eq(BingImage :: getCreateTime, LocalDate.now()));
        }
        if (t == null) {
            bingTask.getBingImage();
            return (BingImage) redisTemplate.opsForValue().get(BingEnum.ALLBING.getKey());
        }
        return (BingImage) t;
    }

    @Override
    public BingImageVO getCnBingImage() {
        Object t = redisTemplate.opsForValue().get(BingEnum.CNBING.getKey());
        if (t == null) {
            bingTask.getBingImage();
            return (BingImageVO) redisTemplate.opsForValue().get(BingEnum.CNBING.getKey());
        }
        return (BingImageVO) t;
    }

    @Override
    public BingImageVO getEnBingImage() {
        Object t = redisTemplate.opsForValue().get(BingEnum.ENBING.getKey());
        if (t == null) {
            bingTask.getBingImage();
            return (BingImageVO) redisTemplate.opsForValue().get(BingEnum.ENBING.getKey());
        }
        return (BingImageVO) t;
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

}
