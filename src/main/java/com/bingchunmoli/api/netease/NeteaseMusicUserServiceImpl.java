package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.netease.bean.NeteaseMusicUser;
import com.bingchunmoli.api.netease.mapper.NeteaseMusicUserMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
* @author MoLi
*/
@Service
public class NeteaseMusicUserServiceImpl extends ServiceImpl<NeteaseMusicUserMapper, NeteaseMusicUser> implements NeteaseMusicUserService{

    @Override
    public Integer saveOrUpdateByThirdId(NeteaseMusicUser playListUser) {
        NeteaseMusicUser dbUser = getOne(new LambdaQueryWrapper<NeteaseMusicUser>()
                .eq(NeteaseMusicUser::getThirdId, playListUser.getThirdId()));
        return Optional.ofNullable(dbUser)
                .map(NeteaseMusicUser::getId)
                .orElseGet(()->{
                    save(playListUser);
                    return playListUser.getId();
                });

    }

    @Override
    public List<Integer> getIdBatch(List<NeteaseMusicUser> artists) {
        if (artists == null || artists.isEmpty()) {
            return List.of();
        }
        List<NeteaseMusicUser> userDbList = getBaseMapper().getIdByBirthId(artists.stream().map(NeteaseMusicUser::getThirdId).toList());
        if (userDbList == null || userDbList.isEmpty()) {
            ((NeteaseMusicUserService)AopContext.currentProxy()).saveBatch(artists);
            return artists.stream().map(NeteaseMusicUser::getId).toList();
        }else {
            List<NeteaseMusicUser> list = artists.stream().filter(v -> {
                for (NeteaseMusicUser user : userDbList) {
                    if (Objects.equals(user.getThirdId(), v.getThirdId())) {
                        //数据库已存在,不在添加过滤
                        return false;
                    }
                }
                return true;
            }).toList();
            ((NeteaseMusicUserService)AopContext.currentProxy()).saveBatch(list);
            return Stream.concat(artists.stream().map(NeteaseMusicUser::getId), list.stream().map(NeteaseMusicUser::getId)).toList();
        }
    }
}




