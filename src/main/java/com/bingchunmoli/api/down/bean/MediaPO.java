package com.bingchunmoli.api.down.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author BingChunMoLi
 */
@Data
@Builder
@TableName("bili_media")
@NoArgsConstructor
@AllArgsConstructor
public class MediaPO {

    private Integer id;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 标题
     */
    private String title;
    /**
     * 封面图
     */
    private String cover;
    /**
     * 简介
     */
    private String intro;
    /**
     * 分P数
     */
    private Integer page;
    /**
     * 用户Id
     */
    private Integer mid;
    /**
     * 创建时间
     */
    private Integer ctime;
    /**
     * 发布时间
     */
    private Integer pubtime;
    /**
     * 收藏时间
     */
    private Integer favTime;
    /**
     * BV
     */
    private String bvId;

    /**
     * 下载状态
     */
    private Integer down;

    /**
     * 上传状态
     */
    private Integer upload;

    /**
     * 失效收藏标记
     */
    private Integer invalidPush;

    /**
     * onedrive上传
     */
    private Integer onedriveUpload;

    /**
     * 仅下载音频
     */
    private Integer onlyAudio;

    /**
     * 收藏夹id
     */
    private Integer favId;
}
