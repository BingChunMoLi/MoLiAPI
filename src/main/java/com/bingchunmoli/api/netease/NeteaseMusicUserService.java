package com.bingchunmoli.api.netease;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.netease.bean.NeteaseMusicUser;

import java.util.List;

/**
* @author MoLi
*/
public interface NeteaseMusicUserService extends IService<NeteaseMusicUser> {

    Integer saveOrUpdateByThirdId(NeteaseMusicUser playListUser);

    /**
     * 根据歌曲作者批量获取id
     * @param artists 作者信息
     * @return 作者id
     */
    List<Integer> getIdBatch(List<NeteaseMusicUser> artists);
}
