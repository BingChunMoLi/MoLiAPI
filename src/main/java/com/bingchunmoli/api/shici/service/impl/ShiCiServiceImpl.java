package com.bingchunmoli.api.shici.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import com.bingchunmoli.api.shici.service.ShiCiService;
import com.bingchunmoli.api.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class ShiCiServiceImpl extends ServiceImpl<ShiCiMapper, ShiCi> implements ShiCiService {
    private final ShiCiMapper shiCiMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtil redisUtil;

    @Override
    public ShiCi findRandomShiCi() {
        Long len = redisTemplate.opsForList().size(ApiConstant.SHI_CI);
        log.debug("redis中数据长度为" + len);
        if (len == null || len == 0) {
            log.info("redis数据为空,数组长度len:" + len);
            return shiCiMapper.findRandom();
        }
        return (ShiCi) redisTemplate.opsForList().index(ApiConstant.SHI_CI, new Random().nextInt(len.intValue()));
    }

    @Override
    public ShiCi getShiCi(Integer id) {
        ShiCi shiCi = (ShiCi) redisTemplate.opsForList().index(ApiConstant.SHI_CI, id);
        if (shiCi == null) {
            return this.getById(id);
        }
        return shiCi;
    }
}
