package com.bingchunmoli.api.bing.bean;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author bingchunmoli
 */
@Data
@TableName("bing_image")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Bing每日图片", description = "<startdate>20201114</startdate>\n" +
        "<fullstartdate>202011140800</fullstartdate>\n" +
        "<enddate>20201115</enddate>\n" +
        "<url>/th?id=OHR.LupineNZ_ZH-CN0613960648_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp</url>\n" +
        "<urlBase>/th?id=OHR.LupineNZ_ZH-CN0613960648</urlBase>\n" +
        "<copyright>特卡波湖岸上的羽扇豆，新西兰 (© Stanislav Kachyna/Shutterstock)</copyright>\n" +
        "<copyrightlink>https://www.bing.com/search?q=%E7%BE%BD%E6%89%87%E8%B1%86&form=hpcapt&mkt=zh-cn</copyrightlink>")
public class BingImage implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String startDate;
    private String startDateEn;
    private String fullStartDate;
    private String fullStartDateEn;
    private String endDate;
    private String url;
    private String urlEn;
    private String urlBase;
    private String urlBaseEn;
    private String copyright;
    private String copyrightEn;
    private String copyrightLink;
    private String copyrightLinkEn;
    private String headlineEn;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private String urlUhd;
    private String urlUhdEn;
    private String obsUrlCn;
    private String obsUrlEn;

    public BingImage(BingImageVO cn, BingImageVO en) {
        BingImageVO.BingImages bingImagesCn = cn.getImages().get(0);
        BingImageVO.BingImages bingImagesEn = en.getImages().get(0);
        this.startDate = bingImagesCn.getStartDate();
        this.startDateEn = bingImagesEn.getStartDate();
        this.fullStartDate = bingImagesCn.getFullStartDate();
        this.fullStartDateEn = bingImagesEn.getFullStartDate();
        this.endDate = bingImagesCn.getEndDate();
        this.url = bingImagesCn.getUrl();
        this.urlEn = bingImagesEn.getUrl();
        this.urlBase = bingImagesCn.getUrlBase();
        this.urlBaseEn = bingImagesEn.getUrlBase();
        this.urlUhd = bingImagesCn.getUrlBase() + "_UHD.jpg";
        this.urlUhdEn = bingImagesEn.getUrlBase() + "_UHD.jpg";
        this.copyright = bingImagesCn.getCopyright();
        this.copyrightEn = bingImagesEn.getCopyright();
        this.copyrightLink = bingImagesCn.getCopyrightLink();
        this.copyrightLinkEn = bingImagesEn.getCopyrightLink();
        this.headlineEn = bingImagesEn.getTitle();
        this.createTime = LocalDateTime.now();
    }
}