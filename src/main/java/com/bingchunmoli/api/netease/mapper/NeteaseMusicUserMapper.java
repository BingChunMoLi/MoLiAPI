package com.bingchunmoli.api.netease.mapper;

import com.bingchunmoli.api.netease.bean.NeteaseMusicUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author MoLi
*/
public interface NeteaseMusicUserMapper extends BaseMapper<NeteaseMusicUser> {

    List<NeteaseMusicUser> getIdByBirthId(List<Long> list);
}




