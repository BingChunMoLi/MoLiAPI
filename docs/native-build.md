# GraalVM Native 构建

主要部署目标为 Linux。普通 JAR 仍使用 Java 17，native 构建使用 GraalVM 25 或更新版本。
要求来自 [Spring Boot 系统要求](https://docs.spring.io/spring-boot/system-requirements.html)。

## Linux 本机编译

安装 GraalVM 25+ 并将 `JAVA_HOME`、`PATH` 指向它。确认 `java -version` 与 `native-image --version`。
Ubuntu/Debian 需要 C/C++ 工具链以及图片处理所需的系统库：

```shell
sudo apt-get install build-essential zlib1g-dev libfreetype6-dev libfontconfig1-dev \
    libx11-dev libxext-dev libxrender-dev libxi-dev libxtst-dev
mvn -Pnative package
./target/moliapi
```

`native` 是 Maven profile，会运行测试、Spring AOT 和 Native Image 编译。
默认以 Spring `h2` profile 生成应用。构建产物为 `target/moliapi`。
空数据库在启动时由 Flyway 自动迁移；生产环境应先备份并确认待执行的迁移。
本地快速验证可加 `-DquickBuild`；跳过测试可加 `-DskipTests`。
建议构建机至少提供 8 GiB 内存，实际需求取决于 GraalVM、并行度和启用的第三方集成。

若 `target/` 同时生成 `.so` 动态库，部署时必须和可执行文件一同复制，不能只复制 `moliapi`。
运行系统需提供 glibc、zlib、freetype、fontconfig 及所需 X11 依赖；不要将此产物用于 Alpine/musl。
中文错误图片需要系统安装中文字体，或在镜像中额外提供字体文件。

## Docker 构建

```shell
docker build -f Dockerfile.native -t moliapi:native .
```

`Dockerfile.native` 使用 GraalVM 25 的 Oracle Linux 9 构建环境和无 JVM 的 Oracle Linux 9 运行环境，
复制 Native Image 生成的伴随动态库，以 UID/GID 10001 运行。
独立的 `Dockerfile.native.dockerignore` 只包含后端构建输入并排除本地配置、凭据和私有源码覆盖。
不要向构建上下文添加生产配置或密钥。

默认构建及运行 profile 为 `h2,container`。实际应用请求仍依赖 Redis；
请通过 `SPRING_DATA_REDIS_HOST` 等环境变量连接已有 Redis，并将宿主机端口映射到容器 8090。
外部配置挂载到 `/soft/.api/config/`，文件数据放在 `/soft/.api/`，目录需允许 UID 10001 写入。
容器使用 glibc，已包含图片处理的系统库；按业务需要补充中文字体。

## 数据库与功能开关

Spring AOT 在构建时决定 Bean 的存在与否，数据库 profile、`@ConditionalOnProperty`、`@Profile`
等条件必须在构建时确定，不能只在 native 启动时改变。
详见 [Spring Boot AOT 配置](https://docs.spring.io/spring-boot/maven-plugin/aot.html)。

例如构建 PostgreSQL 应用：

```shell
mvn -Pnative -Dnative.spring.profiles=postgresql package
./target/moliapi --spring.profiles.active=postgresql
```

部署时通过 `MOLI_POSTGRESQL_URL`、`MOLI_POSTGRESQL_USERNAME`、`MOLI_POSTGRESQL_PASSWORD` 设置连接。
容器构建时可用 `--build-arg NATIVE_SPRING_PROFILES=postgresql,container`，
运行时同步设置 `SPRING_PROFILES_ACTIVE=postgresql,container`。

如需在构建时开启可选任务，可向 AOT 传递参数，例如：

```shell
mvn -Pnative -Dnative.spring.profiles=h2 \
    '-Dspring-boot.aot.jvmArguments=-Dmoli.features.scheduling=true -Dmoli.features.tasks.bing=true' package
```

运行时也需要设置同样的功能开关。连接地址、密码、图片路径等值应通过运行时环境变量或外部配置提供。
不要把真实密码、证书或 Firebase 凭据作为 AOT 参数传入。
默认构建保持外部任务和 Firebase 初始化关闭；启用 Firebase、腾讯云、SQLite 等集成后需要分别验证
相应 SDK、JNI 和反射路径。当前新增元数据不能替代对所有第三方集成的 native 验证。

## Windows 测试

Windows 可以用 Java 17+ 验证 Spring AOT，无需安装 GraalVM 或 MSVC：

```powershell
mvn -Pnative -DskipNativeBuild package
java -Dspring.aot.enabled=true -jar target/moliapi.jar
```

普通 Maven 构建与 AOT 构建之间建议使用干净的 `target/`，防止残留的 AOT 类混入构建产物。
以上命令的产物是可用 AOT 模式启动的 JAR，不是原生可执行文件。

本项目的二维码、QQ 头像和图片错误页使用 AWT/ImageIO，GraalVM 的 Windows AWT 支持仍有限制，
参见 [GraalVM Windows AWT 跟踪问题](https://github.com/oracle/graal/issues/3084)。
因此完整功能的 Linux native 产物应在 Linux、WSL2 或 Docker Desktop 的 Linux 容器中编译测试。
Windows 本机的 `.exe` 不能作为 Linux 部署产物，本次不承诺完整应用的 Windows native 编译成功。

## 自动验证与冒烟测试

`.github/workflows/native.yml` 在相关 PR 和手动触发时使用 GraalVM 25 编译 Linux 产物，
启动临时 Redis，测试数据库查询、emoji、IPv4/IPv6、二维码生成、错误图片和后台认证。
工作流只上传构建产物与日志，不发布镜像或部署服务。

本地启动应用和 Redis 后，可运行同一套只读检查：

```shell
python3 scripts/native-smoke-test.py --base-url http://127.0.0.1:8090
```

默认 H2 种子数据用于检查随机诗词和一言，测试会读取 IP 数据库并生成临时响应图片。
检查程序也适用于 Windows 上的 AOT JVM 应用。Java 单元测试验证自定义资源/反射提示和校验元数据，
最终 native 编译与运行结果以 Linux 工作流日志为准。
