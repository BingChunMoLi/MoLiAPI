package com.bingchunmoli.api.yiyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class YiYanServiceImpl extends ServiceImpl<YiYanMapper, YiYan> implements YiYanService {
    private final YiYanMapper yiYanMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public YiYan findRandomYiYan() {
        Long len = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (len == null || len == 0) {
            return yiYanMapper.findRandom();
        }
        return (YiYan) redisTemplate.opsForList().index(ApiConstant.YI_YAN, new Random().nextInt(len.intValue()));
    }

    @Override
    public YiYan getYiYan(Integer id) {
        YiYan yiYan = (YiYan) redisTemplate.opsForList().index(ApiConstant.YI_YAN, id);
        if (yiYan == null) {
            return this.getById(id);
        }
        return yiYan;
    }


}
