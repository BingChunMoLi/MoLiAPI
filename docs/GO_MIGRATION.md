# Go 分支功能对齐

以 develop 的可用功能为基准。新增接口统一返回
`{"code":"00000","msg":"一切OK","data":...}`，同时支持 `/api/` 前缀。
原有 Go 接口保留响应格式，`/api/ip`、`/api/yiyan/random` 提供包装后的对应接口。

## 功能清单

| 功能 | 路由 / 实现 |
| --- | --- |
| 初始化、登录、退出 | `/user/init`、`/user/register`、`/user/login`、`/user/logout`；bcrypt、HttpOnly 会话 |
| 后台配置 | `GET/POST /system`；局部更新、持久化、重启恢复 |
| 导航和标签 | `GET/PUT /navigation`、`POST/DELETE /navigation/{id}`、`GET /navigation/export`；tenant 过滤、原子关联更新 |
| 签到 | `/daily`、`/daily/all`、`/daily/signed`、`/daily/check`、`/daily/param`、`/daily/query`；包含结束日期全天 |
| App 设备 | `POST /app`、`GET /app/{name}/{model}/{androidId}`；设备合并、保留推送 token |
| Bing | `/bing/cn`、`/bing/en`、`/bing/all`、`/bing/random`、`/bing/date`、`/bing/{year}/{month}/{day}`；日期档案持久化 |
| Emoji | `/emoji/alise`、`unicode`、`html`、`isEmoji`、`contains`、`tag`、`getByAlise`、`removeAllEmojis`、`extractEmojis` |
| 二维码、迅雷、UA | `/generate`、`GET/POST /decode`（multipart 字段 file）、`/thunder/toRaw`、`/thunder/toThunder`、`/ua/userAgentInfo`、`/ua/userAgentInfoByParam`、`/ua/userAgent` |
| IP、头像、租金 | `/address`、`/tencent/qq`、`/tencent/qz`、`/tencent/qz/json`、`/rent` |
| 图片、一言、诗词 | `/img`、`/img/pc`、`/img/mobile`、`/img/1080`、`/yiyan/{id}`、`/shici/{id}`、`/shici/random` |
| hosts | `/host/json`、`/host/raw`、`/host/file`；类型过滤、去重 |
| 音乐 | `/music/playlist`、`/music/{id}`、`/music/random`；保存歌单和歌曲/专辑/歌手信息 |
| 天气和订阅 | `/weather/byDay`、`/weather/now`、`/weather/sub`、`/weather/callback`、`/weather/unSub`；签名确认与退订 |
| 通知与任务 | SMTP、FCM、HMS、ServerChan；`/push/logs`、`GET /tasks`、`POST /tasks/{name}` |
| 管理与监控 | `/admin/`、`/dashboard`、`/api/stats`、`/actuator/health`、`/actuator/prometheus` |
| 证书 | 保留 Go 分支已有云证书任务与 `/cert/update` |

未配置显式证书任务时，后台 certificatePath、privateKeyPath、domain 自动构成腾讯 CDN 更新任务，
修改后下次执行读取新值；显式 config.yaml / CERT_TASKS_JSON 任务优先。

后台配置、导航写入/导出、手动任务和通知日志需要登录。
导航省略/null 的 tagList 保留关联，空数组清除关联；已有标签 ID 保持不变。
签到 urls 过滤使用重复查询参数，避免破坏网址中的逗号。
Java 版 navigation/import 固定返回 false，通用 /unsubscribe 是 TODO，未作为可用功能迁移。

## 启动

```shell
go test ./...
go build -o moliapi .
cd web/admin
yarn install --frozen-lockfile
yarn build
yarn type-check
```

从项目根目录运行二进制后访问 `http://localhost:8080/admin/`，首次创建管理员。
前端默认同源 `/api/`，开发代理指向本地 8080；VITE_API_BASE_URL 可覆盖。
Dockerfile 同时构建前后端并包含监控面板文件。
程序读取进程环境，不自动加载 `.env`；模板见根目录 `.env.example`。

PUBLIC_URL 用于邮件链接及 HTTPS Cookie；CORS_ORIGINS 是逗号分隔的完整来源，允许 tenant 预检头。
TRUSTED_PROXIES 配置可信代理 IP/CIDR，默认不信任转发头。

## 存储与迁移范围

- 默认 DATA_PATH=data/state.json，写入使用临时文件、同步和原子替换；失败不会改变内存状态。
  同一文件只供一个进程使用，需挂载持久卷并限制访问。
- MYSQL_DSN 启用 MySQL 独立表 moli_go_state，整次写操作使用 InnoDB 行锁事务。
  面向个人部署规模，大量签到历史会增加快照读写开销。
