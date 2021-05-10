package com.bingchunmoli.api.yiyan.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import com.bingchunmoli.api.yiyan.service.IYiYanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class YiYanServiceImpl extends ServiceImpl<YiYanMapper, YiYan> implements IYiYanService {

    @Autowired
    YiYanMapper yiYanMapper;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public String findRandomYiYan() {
        Long len = redisTemplate.opsForList().size(ApiConstant.YI_YAN);
        if (len == null || len == 0) {
            return JSON.toJSONString(yiYanMapper.findRandom());
        }
        return JSON.toJSONString(redisTemplate.opsForList().index(ApiConstant.YI_YAN, new Random().nextInt(len.intValue())));
    }
}
