package com.bingchunmoli.api.daily.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName account
 */
@TableName(value ="account")
@Data
public class Account implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 类型
     */
    @TableField(value = "type")
    private String type;
    /**
     *
     */
    @TableField(value = "method")
    private String method;
    /**
     * url
     */
    @TableField(value = "url")
    private String url;
    /**
     * cookies
     */
    @TableField(value = "cookies")
    private String cookies;
}
