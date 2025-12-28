# ProductCatalogAPI

Lightweight Spring Boot sample API used as a demo project and development playground.

**Quick overview**
- **Language:** Java 21
- **Framework:** Spring Boot 3.3.2
- **Build:** Maven
- **Persistence:** Spring Data JPA (H2 in-memory by default)
- **Migrations:** Flyway (SQL migrations in `src/main/resources/db/migration`)

**Useful Commands**
- **Build:** `mvn -U clean package`
- **Run (local):** `mvn spring-boot:run` or `java -jar target/*.jar`
- **Run tests:** `mvn test` or `mvn -Dtest=<TestName> test`
- **Run with Docker Compose:** `docker compose up --build`
- **Build Docker image:** `docker build -t product-catalog-api:local .`

**Docker**
- A multi-stage `Dockerfile` is included for building a runnable image.
- `docker-compose.yml` provides a simple `app` service exposing port `8080`.
- Use `SPRING_PROFILES_ACTIVE` to switch profiles (compose currently uses `prod`).

**Database / Migrations**
- Flyway is configured via the `flyway-core` dependency.
- Migrations live in `src/main/resources/db/migration`:
  - `V1_create_tables.sql` — creates `categories` and `products` tables.
  - `V2_seed_data.sql` — optional seed/demo data.
- Default `application.yml` uses an in-memory H2 DB for dev/tests; a `prod` profile is provided that disables `hibernate.ddl-auto` so Flyway controls schema.

**Project Structure (high-level)**
- `src/main/java/com/product/catalog/api`
  - `controller/` — REST controllers
  - `service/` — transactional business logic
  - `repository/` — Spring Data JPA repositories
  - `dto/` — DTO records and `domain/` entities
  - `handler/` — global exception handler and error DTOs
  - `filter/`, `config/` — request filters and configuration classes
- `src/test/java/...` — unit and integration tests

**Conventions & Notes**
- DTOs are implemented as Java `record`s in `dto/` (requests/responses). Keep validation annotations on request record components.
- Domain logic (state changes and validation) lives inside JPA entities in `dto/domain` (e.g., `Product` methods like `update`, `changeName`).
- Services are `@Transactional`; controllers should delegate to services.
- Use Spring Data derived method names for repository queries (e.g. `existsByNameIgnoreCase`).
- API errors are produced by `handler/ApiExceptionHandler` and shaped as `ApiError` objects.

**Local development tips**
- Tests use an in-memory H2 DB; run `mvn test` locally or push to CI (GitHub Actions workflow included).
- If you want Flyway to manage schema locally, set `SPRING_PROFILES_ACTIVE=prod` (production profile disables `hibernate.ddl-auto`).
- For faster iterative development, you can run the app via your IDE with annotation processing enabled for Lombok.

**Swagger / OpenAPI**
- **Live UI:** start the app and open `http://localhost:8080/swagger-ui.html` or `http://localhost:8080/swagger-ui/index.html`.
- **OpenAPI JSON:** `GET http://localhost:8080/v3/api-docs`
- **Generate static docs (build-time):** the project includes the `springdoc-openapi-maven-plugin` which can produce `openapi.json` and `openapi.yaml` into the build directory.

To generate static OpenAPI files (requires the app to be reachable on `http://localhost:8080`):
```bash
# Start the app in the background (or in another terminal):
mvn spring-boot:run

# Generate OpenAPI JSON and YAML into target/generated-resources/openapi:
mvn generate-resources

# The files will be available at:
# target/generated-resources/openapi/openapi.json
# target/generated-resources/openapi/openapi.yaml
```

You can also run the plugin goal directly (when the app is running):
```bash
mvn org.springdoc:springdoc-openapi-maven-plugin:generate -Dspringdoc.apiDocsUrl=http://localhost:8080/v3/api-docs
```

**CI**
- A GitHub Actions workflow (`.github/workflows/ci.yml`) runs `mvn clean test` on push/PR.

**Next steps / optional improvements**
- Add `application-test.yml` to explicitly control test DB settings (e.g., `ddl-auto:create-drop`).
- Add healthchecks, Actuator, or Prometheus metrics if you want observability in Docker.
- Consider MapStruct for mapping if mapper classes grow large.

If you want, I can add a short `README` snippet to the repo root describing how to run tests in CI, or append a development checklist.
