package com.bingchunmoli.api.netease.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户
 */

@Data
@Builder
@TableName(value ="netease_music_user")
public class NeteaseMusicUser implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "third_id")
    private Long thirdId;

    /**
     *
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 城市
     */
    @TableField(value = "city")
    private Integer city;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private Long birthday;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     *
     */
    @TableField(value = "background_img")
    private String backgroundImg;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
