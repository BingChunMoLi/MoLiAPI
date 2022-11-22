-- api.shi_ci definition

CREATE TABLE `shi_ci`
(
    `id`          int NOT NULL AUTO_INCREMENT,
    `content`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `origin`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `author`      varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL,
    `category`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `deleted`     int                                                     DEFAULT '0',
    `create_time` datetime                                                DEFAULT NULL,
    `update_time` datetime                                                DEFAULT NULL,
    `version`     int                                                     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4001
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC;