# Kubernetes 部署

本目录提供 Kustomize 和 Helm 两种独立部署方式。两者部署的组件、端口、Secret 键名和默认资源规格一致。
如果你熟悉 Docker Compose，可以先阅读下面的概念映射，再选择一种方式部署。

## 部署内容

| 模式 | MoLiAPI | MySQL | Redis | Prometheus | Grafana |
| --- | --- | --- | --- | --- | --- |
| 最小化 | 启用 | 启用 | 启用 | 关闭 | 关闭 |
| 全量指标监控 | 启用 | 启用 | 启用 | 启用 | 启用 |

Prometheus 和 Grafana 不是应用启动依赖。如果集群或云厂商已经提供监控平台，使用最小化模式即可，并让现有
Prometheus 抓取 `http://moliapi:8090/actuator/prometheus`。

本配置不部署 Loki、Promtail、Grafana Alloy 或其他日志采集系统。应用使用 JSON 标准输出，可通过
`kubectl logs` 查看。需要集中日志时，应作为集群级基础设施单独建设。

## 从 Docker Compose 理解 Kubernetes

| Docker Compose | Kubernetes | 作用 |
| --- | --- | --- |
| `service` | Deployment/StatefulSet + Service | 运行容器并提供稳定网络名称 |
| `ports` | Service + Ingress | 集群内访问和 HTTP 对外入口 |
| `environment` | ConfigMap + Secret + `env` | 注入普通配置和敏感配置 |
| `volumes` | PVC、ConfigMap volume | 持久化数据或挂载配置 |
| `depends_on` | initContainer + Probe | 等待依赖并判断容器是否可用 |
| Compose profile | Kustomize overlay / Helm values | 组合最小化或全量组件 |

MySQL 和 Redis 使用 StatefulSet，因为它们需要稳定身份和持久化卷。MoLiAPI、Prometheus 和 Grafana 使用
Deployment。所有 Service 默认是 ClusterIP，只有 MoLiAPI 通过 Ingress 暴露。

## 前置条件

- Kubernetes 1.27 或更高版本；
- 集群存在默认 StorageClass，或你已在配置中填写 `storageClassName`；
- 集群已经安装 Ingress Controller；
- 本机可使用 `kubectl`；
- 使用 Helm 方式时需要 Helm 3。

```powershell
kubectl version --client
kubectl get storageclass
kubectl get ingressclass
helm version
```

## 准备 Namespace 和 Secret

先创建 Namespace：

```powershell
kubectl create namespace moliapi --dry-run=client -o yaml | kubectl apply -f -
```

复制 Secret 模板：

```powershell
Copy-Item k8s/kustomize/base/secret.template.yaml k8s/kustomize/base/secret.yaml
```

编辑 `secret.yaml`，替换下面四个值：

- `MYSQL_ROOT_PASSWORD`：仅用于 MySQL 管理和健康检查；
- `MYSQL_PASSWORD`：MoLiAPI 使用的数据库用户密码；
- `REDIS_PASSWORD`：Redis 和应用共同使用的密码；
- `GF_SECURITY_ADMIN_PASSWORD`：全量模式下的 Grafana 管理员密码。

模板中的 `example-only-*` 仅用于说明，不能直接使用。`secret.yaml` 已被 Git 忽略，不要提交真实密码。

```powershell
kubectl apply -f k8s/kustomize/base/secret.yaml
kubectl get secret moliapi-secrets -n moliapi
```

## Kustomize 最小化部署

```powershell
kubectl apply -k k8s/kustomize/overlays/minimal
kubectl get pods,pvc,service,ingress -n moliapi
kubectl rollout status deployment/moliapi -n moliapi --timeout=5m
```

默认域名是 `moliapi.local`。部署前应编辑 `k8s/kustomize/base/ingress.yaml`，换成真实域名；如果集群没有
默认 IngressClass，还要增加 `spec.ingressClassName`。

## Kustomize 全量部署

```powershell
kubectl apply -k k8s/kustomize/overlays/full
kubectl get pods,pvc,service,ingress -n moliapi
kubectl rollout status deployment/moliapi-prometheus -n moliapi --timeout=5m
kubectl rollout status deployment/moliapi-grafana -n moliapi --timeout=5m
```

只需要其中一个指标组件时，复制 `overlays/full`，再从 `components` 中删除不需要的条目。

## Helm 最小化部署

先按“准备 Namespace 和 Secret”创建 Secret，然后运行：

```powershell
helm upgrade --install moliapi k8s/helm/moliapi `
    --namespace moliapi `
    --create-namespace `
    -f k8s/helm/moliapi/values-minimal.yaml
kubectl get pods,pvc,service,ingress -n moliapi
```

## Helm 全量部署

```powershell
helm upgrade --install moliapi k8s/helm/moliapi `
    --namespace moliapi `
    --create-namespace `
    -f k8s/helm/moliapi/values-full.yaml
kubectl get pods,pvc,service,ingress -n moliapi
```

Helm 支持单独开关组件，例如只启用 Prometheus：

```powershell
helm upgrade --install moliapi k8s/helm/moliapi `
    --namespace moliapi `
    --set prometheus.enabled=true `
    --set grafana.enabled=false
