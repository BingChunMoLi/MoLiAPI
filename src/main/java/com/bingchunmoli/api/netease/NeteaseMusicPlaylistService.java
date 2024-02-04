package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.netease.bean.NeteaseMusicPlaylist;

/**
* @author MoLi
*/
public interface NeteaseMusicPlaylistService extends IService<NeteaseMusicPlaylist> {

    Integer saveIsExist(NeteaseMusicPlaylist playlist);
}
