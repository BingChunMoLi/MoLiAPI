CREATE TABLE IF NOT EXISTS bing_image
(
    id                 BIGSERIAL PRIMARY KEY,
    start_date         VARCHAR(30),
    start_date_en      VARCHAR(30),
    full_start_date    VARCHAR(30),
    full_start_date_en VARCHAR(30),
    end_date           VARCHAR(30),
    end_date_en        VARCHAR(30),
    url                VARCHAR(150),
    url_en             VARCHAR(150),
    url_base           VARCHAR(70),
    url_base_en        VARCHAR(70),
    copyright          VARCHAR(200),
    copyright_en       VARCHAR(200),
    copyright_link     VARCHAR(300),
    copyright_link_en  VARCHAR(300),
    headline_en        VARCHAR(100),
    create_time        TIMESTAMP,
    obs_url_cn         VARCHAR(120),
    obs_url_en         VARCHAR(120),
    url_uhd            VARCHAR(255),
    url_uhd_en         VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS host
(
    id     SERIAL PRIMARY KEY,
    ip     VARCHAR(30),
    domain VARCHAR(60),
    source VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS navigation
(
    id     SERIAL PRIMARY KEY,
    title  VARCHAR(30),
    des    VARCHAR(300),
    url    VARCHAR(1000),
    icon   VARCHAR(1000),
    tenant VARCHAR(30) NOT NULL DEFAULT 'public'
);

CREATE TABLE IF NOT EXISTS tag
(
    id         SERIAL PRIMARY KEY,
    tag_name   VARCHAR(20) NOT NULL,
    is_open    BOOLEAN NOT NULL DEFAULT FALSE,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    pwd        VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS navigation_tag
(
    id            SERIAL PRIMARY KEY,
    tag_id        INTEGER NOT NULL,
    navigation_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS shi_ci
(
    id          SERIAL PRIMARY KEY,
    content     VARCHAR(100),
    origin      VARCHAR(100),
    author      VARCHAR(10),
    category    VARCHAR(100),
    deleted     INTEGER DEFAULT 0,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    version     INTEGER
);

CREATE TABLE IF NOT EXISTS yi_yan
(
    id          SERIAL PRIMARY KEY,
    uuid        VARCHAR(80),
    hitokoto    VARCHAR(200),
    type        VARCHAR(5),
    "from"      VARCHAR(50),
    from_who    VARCHAR(20),
    creator     VARCHAR(50),
    creator_uid INTEGER,
    reviewer    INTEGER,
    commit_from VARCHAR(20),
    created_at  VARCHAR(20),
    length      INTEGER,
    deleted     INTEGER DEFAULT 0,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    version     INTEGER
);

CREATE TABLE IF NOT EXISTS weather_sub
(
    id       SERIAL PRIMARY KEY,
    location VARCHAR(10) NOT NULL,
    email    VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS daily_log
(
    id          BIGSERIAL PRIMARY KEY,
    url         VARCHAR(700) NOT NULL,
    tenant      SMALLINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    type        SMALLINT NOT NULL
);

CREATE TABLE IF NOT EXISTS device
(
    id         SERIAL PRIMARY KEY,
    token      VARCHAR(300),
    hms_token  VARCHAR(300),
    push_type  VARCHAR(10) DEFAULT 'FCM',
    name       VARCHAR(10),
    model      VARCHAR(30),
    android_id VARCHAR(60)
);

CREATE TABLE IF NOT EXISTS push_log
(
    id          SERIAL PRIMARY KEY,
    type        INTEGER NOT NULL,
    title       VARCHAR(100) NOT NULL,
    body        VARCHAR(3000),
    receive     VARCHAR(300) NOT NULL,
    status      SMALLINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS netease_music_song
(
    id          SERIAL PRIMARY KEY,
    third_id    BIGINT,
    name        VARCHAR(50) NOT NULL,
    album_id    INTEGER,
    playlist_id INTEGER
);

CREATE TABLE IF NOT EXISTS netease_music_playlist
(
    id          SERIAL PRIMARY KEY,
    third_id    BIGINT,
    name        VARCHAR(100) NOT NULL,
    user_id     VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS netease_music_user
(
    id             SERIAL PRIMARY KEY,
    third_id       BIGINT,
    avatar_url     VARCHAR(500),
    city           INTEGER,
    birthday       BIGINT,
    nickname       VARCHAR(100),
    background_img VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS netease_music_song_user
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER,
    song_id INTEGER
);

CREATE TABLE IF NOT EXISTS netease_music_album
(
    id           SERIAL PRIMARY KEY,
    third_id     BIGINT,
    name         VARCHAR(200),
    pic_url      VARCHAR(500),
    type         VARCHAR(20),
    publish_time TIMESTAMP,
    user_id      INTEGER
);

CREATE TABLE IF NOT EXISTS api_user
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(30),
    password    VARCHAR(60),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bili_fav
(
    id          INTEGER PRIMARY KEY,
    fid         INTEGER NOT NULL,
    mid         INTEGER NOT NULL,
    title       VARCHAR(20),
    type        INTEGER,
    media_count INTEGER,
    cover       VARCHAR(500),
    intro       VARCHAR(3000),
    ctime       INTEGER,
    mtime       INTEGER,
    only_audio  INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT bili_fav_fid_uindex UNIQUE (fid)
);

CREATE TABLE IF NOT EXISTS bili_media
(
    id              BIGINT PRIMARY KEY,
    type            INTEGER,
    title           VARCHAR(150),
    cover           VARCHAR(500),
    intro           VARCHAR(3000),
    page            INTEGER,
    mid             INTEGER,
    ctime           INTEGER,
    pubtime         INTEGER,
    fav_time        INTEGER,
    bv_id           VARCHAR(12),
    down            INTEGER NOT NULL DEFAULT 0,
    upload          INTEGER NOT NULL DEFAULT 0,
    invalid_push    INTEGER NOT NULL DEFAULT 0,
    onedrive_upload INTEGER NOT NULL DEFAULT 0,
    only_audio      INTEGER NOT NULL DEFAULT 0,
    fav_id          INTEGER,
    CONSTRAINT bili_media_bv_id_uindex UNIQUE (bv_id)
);

CREATE TABLE IF NOT EXISTS bili_user
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(30),
    face VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS config
(
    id          SERIAL PRIMARY KEY,
    "key"       VARCHAR(100) NOT NULL,
    "value"     VARCHAR(500),
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL,
    CONSTRAINT config_uindex UNIQUE ("key")
);

CREATE TABLE IF NOT EXISTS account
(
    id      SERIAL PRIMARY KEY,
    type    VARCHAR(20),
    method  VARCHAR(10),
    url     VARCHAR(1000),
    cookies VARCHAR(800)
);
