package com.bingchunmoli.api.down.bean;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LeagueJinx
 */
@Data
@NoArgsConstructor
public class Fav {

    private Integer code;
    private String message;
    private Integer ttl;
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private InfoDTO info;
        private List<MediasDTO> medias;
        private Boolean hasMore;

        @NoArgsConstructor
        @Data
        public static class InfoDTO {
            private Integer id;
            private Integer fid;
            private Integer mid;
            private Integer attr;
            private String title;
            private String cover;
            private UpperDTO upper;
            private Integer coverType;
            private CntInfoDTO cntInfo;
            private Integer type;
            private String intro;
            private Integer ctime;
            private Integer mtime;
            private Integer state;
            private Integer favState;
            private Integer likeState;
            private Integer mediaCount;

            @NoArgsConstructor
            @Data
            public static class UpperDTO {
                private Integer mid;
                private String name;
                private String face;
                private Boolean followed;
                private Integer vipType;
                private Integer vipStatue;
            }

            @NoArgsConstructor
            @Data
            public static class CntInfoDTO {
                private Integer collect;
                private Integer play;
                private Integer thumbUp;
                private Integer share;
            }
        }

        @NoArgsConstructor
        @Data
        public static class MediasDTO {
            private Integer id;
            private Integer type;
            private String title;
            private String cover;
            private String intro;
            private Integer page;
            private Integer duration;
            private UpperDTO upper;
            private Integer attr;
            private CntInfoDTO cntInfo;
            private String link;
            private Integer ctime;
            private Integer pubtime;
            private Integer favTime;
            private String bvId;
            private String bvid;
            private Object season;
            private Object ogv;
            private UgcDTO ugc;

            @NoArgsConstructor
            @Data
            public static class UpperDTO {
                private Integer mid;
                private String name;
                private String face;
            }

            @NoArgsConstructor
            @Data
            public static class CntInfoDTO {
                private Integer collect;
                private Integer play;
                private Integer danmaku;
            }

            @NoArgsConstructor
            @Data
            public static class UgcDTO {
                private Integer firstCid;
            }
        }
    }
}
