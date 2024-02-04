package com.bingchunmoli.api.shici.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.shici.bean.ShiCi;
import com.bingchunmoli.api.shici.mapper.ShiCiMapper;
import com.bingchunmoli.api.shici.service.ShiCiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private List<ShiCi> list;

    @Override
    public ShiCi findRandomShiCi() {
        return list.get(new Random().nextInt(list.size()));
    }

    /**
     * 初始化service中的shiciList
     */
    @Override
    public void setShiCiList(List<ShiCi> list) {
        this.list = list;
    }

}
