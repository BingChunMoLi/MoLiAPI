package com.bingchunmoli.api.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库类型
 * @author MoLi
 */
@Getter
@AllArgsConstructor
public enum DriveType {
    /**
     * 数据库类型
     */
    NONE(0, "NONE"),
    MYSQL(1, "MYSQL"),
    H2(2, "H2");

    private final int type;
    private final String driveName;
}
