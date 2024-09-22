package com.bingchunmoli.api.daily.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 网址实体k,v一对多
 * @author moli
 */
@Data
@AllArgsConstructor
public class Daily {
    private String key;
    private String value;
}
