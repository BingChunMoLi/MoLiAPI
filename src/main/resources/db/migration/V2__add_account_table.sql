CREATE TABLE IF NOT EXISTS account
(
    id      int auto_increment primary key,
    type    varchar(20) NULL,
    method  varchar(10) NULL,
    url     varchar(1000) NULL,
    cookies varchar(800) NULL
);
