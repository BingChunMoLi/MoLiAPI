package com.bingchunmoli.api.push.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author moli
 */
@Getter
@AllArgsConstructor
public enum PushLogStatusEnum {

            CREATED(0, "创建"),
            SUCCESS(1, "成功"),
            FAIL(2, "失败");
            private final Integer status;
            private final String name;
}
