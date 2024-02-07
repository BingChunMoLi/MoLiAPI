package com.bingchunmoli.api.down.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.down.bean.MediaPO;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author BingChunMoLi
 */
public interface MediaService extends IService<MediaPO> {

    /**
     * 保存判断后未被保存的媒体
     *
     * @param mediaList 需要判断的媒体列表
     */
    void saveByFilterAlreadyExists(Collection<MediaPO> mediaList);

    /**
     * 获取需要下载的媒体，包括未下载完成的其他收藏任务
     *
     * @return 需要下载的媒体
     */
    List<MediaPO> getNeedDownloadMediaList();

    /**
     * 下载单个视频
     *
     * @param av 视频av
     * @return 结果
     */
    String downloadOne(String av, int onlyAudio) throws IOException;
}
