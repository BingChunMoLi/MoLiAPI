package com.bingchunmoli.api.tencent.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author bingchunmoli
 */

@Getter
@AllArgsConstructor
public enum QZSizeEnum {

    //30*30 超小图
    SMALL("小",30),
    //50*50 中小图
    ZHONG_SMALL("中小",50),
    //100*100 中图
    ZHONG("中",100),
    //140*140 中大图
    ZHONG_BIG("大",200),
    //640*640 大图
    BIG("超大",640);
    private final String name;
    private final Integer size;

}
