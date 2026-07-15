---
title: 'Project Initialization and Base Model Setup'
type: 'feature'
created: '2026-07-15'
status: 'done'
baseline_commit: 'NO_VCS'
review_loop_iteration: 1
context: ['D:/OJT/week1/_bmad-output/project-context.md']
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The Project Resource Allocation Management System is currently empty without any Maven project structure, PostgreSQL database setup, or database tables/entities defined. A dockerized PostgreSQL instance is needed to run the application locally without requiring manual database installations.

**Approach:** Initialize a Maven-based Spring Boot 3.3.x application with Java 17, docker-compose configuration for PostgreSQL and pgAdmin, basic schema.sql script matching the SQL specifications exactly, JPA Entity classes with correct relationships, Repositories, and a health check API endpoint to verify system connectivity.

## Boundaries & Constraints

**Always:**
- Use Maven for dependency management with Java 17 and Spring Boot 3.3.x.
- Keep local PostgreSQL on port 5432 and run pgAdmin on port 5050 to avoid conflicts.
- Implement the exact table definitions and column constraints from PRD Section 5.
- Apply Constructor Injection using `@RequiredArgsConstructor` for dependency injection.
- Mark `@ManyToOne` relations as `FetchType.LAZY` in the `Allocation` entity.
- Write request validation constraints on DTOs and validate requests in controllers using `@Valid`.

**Ask First:**
- Changing base package name `com.company.resourcealloc`.
- Changing default PostgreSQL port or database credentials.
- Adding additional third-party dependencies outside the standard Spring Boot starters and PostgreSQL driver.

**Never:**
- Commit credentials or sensitive environment configurations (.env) directly to version control.
- Use `@Data` annotation on JPA Entity classes to prevent loop recursions in toString/equals/hashCode methods.
- Use `@Autowired` field injection in controllers or services.
- Define allocation percentages outside the `0 < allocation <= 100` bounds.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| DB Up & Connected | GET /api/health | HTTP 200: {"status": "UP"} | N/A |
| DB Down | GET /api/health | HTTP 500 or connection failure | Throw exception, return HTTP 500 |

</frozen-after-approval>

## Code Map

- `.env` -- Local environment variables for database credentials and configurations.
- `docker-compose.yml` -- Defines the local PostgreSQL and pgAdmin services reading from .env.
- `pom.xml` -- Project dependencies and Maven build configuration.
- `src/main/resources/application.yml` -- Database credentials, port routing, and JPA init configurations using environment variables.
- `src/main/resources/schema.sql` -- Raw SQL scripts creating `employee`, `project`, and `allocation` tables.
- `src/main/java/com/company/resourcealloc/ResourceAllocApplication.java` -- Main entry point for the Spring Boot application.
- `src/main/java/com/company/resourcealloc/model/Employee.java` -- JPA Entity mapping the `employee` table.
- `src/main/java/com/company/resourcealloc/model/Project.java` -- JPA Entity mapping the `project` table with Project Status Enum.
- `src/main/java/com/company/resourcealloc/model/Allocation.java` -- JPA Entity mapping the `allocation` table.
- `src/main/java/com/company/resourcealloc/repository/EmployeeRepository.java` -- Employee data access interface.
- `src/main/java/com/company/resourcealloc/repository/ProjectRepository.java` -- Project data access interface.
- `src/main/java/com/company/resourcealloc/repository/AllocationRepository.java` -- Allocation data access interface.
- `src/main/java/com/company/resourcealloc/controller/HealthController.java` -- Basic REST controller to verify database connection health.

## Tasks & Acceptance

**Execution:**
- [x] `.env` -- Create file -- Define database credentials (POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD) and pgAdmin configurations.
- [x] `docker-compose.yml` -- Create file -- Set up PostgreSQL 15 database service and pgAdmin web interface loading credentials from .env.
- [x] `pom.xml` -- Create file -- Setup Maven dependencies for Spring Boot 3.3.x, JPA, Validation, PostgreSQL Driver, and Lombok.
- [x] `src/main/resources/application.yml` -- Create file -- Setup Spring Datasource mapping to environment variables, JPA execution options, and logging properties.
- [x] `src/main/resources/schema.sql` -- Create file -- Implement the schema tables (`employee`, `project`, `allocation`) with proper PK and constraints.
- [x] `src/main/java/com/company/resourcealloc/ResourceAllocApplication.java` -- Create file -- Main entrypoint method to bootstrap Spring Boot.
- [x] `src/main/java/com/company/resourcealloc/model/Employee.java` -- Create file -- Implement Employee entity class with validations.
- [x] `src/main/java/com/company/resourcealloc/model/Project.java` -- Create file -- Implement Project entity class and ProjectStatus enum.
- [x] `src/main/java/com/company/resourcealloc/model/Allocation.java` -- Create file -- Implement Allocation entity class with LAZY mappings to Employee and Project.
- [x] `src/main/java/com/company/resourcealloc/repository/EmployeeRepository.java` -- Create file -- Extend JpaRepository for Employee operations.
- [x] `src/main/java/com/company/resourcealloc/repository/ProjectRepository.java` -- Create file -- Extend JpaRepository for Project operations.
- [x] `src/main/java/com/company/resourcealloc/repository/AllocationRepository.java` -- Create file -- Extend JpaRepository for Allocation operations.
- [x] `src/main/java/com/company/resourcealloc/controller/HealthController.java` -- Create file -- Simple health endpoint testing active DB connection.

