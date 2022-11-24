CREATE TABLE `bing_image`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `start_date`         varchar(30)  DEFAULT NULL,
    `start_date_en`      varchar(30)  DEFAULT NULL,
    `full_start_date`    varchar(30)  DEFAULT NULL,
    `full_start_date_en` varchar(30)  DEFAULT NULL,
    `end_date`           varchar(30)  DEFAULT NULL,
    `url`                varchar(150) DEFAULT NULL,
    `url_en`             varchar(150) DEFAULT NULL,
    `url_base`           varchar(70)  DEFAULT NULL,
    `url_base_en`        varchar(70)  DEFAULT NULL,
    `copyright`          varchar(200) DEFAULT NULL,
    `copyright_en`       varchar(200) DEFAULT NULL,
    `copyright_link`     varchar(200) DEFAULT NULL,
    `copyright_link_en`  varchar(200) DEFAULT NULL,
    `headline_en`        varchar(100) DEFAULT NULL,
    `create_time`        datetime     DEFAULT NULL,
    `obs_url_cn`         varchar(120) DEFAULT NULL,
    `obs_url_en`         varchar(120) DEFAULT NULL,
    `url_uhd`            varchar(255) DEFAULT NULL,
    `url_uhd_en`         varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `host`
(
    `id`     int NOT NULL AUTO_INCREMENT,
    `ip`     varchar(30) DEFAULT NULL,
    `domain` varchar(60) DEFAULT NULL,
    `source` varchar(30) DEFAULT NULL,
    PRIMARY KEY (`id`)
);