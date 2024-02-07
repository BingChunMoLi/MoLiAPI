package com.bingchunmoli.api.down.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author BingChunMoLi
 */
@Data
@TableName("bili_fav")
public class FavPO {

    private Integer id;
    private Integer fid;
    /**
     * 用户Id
     */
    private Integer mid;
    /**
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 媒体数
     */
    private Integer mediaCount;
    /**
     * 封面图
     */
    private String cover;
    /**
     * 简介
     */
    private String intro;
    /**
     * 创建时间
     */
    private Integer ctime;
    private Integer mtime;
    /**
     * 仅下载音频
     */
    private Integer onlyAudio;
}
