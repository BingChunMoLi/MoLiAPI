package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.netease.bean.NeteaseMusicSong;
import com.bingchunmoli.api.netease.mapper.NeteaseMusicSongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
* @author MoLi
*/
@Service
@RequiredArgsConstructor
public class NeteaseMusicSongServiceImpl extends ServiceImpl<NeteaseMusicSongMapper, NeteaseMusicSong> implements NeteaseMusicSongService{
    private final NeteaseMusicUserService neteaseMusicUserService;

    @Override
    @Transactional
    public void saveBatchAndChild(List<NeteaseMusicSong> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        List<NeteaseMusicSong> dbSongs = list(new LambdaQueryWrapper<NeteaseMusicSong>()
                .in(NeteaseMusicSong::getThirdId, songs.stream().map(NeteaseMusicSong::getThirdId).toList()));
        if (dbSongs == null || dbSongs.isEmpty()) {
            ((NeteaseMusicSongService)AopContext.currentProxy()).saveBatch(songs);
            for (NeteaseMusicSong song : songs) {
                List<Integer> userIds = neteaseMusicUserService.getIdBatch(song.getArtists());
                getBaseMapper().saveSongUser(song.getId(), userIds);
            }
        }else {
            List<NeteaseMusicSong> list = songs.stream().filter(v -> {
                for (NeteaseMusicSong song : dbSongs) {
                    if (Objects.equals(song.getId(), v.getId())) {
                        return false;
                    }
                }
                return true;
            }).toList();
            for (NeteaseMusicSong song : list) {
                List<Integer> userIds = neteaseMusicUserService.getIdBatch(song.getArtists());
                getBaseMapper().saveSongUser(song.getId(), userIds);
            }
        }

    }
}




