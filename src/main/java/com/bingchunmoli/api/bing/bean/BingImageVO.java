package com.bingchunmoli.api.bing.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * bing接口实体
 * @author BingChunMoLi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingImageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private List<BingImages> images;
    private BingTooltips tooltips;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class BingImages implements Serializable {
        @Serial
        private static final long serialVersionUID = 2L;
        @JsonProperty("startdate")
        private String startDate;
        @JsonProperty("fullstartdate")
        private String fullStartDate;
        @JsonProperty("enddate")
        private String endDate;
        private String url;
        @JsonProperty("urlbase")
        private String urlBase;
        private String copyright;
        @JsonProperty("copyrightlink")
        private String copyrightLink;
        private String title;
        private String quiz;
        private Boolean wp;
        private String hsh;
        private Integer drk;
        private Integer top;
        private Integer bot;
        private List<Object> hs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class BingTooltips implements Serializable {
        @Serial
        private static final long serialVersionUID = 2L;
        private String loading;
        private String previous;
        private String next;
        private String walle;
        private String walls;
    }
}
