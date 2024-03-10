package com.bingchunmoli.api.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.config.bean.User;
import com.bingchunmoli.api.config.mapper.UserMapper;
import com.bingchunmoli.api.exception.system.ApiSystemException;
import com.bingchunmoli.api.exception.system.ApiUserNonFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author moli
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Boolean login(User user) {
        Optional<User> optional = getOneOpt(Wrappers.lambdaQuery(new User()).eq(User::getName, user.getName()));
        optional.orElseThrow(() -> new ApiUserNonFoundException("non user"));
        String password = optional.map(User::getPassword).orElseThrow(() -> new ApiSystemException("non password"));
        return passwordEncoder.matches(user.getPassword(), password);
    }

    @Override
    public synchronized boolean register(User user) {
        if (count() > 0) {
            throw new ApiSystemException("错误的初始化用户");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(user);
    }
}




