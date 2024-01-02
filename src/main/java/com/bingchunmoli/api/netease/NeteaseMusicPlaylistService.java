package com.bingchunmoli.api.netease;

import com.bingchunmoli.api.netease.bean.NeteaseMusicPlaylist;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author MoLi
*/
public interface NeteaseMusicPlaylistService extends IService<NeteaseMusicPlaylist> {

    Integer saveIsExist(NeteaseMusicPlaylist playlist);
}
