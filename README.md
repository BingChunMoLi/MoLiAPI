# A moliapi golang version

## Quick start
> go build -o moliapi main.go && ./moliapi

## Docker

## 证书自动更新模块

支持阿里云 (CDN, DCDN, OSS) 和腾讯云 (CDN, EdgeOne, COS) 的证书自动更新。

### 配置方式
支持 `config.yaml` 文件或环境变量 (ENV) 混合配置。环境变量优先级高于配置文件。

#### 1. 配置文件 (`config.yaml`)
```yaml
aliyun_access_key_id: "your_ak"
aliyun_access_key_secret: "your_sk"
tencent_secret_id: "your_id"
tencent_secret_key: "your_key"
cron: "0 0 * * *"
api_key: "your_optional_secure_key"
tasks:
  - cert_path: "/certs/example.com/fullchain.pem"
    key_path: "/certs/example.com/privkey.pem"
    targets:
      - provider: "aliyun"
        service: "cdn"
        domain: "cdn.example.com"
```

#### 2. 触发方式
- **定时任务 (Cron)**: 默认开启，可通过 `DISABLE_CRON=true` 关闭（Serverless 环境下建议关闭）。
- **HTTP 接口**: `POST /cert/update`。支持 `X-API-Key` 请求头或 `api_key` 查询参数鉴权。
- **acme.sh 集成**:
  `acme.sh` 续期后可直接调用：
  ```bash
  curl -X POST "http://your-api.com/cert/update?api_key=your_key"
  ```
