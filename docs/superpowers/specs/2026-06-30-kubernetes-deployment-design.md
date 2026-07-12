# MoLiAPI Kubernetes 部署设计

## 目标

在仓库根目录新增 `k8s/`，提供彼此独立可用的 Kustomize 和 Helm 部署方式，使用户可以在 Kubernetes
中以最小化或全量模式运行 MoLiAPI。配置面向熟悉 Docker 与 Docker Compose、但 Kubernetes 经验有限的
使用者，关键参数和操作均提供中文说明。

## 范围

最小化模式包含：

- MoLiAPI；
- MySQL；
- Redis；
- 标准 Kubernetes Ingress。

全量模式在最小化模式基础上增加：

- Loki；
- Grafana；
- Prometheus；
- 仅采集 `moliapi` Namespace 中 Pod 日志的 Promtail。

Docker Compose 中独立部署的 Nginx 和 Nginx Exporter 不迁移为 Pod。入口代理职责由集群已有的 Ingress
Controller 承担，入口监控复用 Ingress Controller 自身暴露的指标。该选择保留对外访问和监控能力，同时
避免在 Kubernetes 内重复部署入口代理。

本次不修改业务代码、现有 Docker Compose 文件或现有生产配置。

## 目录结构

```text
k8s/
├── README.md
├── config/
│   ├── prometheus.yml
│   ├── promtail.yml
│   ├── loki.yml
│   └── grafana-datasources.yml
├── kustomize/
│   ├── base/
│   ├── components/
│   │   ├── loki/
│   │   ├── grafana/
│   │   ├── prometheus/
│   │   └── promtail/
│   └── overlays/
│       ├── minimal/
│       └── full/
├── helm/
│   └── moliapi/
│       ├── Chart.yaml
│       ├── values.yaml
│       ├── values-minimal.yaml
│       ├── values-full.yaml
│       └── templates/
└── scripts/
    └── validate.ps1
```

Kustomize 与 Helm 各自采用原生资源定义，不要求其中一种工具调用另一种工具。两套配置共享资源名称、标签、
镜像版本、端口和默认资源规格约定，并通过验证脚本检查关键行为一致性。

## Kustomize 组合方式

`base` 包含 MoLiAPI、MySQL、Redis、Service、Ingress、PVC 和非敏感配置。`overlays/minimal` 直接组合
基础资源。Loki、Grafana、Prometheus 和 Promtail 分别作为独立 Component；`overlays/full` 引用全部
可观测性 Component。

用户可以复制一个 overlay，并通过增删 Component 实现按需部署。每个 overlay 提供清晰的镜像、域名、
StorageClass、容量和资源规格修改位置。Secret 模板不会被 overlay 自动应用，避免示例密码被误当作生产凭据。

## Helm 组合方式

Helm Chart 自包含所有资源模板，不依赖外部 Chart 仓库。以下开关分别控制组件：

- `mysql.enabled`；
- `redis.enabled`；
- `loki.enabled`；
- `grafana.enabled`；
- `prometheus.enabled`；
- `promtail.enabled`。

`values-minimal.yaml` 启用 MoLiAPI、MySQL 和 Redis，关闭可观测性组件。`values-full.yaml` 启用全部组件。
`values.yaml` 保存完整默认值和参数说明。用户可以从任一预设文件复制自己的 values 文件，再覆盖镜像、Ingress、
存储和资源规格。

## 配置与 Secret

非敏感配置使用 ConfigMap 或环境变量注入。数据库密码、Redis 密码、Grafana 管理员密码使用已有 Secret
引用。仓库只提交 Secret 模板、所需键名和创建命令，不提交真实密码、证书或 Token。

Kustomize 和 Helm 均引用约定名称的 Secret。Secret 不存在时，相应 Pod 保持未启动状态。README 先引导
用户创建 Secret，再应用资源，从而提供可预测的失败方式。

邮件、腾讯云、Firebase、证书更新和其他外部集成默认不配置。用户显式提供对应 Secret 和配置后才启用。
Ingress 默认关闭 TLS；用户提供域名和 TLS Secret 名称后启用。

MoLiAPI 使用 Kubernetes 专用环境变量覆盖以下运行配置：

- MySQL JDBC URL、用户名和密码；
- Redis 主机、端口和密码；
- `moli.path` 持久化路径；
- Actuator health、prometheus 端点；
- JSON 控制台日志；
- JVM 内存和其他 JVM 参数。

## 数据与启动流程

MySQL 使用 StatefulSet、Headless Service 和独立 PVC。启动时由官方镜像创建 `moliapi` 数据库及独立
应用用户，应用不使用 root 账号。数据库结构仅由 MoLiAPI 内置 Flyway 迁移管理，不把 Flyway SQL 再挂载到
MySQL 初始化目录，避免同一迁移执行两次。

