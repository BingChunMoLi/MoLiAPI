package com.bingchunmoli.api.app.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "device")
public class DeviceVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String token;
    private String hmsToken;
    private String pushType;
    private String name;
    private String model;
    private String androidId;

    public PushTypeEnum getPushTypeEnum() {
        return PushTypeEnum.from(pushType);
    }
}
