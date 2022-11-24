create database api;
use api;
-- api.bing_image definition

CREATE TABLE `bing_image` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `start_date` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `start_date_en` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `full_start_date` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `full_start_date_en` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `end_date` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url_en` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url_base` varchar(70) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url_base_en` varchar(70) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `copyright` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `copyright_en` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `copyright_link` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `copyright_link_en` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `headline_en` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `create_time` datetime DEFAULT NULL,
                              `obs_url_cn` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `obs_url_en` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url_uhd` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              `url_uhd_en` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2933 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


-- api.host definition

CREATE TABLE `host` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `ip` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `domain` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `source` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        PRIMARY KEY (`id`) USING BTREE,
                        UNIQUE KEY `host_id_uindex` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4941 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

