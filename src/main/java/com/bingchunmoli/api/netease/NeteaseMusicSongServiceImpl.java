package com.bingchunmoli.api.netease;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.bingchunmoli.api.netease.bean.NeteaseMusicSong;
import com.bingchunmoli.api.netease.bean.NeteaseMusicSongVO;
import com.bingchunmoli.api.netease.mapper.NeteaseMusicSongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
* @author MoLi
*/
@Service
@RequiredArgsConstructor
public class NeteaseMusicSongServiceImpl extends ServiceImpl<NeteaseMusicSongMapper, NeteaseMusicSong>
        implements NeteaseMusicSongService {
    private final NeteaseMusicUserService neteaseMusicUserService;

    @Override
    public void saveBatchAndChild(final List<NeteaseMusicSong> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        final List<NeteaseMusicSong> dbSongs = list(new LambdaQueryWrapper<NeteaseMusicSong>()
                .in(NeteaseMusicSong::getThirdId, songs.stream().map(NeteaseMusicSong::getThirdId).toList()));
        if (dbSongs == null || dbSongs.isEmpty()) {
            ((NeteaseMusicSongService) AopContext.currentProxy()).saveBatch(songs);
            for (final NeteaseMusicSong song : songs) {
                final List<Integer> userIds = neteaseMusicUserService.getIdBatch(song.getArtists());
                getBaseMapper().saveSongUser(song.getId(), userIds);
            }
            return;
        }

        final List<NeteaseMusicSong> existingSongs = songs.stream()
                .filter(song -> song.getId() != null)
                .collect(Collectors.toList());
        final List<NeteaseMusicSong> newSongs = songs.stream()
                .filter(song -> song.getId() == null)
                .collect(Collectors.toList());
        ((NeteaseMusicSongService) AopContext.currentProxy()).updateBatchById(existingSongs);
        ((NeteaseMusicSongService) AopContext.currentProxy()).saveBatch(newSongs);

        for (final NeteaseMusicSong song : songs) {
            final List<Integer> userIds = neteaseMusicUserService.getIdBatch(song.getArtists());
            final List<Integer> alreadyExistsUserIds = getBaseMapper().getSongUser(song.getId());
            final List<Integer> saveUserIds = alreadyExistsUserIds.stream()
                    .filter(userId -> !userIds.contains(userId))
                    .toList();
            if (CollUtil.isNotEmpty(saveUserIds)) {
                getBaseMapper().saveSongUser(song.getId(), saveUserIds);
            }
        }
    }

    @Override
    public NeteaseMusicSong getRandomSong() {
        final long songCount = count();
        if (songCount == 0) {
            return null;
        }

        final long offset = ThreadLocalRandom.current().nextLong(songCount);
        return getBaseMapper().selectSongAtOffset(offset);
    }

    @Override
    public List<NeteaseMusicSongVO> getMusicSongList(final String id) {
        return getBaseMapper().getMusicSongList(id);
    }
}
