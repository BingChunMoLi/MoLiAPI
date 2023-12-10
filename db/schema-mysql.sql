create database if not EXISTS api;
use api;
create table if not exists `bing_image`
(
    `id`                 bigint not null auto_increment,
    `start_date`         varchar(30) character set utf8mb4 collate utf8mb4_general_ci  default null comment '开始时间',
    `start_date_en`      varchar(30) character set utf8mb4 collate utf8mb4_general_ci  default null comment '国际版开始时间',
    `full_start_date`    varchar(30) character set utf8mb4 collate utf8mb4_general_ci  default null comment '开始时间完全版',
    `full_start_date_en` varchar(30) character set utf8mb4 collate utf8mb4_general_ci  default null comment '国际版开始时间完全版',
    `end_date`           varchar(30) character set utf8mb4 collate utf8mb4_general_ci  default null comment '结束时间',
    `url`                varchar(150) character set utf8mb4 collate utf8mb4_general_ci default null comment '图片地址',
    `url_en`             varchar(150) character set utf8mb4 collate utf8mb4_general_ci default null comment '国际版图片地址',
    `url_base`           varchar(70) character set utf8mb4 collate utf8mb4_general_ci  default null comment '图片地址基础不带清晰度',
    `url_base_en`        varchar(70) character set utf8mb4 collate utf8mb4_general_ci  default null comment '国际版图片地址基础不带清晰度',
    `copyright`          varchar(200) character set utf8mb4 collate utf8mb4_general_ci default null comment '版权信息',
    `copyright_en`       varchar(200) character set utf8mb4 collate utf8mb4_general_ci default null comment '国际版版权信息',
    `copyright_link`     varchar(200) character set utf8mb4 collate utf8mb4_general_ci default null comment '版权信息链接',
    `copyright_link_en`  varchar(200) character set utf8mb4 collate utf8mb4_general_ci default null comment '国际版版权信息链接',
    `headline_en`        varchar(100) character set utf8mb4 collate utf8mb4_general_ci default null comment '',
    `create_time`        datetime                                                      default null comment '创建时间',
    `obs_url_cn`         varchar(120) character set utf8mb4 collate utf8mb4_general_ci default null comment '中国版obs地址,已废弃',
    `obs_url_en`         varchar(120) character set utf8mb4 collate utf8mb4_general_ci default null comment '国际版obs地址,已废弃',
    `url_uhd`            varchar(255) character set utf8mb4 collate utf8mb4_general_ci default null comment '原图地址',
    `url_uhd_en`         varchar(255) character set utf8mb4 collate utf8mb4_general_ci default null comment '国际版原图地址',
    primary key (`id`) using btree
) engine = innodb
  default charset = utf8mb4
  row_format = dynamic;


-- api.host definition

create table if not exists `host`
(
    `id`     int not null auto_increment,
    `ip`     varchar(30) character set utf8mb4 collate utf8mb4_general_ci default null comment 'ip',
    `domain` varchar(60) character set utf8mb4 collate utf8mb4_general_ci default null comment '域名',
    `source` varchar(30) character set utf8mb4 collate utf8mb4_general_ci default null comment '来源',
    primary key (`id`) using btree,
    unique key `host_id_uindex` (`id`) using btree
) engine = innodb
  default charset = utf8mb4
  collate = utf8mb4_general_ci
  row_format = dynamic;


create table if not exists `navigation`
(
    `id`     int                          not null auto_increment primary key,
    `title`  varchar(30) comment '标题',
    `des`    varchar(300) comment '详细信息',
    `url`    varchar(1000) comment '网址',
    `icon`   varchar(1000) comment '图标',
    `tenant` varchar(30) default 'public' not null comment '租户'
);

create table if not exists `tag`
(
    `id`         int                   not null auto_increment primary key,
    `tag_name`   varchar(20)           not null comment '标签名称',
    `is_open`    boolean default false not null comment '是否可以打开标签组',
    `is_private` boolean default false not null comment '是否是私有的',
    `pwd`        varchar(50) comment '密码(如果是私有的需要密码)'
);


create table if not exists `navigation_tag`
(
    `id`            int not null auto_increment primary key,
    `tag_id`        int not null comment '标签id',
    `navigation_id` int not null comment '导航id'
);

-- api.shi_ci definition

CREATE table if not exists `shi_ci`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `content`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `origin`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `author`      varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL,
    `category`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `deleted`     int                                                     DEFAULT 0,
    `create_time` datetime                                                DEFAULT NULL,
    `update_time` datetime                                                DEFAULT NULL,
    `version`     int                                                     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC;


-- api.yi_yan definition
CREATE table if not exists `yi_yan`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `uuid`        char(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `hitokoto`    char(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `type`        char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL,
    `from`        char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `from_who`    char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `creator`     char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `creator_uid` int                                                        DEFAULT NULL,
    `reviewer`    int                                                        DEFAULT NULL,
    `commit_from` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `created_at`  char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    `length`      int                                                        DEFAULT NULL,
    `deleted`     int                                                        DEFAULT 0,
    `create_time` datetime                                                   DEFAULT NULL,
    `update_time` datetime                                                   DEFAULT NULL,
    `version`     int                                                        DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;

create table weather_sub
(
    id       int auto_increment primary key,
    location varchar(10) not null comment '订阅的城市',
    email    varchar(30) not null comment '邮箱'
)
    comment '天气订阅表';

CREATE TABLE api.daily_log
(
    id          BIGINT auto_increment      NOT NULL,
    url         varchar(700)               NOT NULL COMMENT '签到的url',
    tenant      TINYINT UNSIGNED DEFAULT 0 NOT NULL COMMENT '租户 1, moli',
    create_time DATETIME                   NOT NULL COMMENT '创建时间',
    `type`      TINYINT UNSIGNED           NOT NULL COMMENT '类型 1 网址 2 other',
    CONSTRAINT daily_log_PK PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

create table Device
(
    id         int auto_increment
        primary key,
    token      varchar(300) null,
    name       varchar(10)  null comment '设备名称',
    model      varchar(30)  null comment '设备型号',
    android_id varchar(60)  null comment '安卓Id'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

create table push_log
(
    id      int auto_increment,
    type    int           not null comment '1 mail,2 app,3 server',
    title   varchar(100)  not null,
    body    varchar(3000) null,
    receive varchar(60)   not null comment 'device token or topic or toEmail',
    status  tinyint       default 0 not null comment '0 初始化，1已推送， 2失败',
    constraint push_log_pk
        primary key (id)
)
    comment '推送日志记录表';
