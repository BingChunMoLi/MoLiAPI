package com.bingchunmoli.api.down.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.down.bean.FavPO;
import com.bingchunmoli.api.down.mapper.FavDao;
import com.bingchunmoli.api.down.service.FavService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author BingChunMoLi
 */
@Service
public class FavServiceImpl extends ServiceImpl<FavDao, FavPO> implements FavService {

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByFilterAlreadyExists(FavPO saveFav) {
        if (count(new LambdaQueryWrapper<FavPO>().eq(FavPO::getId, saveFav.getId()).last("limit 1")) == 0) {
            save(saveFav);
        }
        if ("dev".equalsIgnoreCase(profile)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
