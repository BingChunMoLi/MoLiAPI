package com.bingchunmoli.api.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.config.bean.User;

/**
 * @author moli
 */
public interface UserService extends IService<User> {

    Boolean login(User user);

    boolean register(User user);
}
