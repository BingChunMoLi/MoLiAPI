package com.bingchunmoli.api.shici.bean;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ShiCi implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private String origin;

    private String author;

    private String category;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    @JsonIgnore
    private String version;

}
