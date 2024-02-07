package com.bingchunmoli.api.down.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.down.bean.UserPO;

import java.util.Collection;

/**
 * @author BingChunMoLi
 */
public interface BiliUserService extends IService<UserPO> {
    /**
     * 保存判断后未被保存的用户
     *
     * @param saveUserList 需要判断的用户列表
     */
    void saveByFilterAlreadyExists(Collection<UserPO> saveUserList);
}
