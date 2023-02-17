CREATE TABLE `bing_image`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `start_date`         varchar(30)  DEFAULT NULL comment '开始时间',
    `start_date_en`      varchar(30)  DEFAULT NULL comment '国际版开始时间',
    `full_start_date`    varchar(30)  DEFAULT NULL comment '开始时间完全版',
    `full_start_date_en` varchar(30)  DEFAULT NULL comment '国际版开始时间完全版',
    `end_date`           varchar(30)  DEFAULT NULL comment '结束时间',
    `url`                varchar(150) DEFAULT NULL comment '图片地址',
    `url_en`             varchar(150) DEFAULT NULL comment '国际版图片地址',
    `url_base`           varchar(70)  DEFAULT NULL comment '图片地址基础不带清晰度',
    `url_base_en`        varchar(70)  DEFAULT NULL comment '国际版图片地址基础不带清晰度',
    `copyright`          varchar(200) DEFAULT NULL comment '版权信息',
    `copyright_en`       varchar(200) DEFAULT NULL comment '国际版版权信息',
    `copyright_link`     varchar(200) DEFAULT NULL comment '版权信息链接',
    `copyright_link_en`  varchar(200) DEFAULT NULL comment '国际版版权信息链接',
    `headline_en`        varchar(100) DEFAULT NULL comment '',
    `create_time`        datetime     DEFAULT NULL comment '创建时间',
    `obs_url_cn`         varchar(120) DEFAULT NULL comment '中国版obs地址,已废弃',
    `obs_url_en`         varchar(120) DEFAULT NULL comment '国际版obs地址,已废弃',
    `url_uhd`            varchar(255) DEFAULT NULL comment '原图地址',
    `url_uhd_en`         varchar(255) DEFAULT NULL comment '国际版原图地址',
    PRIMARY KEY (`id`)
);

CREATE TABLE `host`
(
    `id`     int NOT NULL AUTO_INCREMENT,
    `ip`     varchar(30) DEFAULT NULL comment 'ip',
    `domain` varchar(60) DEFAULT NULL comment '域名',
    `source` varchar(30) DEFAULT NULL comment '来源',
    PRIMARY KEY (`id`)
);

CREATE TABLE `navigation`
(
    `id`     int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title`  varchar(30) comment '标题',
    `des`    varchar(300) comment '详细信息',
    `url`    varchar(1000) comment '网址',
    `icon`   varchar(1000) comment '图标',
    `tenant` varchar(30) default 'public' not null comment '租户'
);

create table `tag`
(
    `id`         int                   not null auto_increment primary key,
    `tag_name`   varchar(20)           not null comment '标签名称',
    `is_open`    boolean default false not null comment '是否可以打开标签组',
    `is_private` boolean default false not null comment '是否是私有的',
    `pwd`        varchar(50) comment '密码(如果是私有的需要密码)'
);


create table `navigation_tag`
(
    `id`            int not null auto_increment primary key,
    `tag_id`        int not null comment '标签id',
    `navigation_id` int not null comment '导航id'
);

CREATE TABLE `shi_ci`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `content`     varchar(100) DEFAULT NULL,
    `origin`      varchar(100) DEFAULT NULL,
    `author`      varchar(10)  DEFAULT NULL,
    `category`    varchar(100) DEFAULT NULL,
    `deleted`     int          DEFAULT '0',
    `create_time` datetime     DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `version`     int          DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `yi_yan`
(
    `id`          int NOT NULL,
    `uuid`        char(40)  DEFAULT NULL,
    `hitokoto`    char(100) DEFAULT NULL,
    `type`        char(5)   DEFAULT NULL,
    `from`        char(50)  DEFAULT NULL,
    `from_who`    char(20)  DEFAULT NULL,
    `creator`     char(20)  DEFAULT NULL,
    `creator_uid` int       DEFAULT NULL,
    `reviewer`    int       DEFAULT NULL,
    `commit_from` char(20)  DEFAULT NULL,
    `created_at`  char(20)  DEFAULT NULL,
    `length`      int       DEFAULT NULL,
    `deleted`     int       DEFAULT '0',
    `create_time` datetime  DEFAULT NULL,
    `update_time` datetime  DEFAULT NULL,
    `version`     int       DEFAULT NULL,
    PRIMARY KEY (`id`)
);