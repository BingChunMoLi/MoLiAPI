package com.bingchunmoli.api.down.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("ttl")
    private Integer ttl;
    @JsonProperty("data")
    private MediaDataDTO data;

    @NoArgsConstructor
    @Data
    public static class MediaDataDTO {
        @JsonProperty("bvid")
        private String bvid;
        @JsonProperty("aid")
        private Integer aid;
        @JsonProperty("videos")
        private Integer videos;
        @JsonProperty("tid")
        private Integer tid;
        @JsonProperty("tname")
        private String tname;
        @JsonProperty("copyright")
        private Integer copyright;
        @JsonProperty("pic")
        private String pic;
        @JsonProperty("title")
        private String title;
        @JsonProperty("pubdate")
        private Integer pubdate;
        @JsonProperty("ctime")
        private Integer ctime;
        @JsonProperty("desc")
        private String desc;
        @JsonProperty("desc_v2")
        private List<DescV2DTO> descV2;
        @JsonProperty("state")
        private Integer state;
        @JsonProperty("duration")
        private Integer duration;
        @JsonProperty("rights")
        private RightsDTO rights;
        @JsonProperty("owner")
        private OwnerDTO owner;
        @JsonProperty("stat")
        private StatDTO stat;
        @JsonProperty("dynamic")
        private String dynamic;
        @JsonProperty("cid")
        private Integer cid;
        @JsonProperty("dimension")
        private DimensionDTO dimension;
        @JsonProperty("premiere")
        private Object premiere;
        @JsonProperty("teenage_mode")
        private Integer teenageMode;
        @JsonProperty("no_cache")
        private Boolean noCache;
        @JsonProperty("pages")
        private List<PagesDTO> pages;
        @JsonProperty("subtitle")
        private SubtitleDTO subtitle;
        @JsonProperty("is_season_display")
        private Boolean isSeasonDisplay;
        @JsonProperty("user_garb")
        private UserGarbDTO userGarb;
        @JsonProperty("honor_reply")
        private HonorReplyDTO honorReply;

        @NoArgsConstructor
        @Data
        public static class RightsDTO {
            @JsonProperty("bp")
            private Integer bp;
            @JsonProperty("elec")
            private Integer elec;
            @JsonProperty("download")
            private Integer download;
            @JsonProperty("movie")
            private Integer movie;
            @JsonProperty("pay")
            private Integer pay;
            @JsonProperty("hd5")
            private Integer hd5;
            @JsonProperty("no_reprint")
            private Integer noReprint;
            @JsonProperty("autoplay")
            private Integer autoplay;
            @JsonProperty("ugc_pay")
            private Integer ugcPay;
            @JsonProperty("is_cooperation")
            private Integer isCooperation;
            @JsonProperty("ugc_pay_preview")
            private Integer ugcPayPreview;
            @JsonProperty("no_background")
            private Integer noBackground;
            @JsonProperty("clean_mode")
            private Integer cleanMode;
            @JsonProperty("is_stein_gate")
            private Integer isSteinGate;
            @JsonProperty("is_360")
            private Integer is360;
            @JsonProperty("no_share")
            private Integer noShare;
        }

        @NoArgsConstructor
        @Data
        public static class OwnerDTO {
            @JsonProperty("mid")
            private Integer mid;
            @JsonProperty("name")
            private String name;
            @JsonProperty("face")
            private String face;
        }

        @NoArgsConstructor
        @Data
        public static class StatDTO {
            @JsonProperty("aid")
            private Integer aid;
            @JsonProperty("view")
            private Integer view;
            @JsonProperty("danmaku")
            private Integer danmaku;
            @JsonProperty("reply")
            private Integer reply;
            @JsonProperty("favorite")
            private Integer favorite;
            @JsonProperty("coin")
            private Integer coin;
            @JsonProperty("share")
            private Integer share;
            @JsonProperty("now_rank")
            private Integer nowRank;
            @JsonProperty("his_rank")
            private Integer hisRank;
            @JsonProperty("like")
            private Integer like;
            @JsonProperty("dislike")
            private Integer dislike;
            @JsonProperty("evaluation")
            private String evaluation;
            @JsonProperty("argue_msg")
            private String argueMsg;
        }

        @NoArgsConstructor
        @Data
        public static class DimensionDTO {
            @JsonProperty("width")
            private Integer width;
            @JsonProperty("height")
            private Integer height;
            @JsonProperty("rotate")
            private Integer rotate;
        }

        @NoArgsConstructor
        @Data
        public static class SubtitleDTO {
            @JsonProperty("allow_submit")
            private Boolean allowSubmit;
            @JsonProperty("list")
            private List<?> list;
        }

        @NoArgsConstructor
        @Data
        public static class UserGarbDTO {
            @JsonProperty("url_image_ani_cut")
            private String urlImageAniCut;
        }

        @NoArgsConstructor
        @Data
        public static class HonorReplyDTO {
        }

        @NoArgsConstructor
        @Data
        public static class DescV2DTO {
            @JsonProperty("raw_text")
            private String rawText;
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("biz_id")
            private Integer bizId;
        }

        @NoArgsConstructor
        @Data
        public static class PagesDTO {
            @JsonProperty("cid")
            private Integer cid;
            @JsonProperty("page")
            private Integer page;
            @JsonProperty("from")
            private String from;
            @JsonProperty("part")
            private String part;
            @JsonProperty("duration")
            private Integer duration;
            @JsonProperty("vid")
            private String vid;
            @JsonProperty("weblink")
            private String weblink;
            @JsonProperty("dimension")
            private DimensionDTO dimension;

            @NoArgsConstructor
            @Data
            public static class DimensionDTO {
                @JsonProperty("width")
                private Integer width;
                @JsonProperty("height")
                private Integer height;
                @JsonProperty("rotate")
                private Integer rotate;
            }
        }
    }
}
