package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.netease.bean.NeteaseMusicSong;

import java.util.List;

/**
* @author MoLi
*/
public interface NeteaseMusicSongService extends IService<NeteaseMusicSong> {

    void saveBatchAndChild(List<NeteaseMusicSong> songs);

    /**
     * 获取随机歌曲
     * @return 一首歌曲
     */
    NeteaseMusicSong getRandomSong();
}
