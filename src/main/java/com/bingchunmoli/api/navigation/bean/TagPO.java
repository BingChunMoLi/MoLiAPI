package com.bingchunmoli.api.navigation.bean;

import lombok.Data;

/**
 * @author MoLi
 */
@Data
public class TagPO {

    private Integer id;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 是否可以打开标签组
     */
    private Boolean isOpen;
    /**
     * 是否是私有的
     */
    private Boolean isPrivate;
    /**
     * 如果是私有的提供密码
     */
    private String pwd;
}
