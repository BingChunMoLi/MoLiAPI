package com.bingchunmoli.api.daily.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.daily.bean.Account;
import com.bingchunmoli.api.daily.mapper.AccountMapper;
import com.bingchunmoli.api.daily.service.AccountService;
import org.springframework.stereotype.Service;

/**
* @author MoLi
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{

}
