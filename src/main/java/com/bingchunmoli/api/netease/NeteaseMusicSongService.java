package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.netease.bean.NeteaseMusicSong;

import java.util.List;

/**
* @author MoLi
*/
public interface NeteaseMusicSongService extends IService<NeteaseMusicSong> {

    void saveBatchAndChild(List<NeteaseMusicSong> songs);
}
