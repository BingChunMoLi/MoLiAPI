# Agent Instructions

This repository is a Spring Boot API project with a Vue admin frontend. Follow these instructions when using AI agents
or automated coding assistants in this codebase.

## Project Context

- Backend: Java 17, Spring Boot, MyBatis-Plus, Flyway, H2 for default/local test profile, MySQL for Docker and
  production-like deployments.
- Frontend: Vue 3, Vite, TypeScript, Element Plus, Pinia.
- Runtime integrations include Redis, Mail, Loki/Grafana/Prometheus, Tencent Cloud, Firebase Admin, and third-party
  public APIs.
- Public configuration should stay template-based. Never commit real secrets, private certificates, production
  credentials, local `.env` files, or generated runtime data.
- AI-facing project notes live in `.agents/`. The concise current project context is in `context.md`.

## Code Formatting

- Indentation: 4 spaces.
- Blank Lines: Use to separate logical blocks of code.
- Line Length: Maximum 120 characters.
- Use IntelliJ IDEA default code style for Java.

## Java Style

- Use UTF-8 encoding.
- Use descriptive names for classes, methods, and variables.
- Avoid `var` keyword, prefer explicit types.
- All method parameters should be `final`.
- All variables should be declared as `final` where possible.
- Preference for immutability.
- Avoid mutations of objects, specially when using for-each loops or Stream API using `forEach()`.
- Avoid magic numbers and strings; use constants instead.
- Check emptiness and nullness before operations on collections and strings.
- Avoid methods using `throws` clause; prefer unchecked exceptions.
- Avoid comments.
- Comments could be applied for: cron expressions, Regex patterns, TODOs or given/when/then separation in tests.
- Use `@Override` annotation when overriding methods.
- Avoid `Objects.isNull()` and `Objects.nonNull()` for one or two variables; prefer direct null checks for better
  performance.
- Wrap multiple conditions in a boolean variable for better readability.
- Prefer early returns.
- Avoid else statements when not necessary and try early returns.

## Lombok Annotations

- Use `@RequiredArgsConstructor` from Lombok for dependency injection via constructor.
- Use `@Slf4j` from Lombok for logging.
- Use `@Builder(setterPrefix = "with")` for complex object creation.
- Avoid `@Data` annotation; prefer `@Getter` and `@Setter` for granular control.

## Annotations

- **`@Service`**: For business logic classes.
- **`@Repository`**: For data access classes that extend JPA repositories or interact with the database.
- **`@RestController`**: For web controllers.
- **`@Component`**: For generic Spring components.
- **`@Configuration`**: For Spring configuration classes.
- **`@Autowired`**: Prefer constructor injection for production code and field injection only for tests.
- **`@ConfigurationProperties`**: For binding related properties avoid multiple `@Value` annotations. From more than 2
  properties, consider using this annotation.
- **`@Transactional`**: Only Service classes should be annotated with `@Transactional` at class level to avoid
  transaction management in each method.
- **`@Validated`**: To enable Bean Validation in method parameters or classes.
- **`@PreAuthorize`**: At the controller layer when using Spring Security to enforce method-level security.
- Circular dependencies should be avoided. Avoid `@Order` annotation for dependency resolution.

## Persistence And Mappers

- This project uses MyBatis-Plus with XML mapper files under `src/main/resources/mapper`.
- Flyway migrations live under `src/main/resources/db/migration`.
- Keep schema changes backward-compatible when possible and document migration risks.
- Prefer small, measured database changes. For MySQL, consider indexes, transaction boundaries, and lock behavior before
  changing hot paths.
- Mapper interfaces should have the `Mapper` suffix.
- Mapper method names should describe the operation clearly.

## Mappers

As a development team choose MapStruct or strictly static Mappers.

**Use MapStruct**

- MapFor mapping between DTOs and entities.
- Define mapper interfaces with `@Mapper` annotation.
- Use `@Mapping` annotation for custom field mappings.
- Use `componentModel = "spring"` to allow Spring to manage mapper instances.
- Mapper should have as suffix `Mapper` (e.g., `UserMapper`).
- Name mapper methods clearly (e.g., `toDto`, `toEntity`).
- Example Mapper Interface:

  ```java
  @Mapper(componentModel = "spring")
  public interface UserMapper {
      @Mapping(source = "email", target = "emailAddress")
      UserDTO toDto(User user);

      @Mapping(source = "emailAddress", target = "email")
      User toEntity(UserDTO userDto);
  }
  ```

## Testing And Verification

- New backend features or behavior changes should include matching layered unit tests for the affected `Controller`,
  `Service`, and `Mapper` classes.
- Controller tests should verify request validation, authentication/authorization behavior when applicable, response
  status codes, and response payload shape. Example cases: valid request returns the expected DTO; invalid parameters
  return a validation error; unauthorized or forbidden requests are rejected.
- Service tests should verify business rules, branching logic, transaction-facing behavior, and interactions with
  dependencies through mocks or focused test doubles. Example cases: successful command/query returns the expected
  result; missing or invalid domain data raises the expected domain exception; duplicate or conflicting input is handled
  without writing invalid state.
- Mapper tests should verify custom SQL, XML mapper bindings, result mapping, dynamic query conditions, and pagination
  or ordering when present. Example cases: inserted fixture rows are selected correctly; optional filters include or
  exclude rows as expected; update/delete operations affect only the intended rows.
- For backend changes, prefer focused Maven tests first:

  ```shell
  mvn test
  ```

- For frontend changes, run the relevant Vite/Vue checks from `src/main/vue/admin` when dependencies are available:

  ```shell
  yarn build
  yarn type-check
  ```

- If tests cannot be run locally, state why and describe the residual risk.

## Open Source Hygiene

- Keep examples and templates commit-safe.
- Do not commit real `.env` files, local profile files, generated Docker volumes, logs, certificates, upload caches, or
  IDE workspace state.
- Prefer documenting required configuration in templates rather than relying on local files.
