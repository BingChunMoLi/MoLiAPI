# MoLiAPI Context

## Snapshot

MoLiAPI is a personal API service and admin system. The backend is a Java 17 Spring Boot application, and the admin
frontend is a Vue 3 application under `src/main/vue/admin`.

## Main Capabilities

- Bing image APIs.
- Emoji handling.
- Public IP and User-Agent parsing.
- QR code utilities.
- Random poem, quote, music, and image endpoints.
- Weather query and notification tasks.
- Hosts subscription support.
- Daily check-in account and log management.
- Netease Cloud Music playlist collection.
- Bilibili favorite video refresh and notification workflow.
- Tencent CDN certificate update task.

## Architecture

- `src/main/java/com/bingchunmoli/api`: backend source code grouped by feature package.
- `src/main/resources/mapper`: MyBatis XML mapper files.
- `src/main/resources/db/migration`: Flyway migrations.
- `src/main/resources/init`: bundled initialization SQL and seed data.
- `src/main/resources/static`: Docsify static documentation and public assets.
- `src/main/vue/admin`: Vue admin frontend.
- `docker-compose.yml` and `docker-compose/`: local service stack for MySQL, Redis, observability, Nginx, and the API.

## Runtime Defaults

- Default application profile uses H2 in MySQL compatibility mode.
- Docker/template configuration targets MySQL and Redis service names from the compose network.
- The default HTTP port is `8090`.
- Configuration templates should be kept safe to publish. Real credentials belong in local config files or deployment
  secrets.

## Development Commands

```shell
mvn test
```

```shell
docker-compose up -d
```

```shell
cd src/main/vue/admin
yarn build
```

## Notes For Future Agents

- Read `AGENTS.md` before changing code.
- Read `.agents/project-background.md` and `.agents/decision-history.md` before larger design changes.
- Do not treat `.env`, local profile YAML files, generated Docker volumes, certificates, or local upload directories as
  source files.
