package com.bingchunmoli.api.navigation.bean;

import cn.hutool.core.lang.RegexPool;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author MoLi
 */
@Data
@TableName("navigation")
@EqualsAndHashCode
public class NavigationPO {

    private Integer id;
    private String title;
    private String des;
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String url;
    private String icon;
    private String tenant;

//    public void setUrl(String url) {
//        if (Validator.isUrl(url)) {
//            this.url = url;
//        }
//        throw new ApiParamException("url param is not url");
//    }
}
