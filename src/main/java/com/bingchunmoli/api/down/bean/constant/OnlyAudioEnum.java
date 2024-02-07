package com.bingchunmoli.api.down.bean.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仅下载音频字段枚举
 *
 * @author MoLi
 */

@Getter
@AllArgsConstructor
public enum OnlyAudioEnum {
    //是
    TRUE(1),
    //否
    FALSE(0);

    /**
     * 是否仅下载音频
     */
    private final int onlyAudio;
}