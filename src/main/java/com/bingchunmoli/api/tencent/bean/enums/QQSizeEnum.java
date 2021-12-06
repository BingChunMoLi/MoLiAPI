package com.bingchunmoli.api.tencent.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum QQSizeEnum {

    //40*40 小图
    SMALL("小",40),
    //100*100 中小图
    ZHONG_SMALL("中小",100),
    //140*140 中图
    ZHONG("中",140),
    //140*140 中大图
    ZHONG_BIG("中大",160),
    //240*240 大图
//    BIG("大",240),
    //640*640 超大图
    BIG("超大",640);

    private final String name;
    private final Integer size;

}