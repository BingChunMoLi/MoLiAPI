package com.bingchunmoli.api.weather.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 天气接口返回实体
 * @author bingchunmoli
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherVO {

    @JsonProperty("code")
    private String code;
    @JsonProperty("location")
    private List<LocationDTO> location;
    @JsonProperty("refer")
    private ReferDTO refer;

    @NoArgsConstructor
    @Data
    public static class ReferDTO {
        @JsonProperty("sources")
        private List<String> sources;
        @JsonProperty("license")
        private List<String> license;
    }

    @NoArgsConstructor
    @Data
    public static class LocationDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("id")
        private String id;
        @JsonProperty("lat")
        private String lat;
        @JsonProperty("lon")
        private String lon;
        @JsonProperty("adm2")
        private String adm2;
        @JsonProperty("adm1")
        private String adm1;
        @JsonProperty("country")
        private String country;
        @JsonProperty("tz")
        private String tz;
        @JsonProperty("utcOffset")
        private String utcOffset;
        @JsonProperty("isDst")
        private String isDst;
        @JsonProperty("type")
        private String type;
        @JsonProperty("rank")
        private String rank;
        @JsonProperty("fxLink")
        private String fxLink;


    }
}
