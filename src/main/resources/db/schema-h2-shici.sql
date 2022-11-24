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