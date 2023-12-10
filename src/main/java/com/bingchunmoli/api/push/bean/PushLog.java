package com.bingchunmoli.api.push.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 推送日志记录表
 */

@Data
@Accessors(chain = true)
@TableName(value ="push_log")
public class PushLog implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 1 mail,2 app,3 server
     */
    @TableField(value = "type")
    private Integer type;

    /**
     *
     */
    @TableField(value = "title")
    private String title;

    /**
     *
     */
    @TableField(value = "body")
    private String body;

    /**
     * device token or topic or toEmail
     */
    @TableField(value = "receive")
    private String receive;

    /**
     * 0 初始化，1已推送， 2失败
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
