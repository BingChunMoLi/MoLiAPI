# Decision History

## 2026-05-30: Keep AI project memory in `.agents/`

Decision: Store AI-facing repository context in `.agents/` and keep a concise public handoff file at root `context.md`.

Reason: The repository already uses `.agents/skills` for reusable agent knowledge. Keeping project background,
decision history, and short context near those skills makes it easier for different AI tools to load the same source of
truth.

Impact:

- `AGENTS.md` remains the main coding instruction file.
- `.agents/project-background.md` stores durable architecture and stack notes.
- `.agents/decision-history.md` stores decisions that should survive across sessions.
- `.agents/context.md` stores short current handoff context.
- `context.md` gives humans and agents a quick root-level overview.

## 2026-05-30: Treat local configuration and runtime state as private

Decision: Expand ignore rules for open source hygiene around `.env`, local Spring profiles, generated Docker volumes,
logs, certificates, upload caches, frontend dependency/build output, and temporary files.

Reason: This project integrates with external services and can run a full local stack. Open source contributors need
safe templates and examples, while secrets and generated runtime state should stay outside Git.

Impact:

- Real `.env` files and local profile YAML files should not be committed.
- Public templates remain allowed.
- Docker Compose configuration files remain source files, but service data directories are ignored.

## 2026-05-30: Keep backend defaults runnable without external MySQL

Decision: Preserve the default H2-based application configuration while documenting MySQL as the Docker and
production-like database.

Reason: H2 keeps local tests and first startup lightweight. MySQL remains the target for compose deployments and
database behavior that needs production-like validation.

Impact:

- Use H2 for simple local tests.
- Validate MySQL-specific SQL, indexes, migrations, and lock-sensitive changes against MySQL before release.
- Keep Flyway migrations compatible with the intended MySQL deployment path.
