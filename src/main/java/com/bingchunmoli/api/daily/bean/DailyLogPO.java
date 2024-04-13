package com.bingchunmoli.api.daily.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 */

@Data
@Accessors(chain = true)
@TableName(value = "daily_log")
public class DailyLogPO implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 签到的url
     */
    @TableField(value = "url")
    private String url;
    /**
     * 租户 1, moli
     */
    @TableField(value = "tenant")
    private Integer tenant;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 类型 1 网址 2 other
     */
    @TableField(value = "type")
    private Integer type;
}
