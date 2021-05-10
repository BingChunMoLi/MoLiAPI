package com.bingchunmoli.api.shici.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import com.bingchunmoli.api.shici.service.IShiCiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Slf4j
@Service
//@CacheConfig(cacheNames = "shici")
public class ShiCiServiceImpl extends ServiceImpl<ShiCiMapper, ShiCi> implements IShiCiService {
    @Autowired
    private ShiCiMapper shiCiMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ShiCi findRandomShiCi() {
        Long len = redisTemplate.opsForList().size(ApiConstant.SHI_CI);
        log.debug("redis中数据长度为" + len);
        if (len == null || len == 0) {
            log.info("redis数据为空,数组长度len:" + len);
            return shiCiMapper.findRandom();
        }
//        JSON.parseObject(JSON.toJSONString(数据), new TypeReference<对象>() {});
        return JSON.parseObject(JSON.toJSONString(redisTemplate.opsForList().index(ApiConstant.SHI_CI, new Random().nextInt(len.intValue()))), new TypeReference<ShiCi>() {});
    }
}
