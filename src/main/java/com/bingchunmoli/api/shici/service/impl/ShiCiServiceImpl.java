package com.bingchunmoli.api.shici.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import com.bingchunmoli.api.shici.service.ShiCiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

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
    @Override
    public ShiCi findRandomShiCi() {
        final long recordCount = count();
        if (recordCount == 0) {
            return null;
        }
        return baseMapper.findAtOffset(ThreadLocalRandom.current().nextLong(recordCount));
    }

}