- Go 数据格式独立于 Java 表结构，不自动读取或修改 Java 业务表，切换版本前需另行导入已有业务数据。
- 登录会话在进程内存中，重启后重新登录；多实例需要粘性会话，定时任务仅在一个实例启用。
- 配置读取顺序：默认值、环境变量、持久化后台配置。后台修改即时生效。
- Bing 首次获取上游最近 8 天，此后持续积累，不能自动恢复 Java 数据库中的更早档案。
- /music/{id} 使用网易云歌单原始 ID。UA/IP 的部分附加字段来自 Go 解析器/定位服务，
  不作为 Java 第三方库 DTO 的逐字段替代。
- IP 默认使用 [ipwho.is](https://ipwhois.io/documentation)，仅查询公网 IP；
  IP_LOOKUP_URL 支持自建服务及 {ip} 占位符，无占位符时附加 ip 查询参数。
  内网请求不访问外部定位，天气查询需显式提供 location。

## 任务和通知配置

新增任务默认关闭。TASKS 为逗号分隔列表，DISABLE_CRON=true 或已有 serverless 标识关闭调度。
时区 TZ 默认 Asia/Shanghai，失败可在下次运行重试；手动任务最多执行 30 分钟。

| 任务名 | 默认时间 | 配置 |
| --- | --- | --- |
| bing | 每天 00:00 | 无 |
| host | 每天 00:00 | 无 |
| netease-music | 每天 00:00 | PLAYLIST_ID、可选 NETEASE_COOKIE |
| daily | bili 08:00、52pojie 09:00 | DAILY_ACCOUNTS_FILE 或 DAILY_ACCOUNTS_JSON |
| weather-notification | 每天 18:30 | 天气、SMTP、已确认订阅 |
| bilibili | 每月每隔 3 天的 00:00 | BILI_FAV_ID、可选 BILI_COOKIE、命令与通知渠道 |
| image | 每月 1 日 00:00 | 图库目录和通知渠道 |

账号文件示例（占位 Cookie，不提交真实配置）：

```json
[
  {"type":"bili","url":"https://example.com/sign","method":"GET","cookies":""},
  {"type":"52pojie","url":"https://example.com/sign","method":"GET","cookies":""}
]
```

WEATHER_KEY、WEATHER_URL、WEATHER_GEO_URL 使用账号对应的和风 API Host。
订阅确认邮件限频 15 分钟、确认链接 24 小时有效、退订链接一年有效；修改 weatherKey 使旧链接失效。
未来三天日间或夜间有雨雪图标时发邮件。

SMTP 默认 STARTTLS，465 使用隐式 TLS；FCM_CREDENTIALS_FILE 指向服务账号文件，自动签发并缓存 OAuth 令牌。
HMS 使用 HMS_CLIENT_ID、HMS_CLIENT_SECRET、HMS_APP_ID。ServerChan 使用后台 serverSauceKey，
收藏失效邮件使用 NOTIFY_EMAIL。最近 1000 条发送结果持久化。

DOWNLOAD_COMMAND、UPLOAD_COMMAND 是 JSON 参数数组，支持 {url}、{dir}，直接执行，不经过 shell。
下载和上传分别记录成功，失败重试，成功阶段不重复执行。收藏夹完整分页读取，失效通知成功后才标记。
仅跟踪收藏夹可不配置下载命令。BBDown、you-get、rclone 等需在宿主机或自定义镜像内安装，
默认 distroless 镜像不包含外部工具。

## 验证与数据来源

测试使用内存/临时文件和 httptest 模拟上游，不发送真实通知或下载真实视频。
MYSQL_TEST_DSN 指向独立空测试库时执行 MySQL 集成测试，否则跳过。
Linux 静态构建使用 CGO_ENABLED=0、GOOS=linux；竞态检测需要 CGO 和 C 编译器。
诗词包含 Java 版 100 条种子数据；Emoji 数据为 emoji-java v5.1.1，MIT 许可证随数据保存于
internal/emoji/data，来源：[emoji-java](https://github.com/vdurmont/emoji-java/tree/v5.1.1)。

本次本地验证：42 个顶层测试通过，MySQL 实库集成测试因未配置 MYSQL_TEST_DSN 跳过；
go vet、Linux amd64 静态构建、Vue 生产构建和独立类型检查通过。
第三方服务使用模拟响应，尚未用真实凭证联调；Docker 镜像未实构建（本机无 Docker CLI）。
竞态检测未运行（本机 CGO_ENABLED=0 且未安装 C 编译器），并发存储已通过并发更新测试。