Redis 使用 StatefulSet、Service 和独立 PVC。MoLiAPI 使用 Deployment、ClusterIP Service、Ingress
和应用数据 PVC。应用默认单副本，原因是当前存在本地可写数据与定时任务；在引入共享存储和任务互斥机制前，
不提供默认水平扩容。

MoLiAPI initContainer 等待 MySQL 和 Redis 可连接后再启动主容器。Spring Boot 启动后由 startupProbe、
readinessProbe 和 livenessProbe 检查 Actuator 健康端点。只有 readinessProbe 成功的 Pod 才接收 Service
与 Ingress 流量。

所有数据 PVC 默认使用 `ReadWriteOnce`。`storageClassName` 留空时使用集群默认 StorageClass，用户可按
集群环境覆盖。删除 Deployment、StatefulSet 或 Pod 不删除 PVC；README 明确说明 PVC 删除属于数据销毁操作。

## 可观测性

Prometheus 抓取 MoLiAPI 的 `/actuator/prometheus`。Loki、Prometheus 和 Grafana 分别使用持久化卷保存数据。
Grafana 通过预置 datasource 自动连接 Prometheus 和 Loki。

Promtail 以 DaemonSet 运行，仅发现并采集 `moliapi` Namespace 中的 Pod 标准输出。它使用最小化的
Role/RoleBinding 获取该 Namespace 内的 Pod 元数据，并挂载节点容器日志目录。MoLiAPI 启用 JSON 控制台日志，
不通过共享业务日志目录向 Promtail 传递日志。

## 安全与资源约束

所有镜像使用明确版本，不使用 `latest`。每个工作负载提供默认 CPU、内存 requests 和 limits。MoLiAPI
以非 root 用户运行，业务数据和日志目录挂载可写卷，其他文件系统尽可能只读。各组件使用与官方镜像兼容的
用户、组和文件系统权限。

Secret 不出现在 ConfigMap、values 预设或可提交的 Kustomize 文件中。Service 默认使用 ClusterIP，只有
Ingress 对外暴露 MoLiAPI。Grafana、Prometheus 和 Loki 默认不直接暴露到集群外；需要外部访问时由用户显式
增加 Ingress 配置。

## 面向初学者的说明与注释

README 使用 Docker Compose 概念帮助用户建立映射：

| Docker Compose | Kubernetes |
| --- | --- |
| `service` | Deployment 或 StatefulSet 加 Service |
| `ports` | Service 与 Ingress |
| `environment` | ConfigMap、Secret 和容器 env |
| `volumes` | PVC、ConfigMap volume 和 Secret volume |
| `depends_on` | initContainer、Probe 与服务发现 |
| Compose profile | Kustomize overlay 或 Helm values 预设 |

所有用户通常需要修改的 YAML 参数提供中文行内注释，包括镜像、域名、TLS、StorageClass、PVC 容量、资源规格、
副本数和组件开关。注释解释修改影响和常见取值，不重复解释 Kubernetes 自动生成或通常不应修改的字段。

README 分别给出 Kustomize 和 Helm 的最小化、全量化命令，并包含前置条件、Secret 创建、部署、状态检查、
日志查看、端口转发、升级、回滚、卸载和 PVC 保留说明。故障排查覆盖 Secret 缺失、PVC Pending、Ingress
无地址、探针失败、MySQL/Redis 未就绪和镜像拉取失败。

## 验证

本地验证至少包括：

1. `kubectl kustomize` 分别渲染 minimal 和 full overlay；
2. `helm lint` 校验 Chart；
3. `helm template` 分别渲染 minimal 和 full values；
4. 验证脚本检查两套渲染结果中的组件集合、固定镜像版本、端口、探针、资源规格和 Secret 引用；
5. 扫描仓库中是否误提交明文 Secret 或 `latest` 镜像；
6. 存在可用集群时执行 server-side dry-run。

若本地缺少 Helm、kubectl 或 Kubernetes 集群，交付说明会列出未执行的检查及其残余风险。

## 验收标准

- 所有 Kubernetes 相关文件均位于 `k8s/`，设计和实施计划除外；
- Kustomize 与 Helm 都能独立渲染最小化和全量配置；
- 最小化配置只包含 MoLiAPI、MySQL、Redis 及其入口和存储资源；
- 全量配置额外包含 Loki、Grafana、Prometheus 和 Namespace 范围的 Promtail；
- 各可观测性组件可以独立关闭；
- 配置不包含真实 Secret、私钥、证书或生产地址；
- 关键可配置项有适合 Kubernetes 初学者的中文注释；
- README 可让熟悉 Docker Compose 的用户完成部署、验证和卸载；
- 所有工作负载具有探针、资源规格、固定镜像版本和合理的安全上下文。