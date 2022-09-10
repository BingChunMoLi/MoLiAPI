package com.bingchunmoli.api.yiyan.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author 冰纯茉莉
 * @since 2020-11-11
 */
@Data
@TableName("yi_yan")
@EqualsAndHashCode(callSuper = false)
//@ApiModel(value = "Yiyan对象", description = "一言")
public class YiYan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) //主键自增 数据库中需要设置主键自增
    private Long id;

    private String uuid;

    private String hitokoto;

    private String type;

    @TableField("`from`")
    private String from;

    private String fromWho;

    private String creator;

    private Integer creatorUid;

    private Integer reviewer;

    private String commitFrom;

    private String createdAt;

    private Integer length;
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