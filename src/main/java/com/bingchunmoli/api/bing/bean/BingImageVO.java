package com.bingchunmoli.api.bing.bean;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author BingChunMoLi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="Bing每日图片", description="{\"images\":[{\"startdate\":\"20211007\",\"fullstartdate\":\"202110071600\",\"enddate\":\"20211008\",\"url\":\"/th?id=OHR.FriendlyOctopus_ZH-CN2519447724_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp\",\"urlbase\":\"/th?id=OHR.FriendlyOctopus_ZH-CN2519447724\",\"copyright\":\"海草中的章鱼，法国利翁湾 (© BIOSPHOTO/Alamy)\",\"copyrightlink\":\"https://www.bing.com/search?q=%E7%AB%A0%E9%B1%BC&form=hpcapt&mkt=zh-cn\",\"title\":\"\",\"quiz\":\"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20211007_FriendlyOctopus%22&FORM=HPQUIZ\",\"wp\":true,\"hsh\":\"2e4cdcc79efcd8dd814ce96c12f19125\",\"drk\":1,\"top\":1,\"bot\":1,\"hs\":[]}],\"tooltips\":{\"loading\":\"正在加载...\",\"previous\":\"上一个图像\",\"next\":\"下一个图像\",\"walle\":\"此图片不能下载用作壁纸。\",\"walls\":\"下载今日美图。仅限用作桌面壁纸。\"}}\n")
public class BingImageVO implements Serializable {
    private static final long serialVersionUID = 2L;
//    @JSONField(name = "images")
    private List<BingImages> images;
//    @JSONField(name = "tooltips")
    private BingTooltips tooltips;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class BingImages implements Serializable{
        private static final long serialVersionUID = 2L;
//        @JSONField(name = "startdate")
        private String startDate;
//        @JSONField(name = "fullStartdate")
        private String fullStartDate;
//        @JSONField(name = "enddate")
        private String endDate;
        private String url;
//        @JSONField(name = "urlbase")
        private String urlBase;
        private String copyright;
//        @JSONField(name = "copyrightlink")
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
    static class BingTooltips implements Serializable{
        private static final long serialVersionUID = 2L;
        private String loading;
        private String previous;
        private String next;
        private String walle;
        private String walls;
    }
}
