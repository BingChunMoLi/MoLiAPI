package com.bingchunmoli.api.app.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PushTypeEnum {
    FCM("FCM"),
    HMS("HMS");

    private final String type;

    public static PushTypeEnum from(final String type) {
        if (type == null || type.isBlank()) {
            return FCM;
        }
        for (final PushTypeEnum value : values()) {
            if (value.type.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return FCM;
    }
}
