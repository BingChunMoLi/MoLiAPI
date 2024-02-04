package com.bingchunmoli.api.config;

import com.bingchunmoli.api.config.bean.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author moli
 */
public interface UserService extends IService<User> {

    Boolean login(User user);

    boolean register(User user);
}
