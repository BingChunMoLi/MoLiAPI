package com.bingchunmoli.api.daily.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author moli
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLog implements Serializable {

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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 类型 1 网址 2 other
     */
    @TableField(value = "type")
    private Integer type;
    /**
     * 创建时间
     */
    private LocalDate createTime;
}