```

## 常用配置

### 功能开关

Kubernetes 默认关闭启动初始化、一言预加载和全部定时任务。通过 ConfigMap 或 Helm values 生成的额外环境
变量按需开启，例如：

```yaml
MOLI_FEATURES_SCHEDULING: "true"
MOLI_FEATURES_TASKS_BING: "true"
MOLI_FEATURES_TASKS_TENCENT_CDN_CERTIFICATE: "false"
```

只打开任务开关不会执行任务，必须同时启用 `MOLI_FEATURES_SCHEDULING`。开启依赖第三方平台的任务前，应先
通过 Secret 或外部配置提供对应密钥。

### 镜像

- Kustomize：修改相应 workload YAML 中的 `image`；
- Helm：在本地 `values.local.yaml` 中覆盖 `image.tag`、`mysql.image`、`redis.image` 等值。

不要使用 `latest`，否则同一份配置在不同时间可能运行不同镜像。

### Ingress 和 TLS

Kustomize 在 `k8s/kustomize/base/ingress.yaml` 中配置。Helm 示例：

```yaml
ingress:
    className: nginx
    host: api.example.com
    tls:
        enabled: true
        secretName: api-example-com-tls
```

TLS Secret 必须预先存在于 `moliapi` Namespace，不要把证书或私钥提交到仓库。

### 存储与资源

StorageClass 留空时使用集群默认值。没有默认 StorageClass 时，PVC 会一直处于 `Pending`。默认容量为：
应用 5 Gi、MySQL 10 Gi、Redis 2 Gi、Prometheus 10 Gi、Grafana 5 Gi。

`requests` 是调度时预留资源，`limits` 是容器可使用的上限。若 Pod 出现 `OOMKilled`，先检查实际使用量，
再调整内存，不要直接删除 limits。

### 使用外部 MySQL 或 Redis

Helm 可关闭内置组件并填写外部地址：

```yaml
mysql:
    enabled: false
redis:
    enabled: false
external:
    mysql:
        host: mysql.example.internal
        port: 3306
    redis:
        host: redis.example.internal
        port: 6379
```

外部服务的密码仍使用 `moliapi-secrets` 中相同键名。

## 状态检查与日志

```powershell
kubectl get all,pvc,ingress -n moliapi
kubectl describe pod -n moliapi -l app.kubernetes.io/name=moliapi
kubectl logs deployment/moliapi -n moliapi -f
kubectl logs statefulset/moliapi-mysql -n moliapi
kubectl logs statefulset/moliapi-redis -n moliapi
```

全量模式默认不对外暴露 Prometheus 和 Grafana。可临时端口转发：

```powershell
kubectl port-forward service/moliapi-prometheus 9090:9090 -n moliapi
kubectl port-forward service/moliapi-grafana 3000:3000 -n moliapi
```

## 升级与回滚

Kustomize 修改后重新应用：

```powershell
kubectl apply -k k8s/kustomize/overlays/minimal
kubectl rollout status deployment/moliapi -n moliapi --timeout=5m
kubectl rollout undo deployment/moliapi -n moliapi
```

Helm 升级和回滚：

```powershell
helm upgrade moliapi k8s/helm/moliapi -n moliapi -f k8s/helm/moliapi/values-minimal.yaml
helm history moliapi -n moliapi
helm rollback moliapi 1 -n moliapi
```

MySQL 镜像升级前必须先备份数据库并确认版本兼容性。
MySQL 的 `lower_case_table_names=1` 只能在空数据目录首次初始化时确定；已有 PVC 不要直接修改该参数，
应通过备份和恢复迁移到新实例。

## 卸载与数据保留

Kustomize 安全卸载命令只删除工作负载和网络/配置资源，保留 Namespace、Secret 和 PVC：

```powershell
kubectl delete deployment,statefulset,service,ingress,configmap `
    -l app.kubernetes.io/part-of=moliapi `
    -n moliapi
```

不要使用 `kubectl delete -k` 作为常规卸载命令，因为它会删除清单中显式定义的 PVC。

Helm 卸载：

```powershell
helm uninstall moliapi -n moliapi
kubectl get pvc -n moliapi
```

Chart 使用 `helm.sh/resource-policy: keep` 保留显式 PVC；StatefulSet 创建的 PVC 默认也会保留。

以下操作会永久删除数据库、缓存和应用数据，仅在确认不需要恢复时执行：

```powershell
kubectl delete pvc --all -n moliapi
kubectl delete secret moliapi-secrets -n moliapi
kubectl delete namespace moliapi
```

## 故障排查

### Pod 显示 CreateContainerConfigError

通常是 `moliapi-secrets` 不存在或缺少键：

```powershell
kubectl describe pod -n moliapi <pod-name>
kubectl get secret moliapi-secrets -n moliapi
```

### PVC 一直 Pending

```powershell
kubectl get storageclass
kubectl describe pvc -n moliapi <pvc-name>
```

确认集群有默认 StorageClass，或者为每个 PVC 配置正确的 StorageClass。

### Ingress 没有地址

```powershell
kubectl get ingressclass
kubectl describe ingress moliapi -n moliapi
```

确认 Ingress Controller 正常运行、className 正确，并且域名 DNS 指向 Controller 地址。

### 应用探针失败

```powershell
kubectl describe pod -n moliapi -l app.kubernetes.io/name=moliapi
kubectl logs deployment/moliapi -n moliapi --tail=200
```

重点检查 Flyway、MySQL 连接、Redis 连接和 Actuator 端点。

### MySQL 或 Redis 未就绪

```powershell
kubectl logs statefulset/moliapi-mysql -n moliapi --tail=200
kubectl logs statefulset/moliapi-redis -n moliapi --tail=200
```

已有 PVC 中保存了旧密码时，修改 Secret 不会自动修改数据库内部密码，需要使用原密码进入服务后执行密码迁移。

### ImagePullBackOff

```powershell
kubectl describe pod -n moliapi <pod-name>
```

检查镜像标签是否存在、节点能否访问镜像仓库，以及私有仓库的 `imagePullSecrets` 是否配置正确。
