# Go 分支项目上下文

- Go 1.25、Gin；入口 `main.go`。
- 原 Go 公开接口、证书任务与监控面板保留。Java 版可用功能迁移至 `internal/application`。
- `internal/store` 提供本地原子 JSON 存储与 MySQL 事务存储，Go 数据结构独立于 Java 数据库。
- `internal/delivery` 提供 SMTP、FCM、HMS；ServerChan 和业务任务在 application 中。
- Vue 管理界面位于 `web/admin`，构建后通过 `/admin/` 访问，同源 API 前缀 `/api/`。
- 新任务默认关闭，外部凭证通过进程环境或私有挂载文件提供；不得提交 secrets、data、真实配置。
- 功能、接口兼容范围、迁移限制和部署参数见 `docs/GO_MIGRATION.md`。
- 验证：`go test ./...`、`go vet ./...`、`go build .`；前端 `yarn build`、`yarn type-check`。
