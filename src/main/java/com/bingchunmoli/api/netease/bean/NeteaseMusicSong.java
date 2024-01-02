package com.bingchunmoli.api.netease.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 歌曲
 */

@Data
@Builder
@TableName(value ="netease_music_song")
public class NeteaseMusicSong implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "name")
    private String name;

    /**
     * 专辑id
     */
    @TableField(value = "album_id")
    private Integer albumId;

    @TableField(exist = false)
    private List<NeteaseMusicUser> artists;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
