# [MoLiAPI](https://api.bingchunmoli.com)

![](https://badgen.net/github/release/bingchunmoli/MoLiApi/stable)
![](https://badgen.net/github/prs/bingchunmoli/MoLiApi)
![](https://badgen.net/github/commits/bingchunmoli/MoLiApi)
![](https://badgen.net/github/releases/bingchunmoli/MoLiApi)
![](https://badgen.net/github/license/bingchunmoli/MoLiApi)
[![Java CI with Maven](https://github.com/BingChunMoLi/MoLiAPI/actions/workflows/maven.yml/badge.svg?event=branch_protection_rule)](https://github.com/BingChunMoLi/MoLiAPI/actions/workflows/maven.yml)

## 快速开始

Docker Compose 会启动 MySQL、Redis、MoLiAPI 和 Nginx。首次启动前复制环境变量模板并替换其中的示例密码：

```shell
cp .env.example .env
docker compose up -d --build
```

外部配置放在 `runtime/config/` 并以只读方式挂载。业务数据、MySQL、Redis、Prometheus 和 Grafana 使用
Docker 命名卷持久化，避免 Linux 宿主机目录的 UID 权限问题。容器日志写入标准输出：

```shell
docker compose logs -f moliapi
```

### 部署模式

| 模式 | Spring profiles | 日志 | 适用场景 |
| --- | --- | --- | --- |
| JAR 默认 | `h2` 或业务 profile | 文本标准输出 | systemd/supervisor 接管日志 |
| JAR 文件日志 | 增加 `file-log` | 文本控制台 + `${user.home}/logs` | 需要本地滚动文件 |
| Docker/K8s | 增加 `container` | ECS JSON 标准输出 | 容器日志采集 |

启用 `file-log` 后可通过 `MOLI_LOG_PATH` 覆盖目录。目录创建失败时应修正运行用户权限，不要使用 root 启动应用。
结构化日志使用 Spring Boot 4 原生编码器，默认格式为 ECS。可通过 `MOLI_CONSOLE_LOG_FORMAT` 和
`MOLI_FILE_LOG_FORMAT` 切换为 `ecs`、`gelf` 或 `logstash`，通过 `MOLI_ENVIRONMENT` 设置 ECS
`service.environment`。JSON 日志会保留 Micrometer 的 `traceId`、`spanId`，并额外输出 `requestId`。

### 功能开关

所有可能产生启动副作用或主动访问外部服务的功能默认关闭：

| 配置 | 作用 |
| --- | --- |
| `moli.features.startup-initialization` | 运行本地目录与数据库配置初始化器 |
| `moli.features.yi-yan-preload` | 将 classpath 中的一言 JSON 预加载到内存 |
| `moli.features.firebase` | 使用 `google-service.json` 初始化 Firebase |
| `moli.features.scheduling` | 启用 Spring 定时任务调度器 |
| `moli.features.tasks.bing` | 定时同步 Bing 图片 |
| `moli.features.tasks.weather-notification` | 定时发送天气通知 |
| `moli.features.tasks.daily` | 定时执行签到任务 |
| `moli.features.tasks.image` | 定时刷新随机图片 |
| `moli.features.tasks.host` | 定时同步 Hosts |
| `moli.features.tasks.bilibili` | 启动并定时刷新 B 站收藏 |
| `moli.features.tasks.netease-music` | 定时同步网易云歌单 |
| `moli.features.tasks.tencent-cdn-certificate` | 定时更新腾讯云 CDN 证书 |

任务开关只有在 `moli.features.scheduling=true` 时才会执行。推荐先保持全部关闭，确认相应密钥、文件目录和
外部服务可用后逐项开启。

Prometheus 和 Grafana 默认不启动，需要时启用观测 profile：

```shell
docker compose --profile observability up -d
```

如果服务器已运行旧版 Compose，切换前先使用 `mysqldump` 导出数据库。新版使用 Docker 命名卷，不会自动
导入旧的 `docker-compose/mysql/data/`；不同 MySQL 版本之间也不要直接复制数据目录。

本地直接运行：

```shell
java -Dspring.profiles.active=h2 -jar moliapi.jar
```

## Database profiles

H2 is the default in-memory database for local development and tests:

```shell
java -jar moliapi.jar --spring.profiles.active=h2
```

SQLite stores data in `moliapi.db` by default. Set `MOLI_SQLITE_PATH` to use another database file:

```shell
java -jar moliapi.jar --spring.profiles.active=sqlite
```

PostgreSQL uses `MOLI_POSTGRESQL_URL`, `MOLI_POSTGRESQL_USERNAME`, and `MOLI_POSTGRESQL_PASSWORD`:

```shell
java -jar moliapi.jar --spring.profiles.active=postgresql
```

MySQL deployments continue to use the datasource settings from `application-template.yml`.

数据库结构只由 Flyway 管理，旧的运行时 SQL 初始化器已移除。现有 V0 迁移中的少量兼容种子数据保持不变，
避免破坏已部署数据库的 Flyway 校验和；大体量一言 JSON 默认不会加载。

## Kubernetes

项目提供 Kustomize 和 Helm 两种部署方式，并支持最小化与全量指标监控模式。详见
[Kubernetes 部署文档](k8s/README.md)。

## 已支持接口:
1. Bing美图
2. emoji表情处理
3. 公网IP查询
4. UserAgent解析
5. 二维码处理
6. 随机诗词或指定
7. 随机一言或制定
8. QQ头像获取(支持多种方式)
9. QQ空间头像获取(支持多种方式)
10. 天气查询
11. Hosts订阅
12. 随机图
13. 迅雷链接转换
14. 每日签到网址
15. 自动更新腾讯CDN证书(定时任务, 暂仅支持单域名,如需多域名请提Iss)
16. 网易云音乐歌单收藏
17. b站收藏获取,通知失效视频

## 后台管理功能
1. 签到
2. 签到日志查询
3. 随机music
4. 歌单歌曲

## 启用所有功能

1. 复制 `src/main/resources/application-template.yml` 到 `runtime/config/application-template.yml`。
2. 在外部配置中填写所需的第三方服务参数，不要提交密码、证书或生产密钥。
3. 使用 Docker Compose 启动；`template` profile 已默认启用，外部配置会覆盖镜像内模板。
