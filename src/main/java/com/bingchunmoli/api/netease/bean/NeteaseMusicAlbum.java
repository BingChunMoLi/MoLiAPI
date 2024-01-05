package com.bingchunmoli.api.netease.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 专辑
 */

@Data
@Builder
@TableName(value ="netease_music_album")
public class NeteaseMusicAlbum implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 第三方平台id
     */
    @TableField(value = "third_id")
    private Long thirdId;

    /**
     * 专辑名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 专辑图片
     */
    @TableField(value = "pic_url")
    private String picUrl;

    /**
     * 类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 发布时间
     */
    @TableField(value = "publish_time")
    private LocalDateTime publishTime;

    /**
     *
     */
    @TableField(value = "user_id")
    private Integer userId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
