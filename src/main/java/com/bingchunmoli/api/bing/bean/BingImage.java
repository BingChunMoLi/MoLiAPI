package com.bingchunmoli.api.bing.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据库bing图片实体
 * @author bingchunmoli
 */
@Data
@Builder
@TableName("bing_image")
@NoArgsConstructor
@AllArgsConstructor
public class BingImage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String startDate;
    private String startDateEn;
    private String fullStartDate;
    private String fullStartDateEn;
    private String endDate;
    private String endDateEn;
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
        this.endDateEn = bingImagesEn.getEndDate();
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
