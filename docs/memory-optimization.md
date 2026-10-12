# 内存优化记录

本次修改基于 develop 的 `ca4ba2c`，主要减少随机图片请求的临时对象和随机诗词的常驻对象。

## 修改

- `/img/mobile` 直接通过 `FileSystemResource` 传输原文件，避免解码像素后重新编码。
- `/img/mobile`、`/img/pc`、`/img` 按文件扩展名设置 Content-Type，返回原始图片格式。
  移动端原先统一重新编码为 JPEG，现在 PNG、WebP 等文件会保持原格式，调用方需依据 Content-Type 处理。
- 图片缓存不可用、为空或引用已移除的文件时，回退到配置的本地目录。
- 本地随机选择采用蓄水池抽样，只保留一个候选路径；扫描时间仍为 O(n)，保留空间为 O(1)。
- 随机诗词移除整表对象缓存，通过记录数和随机偏移只查询一条未逻辑删除的记录。
- 将 Web MVC 和 JDBC 测试 starter 的依赖作用域调整为 `test`。

## 图片分配量基准

环境为 Windows、OpenJDK 17.0.2，两个独立进程均使用 `-Xms64m -Xmx256m`。
输入为同一张 3840 × 2160 JPEG，文件大小 4,985,286 字节；预热 3 次后采样 10 次。

基准调用实际 `ImgController.randomImgByMobile()`，再调用对应的 Spring HTTP 消息转换器。
Redis 列表操作由返回同一路径的本地替身提供，输出写入空输出流。
优化前的图片 Controller、Service 和接口由上述 Git 提交编译，优化后使用当前代码。
使用 `ThreadMXBean.getThreadAllocatedBytes` 统计调用线程的 Java 堆分配量。

| 指标 | 优化前 | 优化后 |
| --- | ---: | ---: |
| 平均每次请求堆分配 | 80,298,802 字节（约 76.6 MiB） | 12,248 字节（约 12 KiB） |
| 平均处理耗时 | 639.400 ms | 6.013 ms |

这是图片处理路径的本地基准，不包含网络、Servlet 容器、真实 Redis、原生内存或进程 RSS。
耗时受本机文件缓存和运行环境影响，不能用来推算生产吞吐量或整体常驻内存降幅。
本地基准程序和原始输出保存在忽略的 `target/ImageMemoryProbe.java`、
`target/memory-baseline-result.log` 和 `target/memory-current-result.log`。

## 验证与取舍

- 新增 24 项图片和诗词测试，包括原始文件字节、媒体类型、缓存回退、空目录、
  大列表索引、诗词响应结构、ID 间隙和逻辑删除过滤。
- 完整 Maven 测试结果为 78 项通过、2 项原有测试跳过，无失败。
- Java 17 下 `mvn -DskipTests package` 成功，已检查运行 JAR 不含测试 starter、JUnit、Mockito、AssertJ。
- SQL 已通过 H2 的 MySQL 兼容模式验证，尚未在实际 MySQL 上测量执行计划与延迟。
- 诗词每个非空请求新增一次计数和一次单行查询，以数据库查询开销换取不保留全表对象。
  偏移查询保持记录均匀抽样，较大偏移仍需扫描经过的记录；大数据量、高请求量部署应测量查询延迟。
- 本次无需数据库迁移。回滚代码即可恢复原来的图片编码和诗词缓存行为。
