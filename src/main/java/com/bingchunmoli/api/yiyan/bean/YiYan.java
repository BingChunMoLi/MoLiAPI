package com.bingchunmoli.api.yiyan.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.bingchunmoli.api.bean.ApiConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
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
@Builder
@TableName(ApiConstant.YI_YAN_TABLE_NAME)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "一言", description = "一言")
public class YiYan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO) //主键自增 数据库中需要设置主键自增
    private Long id;

    private String uuid;

    @NotNull
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