**Acceptance Criteria:**
- Given a running postgres container, when executing maven package `./mvnw clean package`, then the compilation succeeds.
- Given a running postgres container, when launching the application, then the schema.sql script is executed successfully and all tables are verified in PostgreSQL.
- Given the application is running, when calling `GET http://localhost:8080/api/health`, then it returns HTTP 200 with `{"status": "UP"}`.

## Spec Change Log

### Iteration 1 Review Loop (2026-07-15)
- **Triggering Findings:**
  1. Security Risk: Exposure of raw database query exceptions in HealthController API.
  2. Data Risk: Destructive DDL `DROP TABLE` in schema.sql coupled with `always` run mode risks production data loss.
  3. Database Constraints: Missing database CHECK constraints for allocation percentages (1-100) and date validation (startDate <= endDate).
  4. Validation Gaps: Missing `@Size` annotations matching DB column lengths on entity fields.
  5. Performance: Missing foreign key indexes on `allocation` table.
  6. Docker: Missing PostgreSQL health check and default fallbacks in docker-compose.yml.
- **Amendments Made:**
  - Configured `HealthController` to return a generic connection check message and log the exception. Added query timeout of 3s.
  - Recommended running `schema.sql` database initialization only in local or dev profiles.
  - Added range and date CHECK constraints in `schema.sql` and corresponding `@Size` validations on entity fields.
  - Added indexes on `employee_id` and `project_id` on the `allocation` table in `schema.sql`.
  - Added healthcheck to postgres container in `docker-compose.yml` and environment fallback defaults.
- **Known-Bad State Avoided:** Production data loss during startup, raw server internal stacktrace disclosure, invalid allocation records stored in database, and Hibernate database insertion crashes due to unvalidated input lengths.
- **KEEP Instructions:** Keep Lombok configuration without `@Data`, keep constructor DI patterns, and maintain LAZY fetching strategies.

## Design Notes

No complex business calculations are implemented in this initialization phase. Database constraints are verified on start up.

## Verification

**Commands:**
- `docker compose up -d` -- Spin up PostgreSQL database container.
- `./mvnw clean package` -- Build the project to verify dependencies compile without errors.
- `./mvnw spring-boot:run` -- Launch the application locally and verify database initialization.

## Suggested Review Order

**Database Schema & Environment Configuration**

- Defines database variables used by docker-compose and Spring Boot datasource.
  [`.env:1`](../../.env#L1)

- Starts PostgreSQL 15 and pgAdmin containers, mapping to env credentials with health checks.
  [`docker-compose.yml:1`](../../docker-compose.yml#L1)

- Raw DDL defining database tables, ranges, dates, unique email, and FK indexes.
  [`schema.sql:1`](../../src/main/resources/schema.sql#L1)

**JPA Entities & Model**

- Maps employee table, validates email formats, unique constraint, and length limits.
  [`Employee.java:1`](../../src/main/java/com/company/resourcealloc/model/Employee.java#L1)

- Maps project table and sets up status enum parsing options.
  [`Project.java:1`](../../src/main/java/com/company/resourcealloc/model/Project.java#L1)

- Defends date range boundaries and forces lazy fetching on associations.
  [`Allocation.java:1`](../../src/main/java/com/company/resourcealloc/model/Allocation.java#L1)

**Spring Boot Infrastructure**

- Bootstraps dependency versions for Boot 3.3.x, JPA, and Lombok.
  [`pom.xml:1`](../../pom.xml#L1)

- Maps database settings dynamically to environments, sets init to always.
  [`application.yml:1`](../../src/main/resources/application.yml#L1)

- Exposes health check running timed SQL check, shielding internal traces.
  [`HealthController.java:1`](../../src/main/java/com/company/resourcealloc/controller/HealthController.java#L1)
