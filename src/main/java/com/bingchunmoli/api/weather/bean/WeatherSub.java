package com.bingchunmoli.api.weather.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 天气订阅
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "天气订阅")
public class WeatherSub implements Serializable {
    /**
     * 
     */
    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订阅的城市
     */
    @Schema(description = "订阅的城市")
    private String location;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    @Serial
    @Schema(hidden = true)
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public String getLocation() {
        return location;
    }
}