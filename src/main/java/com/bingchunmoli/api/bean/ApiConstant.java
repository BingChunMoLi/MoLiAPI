package com.bingchunmoli.api.bean;

import org.springframework.util.ResourceUtils;

/**
 * Api常量
 *
 * @author BingChunMoLi
 */
public class ApiConstant {
    public static final String YI_YAN = "yiYan";
    public static final String SHI_CI = "shiCi";
    public static final String PC_IMG = "pc";
    public static final String MOBILE_IMG = "mobile";
    public static final String SCHEMA_PATH_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX + "db/schema-";
    public static final String DATA_PATH_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX + "db/data-";
    public static final String SQL_PATH_SUFFIX = ".sql";
    public static final String YI_YAN_DATA_PATH = ResourceUtils.CLASSPATH_URL_PREFIX + "db/yiyan.data";
    public static final String SHI_CI_DATA_PATH = ResourceUtils.CLASSPATH_URL_PREFIX + "db/shici.data";


}
