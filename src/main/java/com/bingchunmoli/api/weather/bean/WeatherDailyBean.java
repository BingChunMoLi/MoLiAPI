package com.bingchunmoli.api.weather.bean;

import lombok.Data;

import java.util.List;

/**
 * @author bingchunmoli
 */
@Data
public class WeatherDailyBean {
    /**
     * API状态码
     */
    private String code;
    /**
     * 当前API的最近更新时间
     */
    private String updateTime;
    /**
     * 当前数据的响应式页面，便于嵌入网站或应用
     */
    private String fxLink;
    private List<DailyBean> daily;
    private Refer refer;

    @Data
    public static class DailyBean {
        /**
         * 预报日期
         */
        String fxDate;
        /**
         * 日出时间，在高纬度地区可能为空
         */
        String sunrise;
        /**
         * 日落时间，在高纬度地区可能为空
         */
        String sunset;
        /**
         * 当天月升时间，可能为空
         */
        String moonRise;
        /**
         * 当天月落时间，可能为空
         */
        String moonSet;
        /**
         * 月相名称
         */
        String moonPhase;
        /**
         *预报当天最高温度
         */
        String tempMax;
        /**
         * 预报当天最低温度
         */
        String tempMin;
        /**
         * 预报白天天气状况的图标代码
         */
        String iconDay;
        /**
         * 预报白天天气状况文字描述
         */
        String textDay;
        /**
         * 预报夜间天气状况的图标代码
         */
        String iconNight;
        /**
         * 预报晚间天气状况文字描述
         */
        String textNight;
        /**
         * 预报白天风向360角度
         */
        String wind360Day;
        /**
         * 预报白天风向
         */
        String windDirDay;
        /**
         * 预报白天风力等级
         */
        String windScaleDay;
        /**
         * 预报白天风速，公里/小时
         */
        String windSpeedDay;
        /**
         * 预报夜间风向360角度
         */
        String wind360Night;
        /**
         * 预报夜间当天风向
         */
        String windDirNight;
        /**
         * 预报夜间风力等级
         */
        String windScaleNight;
        /**
         * 预报夜间风速，公里/小时
         */
        String windSpeedNight;
        /**
         * 相对湿度，百分比数值
         */
        String humidity;
        /**
         * 预报当天总降水量，默认单位：毫米
         */
        String precip;
        /**
         * 大气压强，默认单位：百帕
         */
        String pressure;
        /**
         * 能见度，默认单位：公里
         */
        String vis;
        /**
         * 云量，百分比数值。可能为空
         */
        String cloud;
        /**
         * 紫外线强度指数
         */
        String uvIndex;
        /**
         * 月相图标代码
         */
        String moonPhaseIcon;
    }
    @Data
    public static class Refer {
        /**
         * 原始数据来源，或数据源说明，可能为空
         */
        List<String> sourcesList;
        /**
         * 数据许可或版权声明，可能为空
         */
        List<String> licenseList;
    }
}