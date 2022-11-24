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