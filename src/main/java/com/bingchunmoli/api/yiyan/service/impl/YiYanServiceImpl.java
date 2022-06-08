package com.bingchunmoli.api.yiyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class YiYanServiceImpl extends ServiceImpl<YiYanMapper, YiYan> implements IYiYanService {
    private final YiYanMapper yiYanMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtil redisUtil;

    @Override
    public Object findRandomYiYan() {
        if (redisUtil.isNotEnable()) {
            return yiYanMapper.findRandom();
        }
        Long len = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (len == null || len == 0) {
            return yiYanMapper.findRandom();
        }
        return redisTemplate.opsForList().index(ApiConstant.YI_YAN, new Random().nextInt(len.intValue()));
    }

    @Override
    public Object getYiYan(Integer id) {
        if (redisUtil.isNotEnable()) {
            return this.getById(id);
        }
        Object yiYan = redisTemplate.opsForList().index(ApiConstant.YI_YAN, id);
        if (yiYan == null) {
            return this.getById(id);
        }
        return yiYan;
    }


}
