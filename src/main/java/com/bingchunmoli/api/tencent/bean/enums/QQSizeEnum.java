package com.bingchunmoli.api.tencent.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: QQ头像大小的枚举类
 * @Author 冰彦糖
 * @Data 2020/11/27 18:42
 * @ClassName QQSizeEnum
 * @PackageName: com.bingchunmoli.api.entity.enums
 * @Version 0.0.1-SNAPSHOT
 **/
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

    private String name;
    private Integer size;

}