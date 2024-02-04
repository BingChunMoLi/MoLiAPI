package com.bingchunmoli.api.down.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.down.bean.UserPO;
import com.bingchunmoli.api.down.mapper.UserDao;
import com.bingchunmoli.api.down.service.BiliUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BingChunMoLi
 */
@Service
public class BiliUserServiceImpl extends ServiceImpl<UserDao, UserPO> implements BiliUserService {

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByFilterAlreadyExists(Collection<UserPO> saveUserList) {
        List<Integer> saveUserIdList = saveUserList.stream().map(UserPO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<UserPO> queryWrapper = new LambdaQueryWrapper<UserPO>().select(UserPO::getId).in(UserPO::getId, saveUserIdList);
        List<UserPO> userPoList = list(queryWrapper);
        List<Integer> userPoIdList = userPoList.stream().map(UserPO::getId).toList();
        List<UserPO> needSaveUserList = saveUserList.stream().filter(v -> !userPoIdList.contains(v.getId())).distinct().collect(Collectors.toList());
        saveBatch(needSaveUserList);
        if ("dev".equalsIgnoreCase(profile)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

}
