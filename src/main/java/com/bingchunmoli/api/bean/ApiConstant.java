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
    public static final String YI_YAN_SCHEMA_PATH_MYSQL = ResourceUtils.CLASSPATH_URL_PREFIX + "db/schema-mysql-yiyan.sql";
    public static final String YI_YAN_DATA_PATH_MYSQL = ResourceUtils.CLASSPATH_URL_PREFIX + "db/data-mysql-yiyan.sql";
    public static final String YI_YAN_SCHEMA_PATH_H2 = ResourceUtils.CLASSPATH_URL_PREFIX + "db/schema-h2-yiyan.sql";
    public static final String YI_YAN_DATA_PATH_H2 = ResourceUtils.CLASSPATH_URL_PREFIX + "db/data-h2-yiyan.sql";
    public static final String YI_YAN_DATA_PATH = ResourceUtils.CLASSPATH_URL_PREFIX + "db/yiyan.data";
    public static final String SHI_CI_SCHEMA_PATH_MYSQL = ResourceUtils.CLASSPATH_URL_PREFIX + "db/schema-mysql-shici.sql";
    public static final String SHI_CI_DATA_PATH_MYSQL = ResourceUtils.CLASSPATH_URL_PREFIX + "db/data-mysql-shici.sql";
    public static final String SHI_CI_SCHEMA_PATH_H2 = ResourceUtils.CLASSPATH_URL_PREFIX + "db/schema-h2-shici.sql";
    public static final String SHI_CI_DATA_PATH_H2 = ResourceUtils.CLASSPATH_URL_PREFIX + "db/data-h2-shici.sql";
    public static final String SHI_CI_DATA_PATH = ResourceUtils.CLASSPATH_URL_PREFIX + "db/shici.data";


}
