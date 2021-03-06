package com.bingchunmoli.api.shici.bean;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Shici对象", description = "")
public class ShiCi implements Serializable {

    private static final long serialVersionUID = 1L;
//    @NotNull
    @TableId(type = IdType.AUTO) //主键自增 数据库中需要设置主键自增
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
    private LocalDateTime gmtCreate;
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
    @Version
    @JsonIgnore
    private String version;


}
