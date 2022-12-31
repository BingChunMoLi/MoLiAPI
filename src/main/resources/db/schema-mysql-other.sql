create table `bing_image`
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

create table `host`
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


create table `navigation`
(
    `id`     int not null auto_increment primary key,
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


