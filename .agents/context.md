# Current Context

## Repository State

- Root instructions are in `AGENTS.md`.
- Public quick context is in `context.md`.
- AI project memory is in `.agents/`.
- MySQL guidance and references are available under `.agents/skills/mysql`.

## Safe Assumptions

- Use Java 17 and Maven wrapper-compatible workflows for backend work.
- Use the existing Spring Boot, MyBatis-Plus, Flyway, Lombok, and XML mapper patterns.
- Use Vue 3 and Vite patterns in `src/main/vue/admin`.
- Prefer focused tests for the feature being changed.
- Keep local secrets and generated runtime state out of Git.

## Caution Areas

- `.env` files may exist locally and should not be copied into documentation or commits.
- `application-template.yml` contains placeholders and examples; it is not a secret source.
- Docker Compose mounts can create large local data directories under `docker-compose/`.
- Some README and YAML comments appear to contain mojibake; avoid broad encoding rewrites unless that is the task.

## Recommended First Reads

1. `AGENTS.md`
2. `context.md`
3. `.agents/project-background.md`
4. `.agents/decision-history.md`
5. Relevant feature package under `src/main/java/com/bingchunmoli/api`
