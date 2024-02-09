package com.bingchunmoli.api.down.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LeagueJinx
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fav {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("ttl")
    private Integer ttl;
    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("info")
        private InfoDTO info;
        @JsonProperty("medias")
        private List<MediasDTO> medias;
        @JsonProperty("has_more")
        private Boolean hasMore;
        @JsonProperty("ttl")
        private Integer ttl;

        @NoArgsConstructor
        @Data
        public static class InfoDTO {
            @JsonProperty("id")
            private Integer id;
            @JsonProperty("fid")
            private Integer fid;
            @JsonProperty("mid")
            private Integer mid;
            @JsonProperty("attr")
            private Integer attr;
            @JsonProperty("title")
            private String title;
            @JsonProperty("cover")
            private String cover;
            @JsonProperty("upper")
            private UpperDTO upper;
            @JsonProperty("cover_type")
            private Integer coverType;
            @JsonProperty("cnt_info")
            private CntInfoDTO cntInfo;
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("intro")
            private String intro;
            @JsonProperty("ctime")
            private Integer ctime;
            @JsonProperty("mtime")
            private Integer mtime;
            @JsonProperty("state")
            private Integer state;
            @JsonProperty("fav_state")
            private Integer favState;
            @JsonProperty("like_state")
            private Integer likeState;
            @JsonProperty("media_count")
            private Integer mediaCount;

            @NoArgsConstructor
            @Data
            public static class UpperDTO {
                @JsonProperty("mid")
                private Integer mid;
                @JsonProperty("name")
                private String name;
                @JsonProperty("face")
                private String face;
                @JsonProperty("followed")
                private Boolean followed;
                @JsonProperty("vip_type")
                private Integer vipType;
                @JsonProperty("vip_statue")
                private Integer vipStatue;
            }

            @NoArgsConstructor
            @Data
            public static class CntInfoDTO {
                @JsonProperty("collect")
                private Integer collect;
                @JsonProperty("play")
                private Integer play;
                @JsonProperty("thumb_up")
                private Integer thumbUp;
                @JsonProperty("share")
                private Integer share;
            }
        }

        @NoArgsConstructor
        @Data
        public static class MediasDTO {
            @JsonProperty("id")
            private Integer id;
            @JsonProperty("type")
            private Integer type;
            @JsonProperty("title")
            private String title;
            @JsonProperty("cover")
            private String cover;
            @JsonProperty("intro")
            private String intro;
            @JsonProperty("page")
            private Integer page;
            @JsonProperty("duration")
            private Integer duration;
            @JsonProperty("upper")
            private UpperDTO upper;
            @JsonProperty("attr")
            private Integer attr;
            @JsonProperty("cnt_info")
            private CntInfoDTO cntInfo;
            @JsonProperty("link")
            private String link;
            @JsonProperty("ctime")
            private Integer ctime;
            @JsonProperty("pubtime")
            private Integer pubtime;
            @JsonProperty("fav_time")
            private Integer favTime;
            @JsonProperty("bv_id")
            private String bvId;
            @JsonProperty("bvid")
            private String bvid;
            @JsonProperty("season")
            private Object season;
            @JsonProperty("ogv")
            private Object ogv;
            @JsonProperty("ugc")
            private UgcDTO ugc;

            @NoArgsConstructor
            @Data
            public static class UpperDTO {
                @JsonProperty("mid")
                private Integer mid;
                @JsonProperty("name")
                private String name;
                @JsonProperty("face")
                private String face;
            }

            @NoArgsConstructor
            @Data
            public static class CntInfoDTO {
                @JsonProperty("collect")
                private Integer collect;
                @JsonProperty("play")
                private Integer play;
                @JsonProperty("danmaku")
                private Integer danmaku;
                @JsonProperty("vt")
                private Integer vt;
                @JsonProperty("play_switch")
                private Integer playSwitch;
                @JsonProperty("reply")
                private Integer reply;
                @JsonProperty("view_text_1")
                private String viewText1;
            }

            @NoArgsConstructor
            @Data
            public static class UgcDTO {
                @JsonProperty("first_cid")
                private Integer firstCid;
            }
        }
    }
}
