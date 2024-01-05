package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.netease.bean.NeteaseMusicPlaylist;
import com.bingchunmoli.api.netease.mapper.NeteaseMusicPlaylistMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
* @author MoLi
*/
@Service
public class NeteaseMusicPlaylistServiceImpl extends ServiceImpl<NeteaseMusicPlaylistMapper, NeteaseMusicPlaylist> implements NeteaseMusicPlaylistService{

    @Override
    public Integer saveIsExist(NeteaseMusicPlaylist playlist) {
        NeteaseMusicPlaylist dbPlaylist = getOne(new LambdaQueryWrapper<NeteaseMusicPlaylist>()
                .eq(NeteaseMusicPlaylist::getThirdId, playlist.getThirdId()));
        return Optional.ofNullable(dbPlaylist)
                .map(NeteaseMusicPlaylist::getId)
                .orElseGet(() -> {
                    save(playlist);
                    return playlist.getId();
                });
    }
}




