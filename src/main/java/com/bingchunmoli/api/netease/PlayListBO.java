package com.bingchunmoli.api.netease;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PlayListBO {

    @JsonProperty("result")
    private ResultDTO result;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("message")
    private String message;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("subscribers")
        private List<?> subscribers;
        @JsonProperty("subscribed")
        private Boolean subscribed;
        @JsonProperty("creator")
        private CreatorDTO creator;
        @JsonProperty("artists")
        private Object artists;
        @JsonProperty("tracks")
        private List<TracksDTO> tracks;
        @JsonProperty("updateFrequency")
        private Object updateFrequency;
        @JsonProperty("backgroundCoverId")
        private Integer backgroundCoverId;
        @JsonProperty("backgroundCoverUrl")
        private Object backgroundCoverUrl;
        @JsonProperty("titleImage")
        private Integer titleImage;
        @JsonProperty("coverText")
        private Object coverText;
        @JsonProperty("titleImageUrl")
        private Object titleImageUrl;
        @JsonProperty("coverImageUrl")
        private Object coverImageUrl;
        @JsonProperty("iconImageUrl")
        private Object iconImageUrl;
        @JsonProperty("englishTitle")
        private Object englishTitle;
        @JsonProperty("opRecommend")
        private Boolean opRecommend;
        @JsonProperty("recommendInfo")
        private Object recommendInfo;
        @JsonProperty("socialPlaylistCover")
        private Object socialPlaylistCover;
        @JsonProperty("tsSongCount")
        private Integer tsSongCount;
        @JsonProperty("trackNumberUpdateTime")
        private Long trackNumberUpdateTime;
        @JsonProperty("cloudTrackCount")
        private Integer cloudTrackCount;
        @JsonProperty("subscribedCount")
        private Integer subscribedCount;
        @JsonProperty("anonimous")
        private Boolean anonimous;
        @JsonProperty("newImported")
        private Boolean newImported;
        @JsonProperty("privacy")
        private Integer privacy;
        @JsonProperty("coverImgUrl")
        private String coverImgUrl;
        @JsonProperty("updateTime")
        private Long updateTime;
        @JsonProperty("adType")
        private Integer adType;
        @JsonProperty("coverImgId")
        private Long coverImgId;
        @JsonProperty("specialType")
        private Integer specialType;
        @JsonProperty("totalDuration")
        private Integer totalDuration;
        @JsonProperty("trackUpdateTime")
        private Long trackUpdateTime;
        @JsonProperty("trackCount")
        private Integer trackCount;
        @JsonProperty("commentThreadId")
        private String commentThreadId;
        @JsonProperty("playCount")
        private Integer playCount;
        @JsonProperty("highQuality")
        private Boolean highQuality;
        @JsonProperty("createTime")
        private Long createTime;
        @JsonProperty("ordered")
        private Boolean ordered;
        @JsonProperty("description")
        private Object description;
        @JsonProperty("status")
        private Integer status;
        @JsonProperty("tags")
        private List<?> tags;
        @JsonProperty("userId")
        private Integer userId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("id")
        private Long id;
        @JsonProperty("shareCount")
        private Integer shareCount;
        @JsonProperty("commentCount")
        private Integer commentCount;

        @NoArgsConstructor
        @Data
        public static class CreatorDTO {
            @JsonProperty("defaultAvatar")
            private Boolean defaultAvatar;
            @JsonProperty("province")
            private Integer province;
            @JsonProperty("authStatus")
            private Integer authStatus;
            @JsonProperty("followed")
            private Boolean followed;
            @JsonProperty("avatarUrl")
            private String avatarUrl;
            @JsonProperty("accountStatus")
            private Integer accountStatus;
            @JsonProperty("gender")
            private Integer gender;
            @JsonProperty("city")
            private Integer city;
            @JsonProperty("birthday")
            private Long birthday;
            @JsonProperty("userId")
            private Integer userId;
            @JsonProperty("userType")
            private Integer userType;
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("signature")
            private String signature;
            @JsonProperty("description")
            private String description;
            @JsonProperty("detailDescription")
            private String detailDescription;
            @JsonProperty("avatarImgId")
            private Long avatarImgId;
            @JsonProperty("backgroundImgId")
            private Long backgroundImgId;
            @JsonProperty("backgroundUrl")
            private String backgroundUrl;
            @JsonProperty("authority")
            private Integer authority;
            @JsonProperty("mutual")
            private Boolean mutual;
            @JsonProperty("expertTags")
            private Object expertTags;
            @JsonProperty("experts")
            private Object experts;
            @JsonProperty("djStatus")
            private Integer djStatus;
            @JsonProperty("vipType")
            private Integer vipType;
            @JsonProperty("remarkName")
            private Object remarkName;
            @JsonProperty("authenticationTypes")
            private Integer authenticationTypes;
            @JsonProperty("avatarDetail")
            private Object avatarDetail;
            @JsonProperty("avatarImgIdStr")
            private String avatarImgIdStr;
            @JsonProperty("backgroundImgIdStr")
            private String backgroundImgIdStr;
            @JsonProperty("anchor")
            private Boolean anchor;
            @JsonProperty("avatarImgId_str")
            private String avatarimgidStr;
        }

        @NoArgsConstructor
        @Data
        public static class TracksDTO {
            @JsonProperty("name")
            private String name;
            @JsonProperty("id")
            private Integer id;
            @JsonProperty("position")
            private Integer position;
            @JsonProperty("alias")
            private List<?> alias;
            @JsonProperty("status")
            private Integer status;
            @JsonProperty("fee")
            private Integer fee;
            @JsonProperty("copyrightId")
            private Integer copyrightId;
            @JsonProperty("disc")
            private String disc;
            @JsonProperty("no")
            private Integer no;
            @JsonProperty("artists")
            private List<ArtistsDTO> artists;
            @JsonProperty("album")
            private AlbumDTO album;
            @JsonProperty("starred")
            private Boolean starred;
            @JsonProperty("popularity")
            private Double popularity;
            @JsonProperty("score")
            private Integer score;
            @JsonProperty("starredNum")
            private Integer starredNum;
            @JsonProperty("duration")
            private Integer duration;
            @JsonProperty("playedNum")
            private Integer playedNum;
            @JsonProperty("dayPlays")
            private Integer dayPlays;
            @JsonProperty("hearTime")
            private Integer hearTime;
            @JsonProperty("sqMusic")
            private Object sqMusic;
            @JsonProperty("hrMusic")
            private Object hrMusic;
            @JsonProperty("ringtone")
            private String ringtone;
            @JsonProperty("crbt")
            private Object crbt;
            @JsonProperty("audition")
            private Object audition;
            @JsonProperty("copyFrom")
            private String copyFrom;
            @JsonProperty("commentThreadId")
            private String commentThreadId;
            @JsonProperty("rtUrl")
            private Object rtUrl;
            @JsonProperty("ftype")
            private Integer ftype;
            @JsonProperty("rtUrls")
            private List<?> rtUrls;
            @JsonProperty("copyright")
            private Integer copyright;
            @JsonProperty("transName")
            private Object transName;
            @JsonProperty("sign")
            private Object sign;
            @JsonProperty("mark")
            private Integer mark;
            @JsonProperty("originCoverType")
            private Integer originCoverType;
            @JsonProperty("originSongSimpleData")
            private Object originSongSimpleData;
            @JsonProperty("single")
            private Integer single;
            @JsonProperty("noCopyrightRcmd")
            private Object noCopyrightRcmd;
            @JsonProperty("rtype")
            private Integer rtype;
            @JsonProperty("rurl")
            private Object rurl;
            @JsonProperty("mvid")
            private Integer mvid;
            @JsonProperty("bMusic")
            private BMusicDTO bMusic;
            @JsonProperty("mp3Url")
            private Object mp3Url;
            @JsonProperty("hMusic")
            private HMusicDTO hMusic;
            @JsonProperty("mMusic")
            private MMusicDTO mMusic;
            @JsonProperty("lMusic")
            private LMusicDTO lMusic;
            @JsonProperty("transNames")
            private List<String> transNames;

            @NoArgsConstructor
            @Data
            public static class AlbumDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("id")
                private Integer id;
                @JsonProperty("type")
                private String type;
                @JsonProperty("size")
                private Integer size;
                @JsonProperty("picId")
                private Long picId;
                @JsonProperty("blurPicUrl")
                private String blurPicUrl;
                @JsonProperty("companyId")
                private Integer companyId;
                @JsonProperty("pic")
                private Long pic;
                @JsonProperty("picUrl")
                private String picUrl;
                @JsonProperty("publishTime")
                private Long publishTime;
                @JsonProperty("description")
                private String description;
                @JsonProperty("tags")
                private String tags;
                @JsonProperty("company")
                private String company;
                @JsonProperty("briefDesc")
                private String briefDesc;
                @JsonProperty("artist")
                private ArtistDTO artist;
                @JsonProperty("songs")
                private List<?> songs;
                @JsonProperty("alias")
                private List<?> alias;
                @JsonProperty("status")
                private Integer status;
                @JsonProperty("copyrightId")
                private Integer copyrightId;
                @JsonProperty("commentThreadId")
                private String commentThreadId;
                @JsonProperty("artists")
                private List<ArtistsDTO> artists;
                @JsonProperty("subType")
                private String subType;
                @JsonProperty("transName")
                private Object transName;
                @JsonProperty("onSale")
                private Boolean onSale;
                @JsonProperty("mark")
                private Integer mark;
                @JsonProperty("gapless")
                private Integer gapless;
                @JsonProperty("dolbyMark")
                private Integer dolbyMark;
                @JsonProperty("picId_str")
                private String picIdStr;

                @NoArgsConstructor
                @Data
                public static class ArtistDTO {
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("id")
                    private Integer id;
                    @JsonProperty("picId")
                    private Integer picId;
                    @JsonProperty("img1v1Id")
                    private Integer img1v1Id;
                    @JsonProperty("briefDesc")
                    private String briefDesc;
                    @JsonProperty("picUrl")
                    private String picUrl;
                    @JsonProperty("img1v1Url")
                    private String img1v1Url;
                    @JsonProperty("albumSize")
                    private Integer albumSize;
                    @JsonProperty("alias")
                    private List<?> alias;
                    @JsonProperty("trans")
                    private String trans;
                    @JsonProperty("musicSize")
                    private Integer musicSize;
                    @JsonProperty("topicPerson")
                    private Integer topicPerson;
                }

                @NoArgsConstructor
                @Data
                public static class ArtistsDTO {
                    @JsonProperty("name")
                    private String name;
                    @JsonProperty("id")
                    private Integer id;
                    @JsonProperty("picId")
                    private Integer picId;
                    @JsonProperty("img1v1Id")
                    private Integer img1v1Id;
                    @JsonProperty("briefDesc")
                    private String briefDesc;
                    @JsonProperty("picUrl")
                    private String picUrl;
                    @JsonProperty("img1v1Url")
                    private String img1v1Url;
                    @JsonProperty("albumSize")
                    private Integer albumSize;
                    @JsonProperty("alias")
                    private List<?> alias;
                    @JsonProperty("trans")
                    private String trans;
                    @JsonProperty("musicSize")
                    private Integer musicSize;
                    @JsonProperty("topicPerson")
                    private Integer topicPerson;
                }
            }

            @NoArgsConstructor
            @Data
            public static class BMusicDTO {
                @JsonProperty("name")
                private Object name;
                @JsonProperty("id")
                private Long id;
                @JsonProperty("size")
                private Integer size;
                @JsonProperty("extension")
                private String extension;
                @JsonProperty("sr")
                private Integer sr;
                @JsonProperty("dfsId")
                private Integer dfsId;
                @JsonProperty("bitrate")
                private Integer bitrate;
                @JsonProperty("playTime")
                private Integer playTime;
                @JsonProperty("volumeDelta")
                private Double volumeDelta;
            }

            @NoArgsConstructor
            @Data
            public static class HMusicDTO {
                @JsonProperty("name")
                private Object name;
                @JsonProperty("id")
                private Long id;
                @JsonProperty("size")
                private Integer size;
                @JsonProperty("extension")
                private String extension;
                @JsonProperty("sr")
                private Integer sr;
                @JsonProperty("dfsId")
                private Integer dfsId;
                @JsonProperty("bitrate")
                private Integer bitrate;
                @JsonProperty("playTime")
                private Integer playTime;
                @JsonProperty("volumeDelta")
                private Double volumeDelta;
            }

            @NoArgsConstructor
            @Data
            public static class MMusicDTO {
                @JsonProperty("name")
                private Object name;
                @JsonProperty("id")
                private Long id;
                @JsonProperty("size")
                private Integer size;
                @JsonProperty("extension")
                private String extension;
                @JsonProperty("sr")
                private Integer sr;
                @JsonProperty("dfsId")
                private Integer dfsId;
                @JsonProperty("bitrate")
                private Integer bitrate;
                @JsonProperty("playTime")
                private Integer playTime;
                @JsonProperty("volumeDelta")
                private Double volumeDelta;
            }

            @NoArgsConstructor
            @Data
            public static class LMusicDTO {
                @JsonProperty("name")
                private Object name;
                @JsonProperty("id")
                private Long id;
                @JsonProperty("size")
                private Integer size;
                @JsonProperty("extension")
                private String extension;
                @JsonProperty("sr")
                private Integer sr;
                @JsonProperty("dfsId")
                private Integer dfsId;
                @JsonProperty("bitrate")
                private Integer bitrate;
                @JsonProperty("playTime")
                private Integer playTime;
                @JsonProperty("volumeDelta")
                private Double volumeDelta;
            }

            @NoArgsConstructor
            @Data
            public static class ArtistsDTO {
                @JsonProperty("name")
                private String name;
                @JsonProperty("id")
                private Integer id;
                @JsonProperty("picId")
                private Integer picId;
                @JsonProperty("img1v1Id")
                private Integer img1v1Id;
                @JsonProperty("briefDesc")
                private String briefDesc;
                @JsonProperty("picUrl")
                private String picUrl;
                @JsonProperty("img1v1Url")
                private String img1v1Url;
                @JsonProperty("albumSize")
                private Integer albumSize;
                @JsonProperty("alias")
                private List<?> alias;
                @JsonProperty("trans")
                private String trans;
                @JsonProperty("musicSize")
                private Integer musicSize;
                @JsonProperty("topicPerson")
                private Integer topicPerson;
            }
        }
    }
}
