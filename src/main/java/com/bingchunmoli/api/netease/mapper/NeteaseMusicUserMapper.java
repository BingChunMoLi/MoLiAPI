package com.bingchunmoli.api.netease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bingchunmoli.api.netease.bean.NeteaseMusicUser;

import java.util.List;

/**
* @author MoLi
*/
public interface NeteaseMusicUserMapper extends BaseMapper<NeteaseMusicUser> {

    List<NeteaseMusicUser> getIdByBirthId(List<Long> list);
}




