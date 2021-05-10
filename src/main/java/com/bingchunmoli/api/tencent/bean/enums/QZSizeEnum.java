package com.bingchunmoli.api.tencent.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO
 * @Author 冰彦糖
 * @Data 2020/11/27 19:06
 * @ClassName QZSizeEnum
 * @PackageName: com.bingchunmoli.api.entity.enums
 * @Version 0.0.1-SNAPSHOT
 **/
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
    private String name;
    private Integer size;

}
