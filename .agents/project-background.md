# Project Background

## Purpose

MoLiAPI is a Spring Boot based API service that collects personal utility endpoints, scheduled jobs, static
documentation, and a small Vue admin frontend in one repository.

## Technology Stack

- Java 17.
- Spring Boot 4.
- MyBatis-Plus with XML mappers.
- Flyway for database migrations.
- H2 for default local/test startup.
- MySQL for Docker and production-like deployments.
- Redis for cache and distributed state where enabled.
- Maven wrapper for backend builds.
- Vue 3, Vite, TypeScript, Element Plus, and Pinia for the admin frontend.
- Docker Compose for local infrastructure and observability services.

## Backend Layout

- Feature packages live under `com.bingchunmoli.api`.
- Controllers expose HTTP APIs and should keep business logic in services.
- Services own transaction boundaries and integration behavior.
- Mapper interfaces and XML files provide persistence access.
- Configuration classes live under `config` packages.
- Scheduled tasks live in feature-specific `task` packages.
- Domain exceptions live under `exception`.

## Data And Persistence

- Flyway migrations are stored in `src/main/resources/db/migration`.
- Initialization SQL and seed data are stored in `src/main/resources/init`.
- MyBatis XML mapper files are stored in `src/main/resources/mapper`.
- The default `application.yml` uses an in-memory H2 database in MySQL compatibility mode.
- Docker and template configuration use MySQL with `utf8mb4` settings.

## Configuration Model

- `src/main/resources/application.yml` is a safe default configuration.
- `src/main/resources/application-template.yml` documents deployment values and should not contain real secrets.
- Local profile files matching `application-*.yml` should remain ignored unless they are explicit public examples.
- `.env` files are local-only and should not be committed.

## Frontend Layout

- The admin app lives under `src/main/vue/admin`.
- It uses Vue Router, Pinia, Element Plus, and Vite.
- Generated files such as `auto-imports.d.ts` and `components.d.ts` currently exist in the repository; if this policy
  changes, update the decision history and ignore rules together.

## Operational Context

- The compose stack includes MySQL, Redis, Loki, Grafana, Promtail, Prometheus, Nginx, and the API service.
- Runtime data directories under `docker-compose/` should be treated as local generated state.
- Certificates, private keys, Google service account files, local upload directories, and logs must not be committed.
