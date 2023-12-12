package com.bingchunmoli.api.app.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value ="device")
public class DeviceVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    private Integer id;
    //FCM token
    private String token;
    //设备厂商名称
    private String name;
    //设备型号
    private String model;
    //androidId
    private String androidId;
}
