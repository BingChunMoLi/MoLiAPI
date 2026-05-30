# AI Collaboration Notes

This directory stores AI-facing project memory that is safe to keep in the repository.

## Files

- `project-background.md`: stable project background and architecture notes.
- `decision-history.md`: durable technical decisions and the reason behind them.
- `context.md`: short handoff context for the current repository state.
- `skills/`: reusable agent skills and references.

## Rules

- Keep this directory free of secrets, tokens, private URLs, customer data, and local-only paths.
- Prefer facts from the repository over guesses.
- When a decision changes, update `decision-history.md` instead of leaving stale context.
- Keep short-lived personal notes in ignored local files such as `.agents/context.local.md`.
