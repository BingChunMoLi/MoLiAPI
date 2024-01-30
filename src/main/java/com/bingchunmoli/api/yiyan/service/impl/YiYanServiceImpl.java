package com.bingchunmoli.api.yiyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.yiyan.bean.YiYan;
import com.bingchunmoli.api.yiyan.mapper.YiYanMapper;
import com.bingchunmoli.api.yiyan.service.YiYanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author BingChunMoLi
 */
@Service
@RequiredArgsConstructor
public class YiYanServiceImpl extends ServiceImpl<YiYanMapper, YiYan> implements YiYanService {

    @Override
    public YiYan findRandomYiYan() {
        return baseMapper.findRandom();
    }

}
