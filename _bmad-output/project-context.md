---
project_name: 'week1'
user_name: 'Hoang'
date: '2026-07-15'
sections_completed: ['technology_stack', 'language_rules', 'framework_rules', 'testing_rules', 'quality_rules', 'workflow_rules', 'anti_patterns']
status: 'complete'
rule_count: 24
optimized_for_llm: true
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

### Core Technologies
* **Java**: 17 (LTS) - Must use Java 17 features like records, switch expressions, and pattern matching.
* **Spring Boot**: 3.3.x - Using latest Spring Boot 3.3 release.
* **Database**: PostgreSQL (latest stable) - Running locally and in containerized dev environment.
* **Build Tool**: Maven (wrapper included, `mvnw`)

### Key Dependencies
* `org.springframework.boot:spring-boot-starter-data-jpa` - ORM and database access.
* `org.springframework.boot:spring-boot-starter-web` - REST API controllers.
* `org.springframework.boot:spring-boot-starter-validation` - Validation annotations (`@NotBlank`, `@Email`, `@Min`, `@Max`).
* `org.postgresql:postgresql` - PostgreSQL JDBC Driver.
* `org.projectlombok:lombok` - Reducing boilerplate code (Getters/Setters, Builders).

## Critical Implementation Rules

### Language-Specific Rules

* **Java 17 Features**:
  * Use **Record classes** for immutable data carriers, specifically for DTOs (Data Transfer Objects), Request Payloads, and Response Payloads.
  * Use **Text Blocks** (`"""..."""`) for multi-line SQL queries, JSON strings, or HTML templates.
  * Use **Pattern Matching for `instanceof`** to eliminate explicit casting.
  * Use **Switch Expressions** for yield-based assignment and pattern matching when resolving statuses or categories.
* **Optional Handling**:
  * Use `Optional<T>` as return types for methods (e.g., repository finders, service lookups) that can return empty values.
  * Do NOT use `Optional` for class fields or method parameters; use `@Nullable` or proper validation instead.
  * Avoid call chains like `.get()` without prior `.isPresent()` check; prefer `.orElse()`, `.orElseThrow()`, or `.ifPresent()`.
* **Streams and Collections**:
  * Use Java 16+ `Stream.toList()` directly instead of `.collect(Collectors.toList())`.
  * Prefer `List.of()`, `Map.of()`, and `Set.of()` to create immutable collections where updates are not needed.

### Framework-Specific Rules

* **Dependency Injection**:
  * ALWAYS use **Constructor Injection** for class dependencies.
  * Avoid `@Autowired` on fields; use Lombok's `@RequiredArgsConstructor` on the class level and declare fields as `private final`.
* **REST Controllers**:
  * Use `@RestController` and map endpoints using specific annotations (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`).
  * Return standard HTTP statuses: `201 Created` for creation, `200 OK` for success, `204 No Content` for deletions.
  * Use Java records for Request and Response DTOs.
* **Validation**:
  * Annotate Request DTO records with Bean Validation constraints (e.g., `@NotBlank`, `@Email`, `@NotNull`, `@Min(1)`, `@Max(100)`).
  * Use `@Valid` in Controller parameters to trigger validation.
* **Service Layer & Business Rules**:
  * All business decisions (such as validating total allocation limit of 100% and checking project status) must reside in the Service Layer, NOT in Controllers or Repositories.
  * Service methods that write, update, or delete records must be annotated with `@Transactional(rollbackFor = Exception.class)`.
* **Spring Data JPA & PostgreSQL**:
  * Prefer method name derivation for simple queries (e.g., `findByEmployeeCode`).
  * Use native queries (`nativeQuery = true`) or custom JPQL inside `@Query` for aggregation reports (e.g., Utilization Report, Available Resource Report).

### Testing Rules

* **Testing Stack**:
  * Use **JUnit 5** (`org.junit.jupiter`) and **AssertJ** (`org.assertj.core.api.Assertions`) for writing assertions.
  * Use **Mockito** (`org.mockito`) for mocking repository and external service dependencies.
* **Naming Conventions**:
  * Test classes must match the name of the class under test with a `Test` suffix (e.g., `AllocationServiceTest`).
  * Test methods should follow the pattern: `given[Setup]_when[Action]_then[ExpectedOutcome]` (e.g., `givenOverloadedAllocation_whenCreateAllocation_thenThrowAllocationExceededException`).
* **Unit Testing Boundaries**:
  * Service layer business logic must have 100% unit test coverage, especially edge cases for the 100% total allocation threshold and project status checks.
  * Use Mockito to isolate services. Do NOT spin up Spring Context (`@SpringBootTest`) for unit tests.
* **Controller testing**:
  * Use `@WebMvcTest` along with `MockMvc` and `@MockBean` to test controller routing, status codes, request validation, and JSON serialization/deserialization.
* **Test Isolation**:
  * Ensure database tests (if any integration tests are added) are isolated and rollback after execution, or use `@DataJpaTest` which is transactional by default.

### Code Quality & Style Rules

* **Package & Folder Structure**:
  * Organize code by technical layers under a root package (e.g., `com.company.resourcealloc`):
    * `.controller` - Contains REST Controllers.
    * `.dto` - Contains Record classes for requests/responses.
    * `.model` - Contains JPA Entities (`Employee`, `Project`, `Allocation`).
    * `.repository` - Contains JPA Repositories.
    * `.service` - Contains Service interfaces and their implementations.
    * `.exception` - Contains Custom Exceptions and Global Exception Handler.
* **Naming Conventions**:
  * **Java Classes/Interfaces**: `PascalCase` (e.g., `AllocationService`, `EmployeeRepository`).
  * **Methods & Variables**: `camelCase` (e.g., `calculateTotalAllocation`, `employeeId`).
  * **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_ALLOCATION_PERCENT = 100`).
  * **Database Tables & Columns**: `snake_case` matching the SQL Schema exactly (e.g., table `employee`, column `employee_code`).
* **Lombok Usage**:
  * Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` or `@Builder` on Entities.
  * Use `@RequiredArgsConstructor` for constructor dependency injection.
  * Avoid `@Data` on JPA Entities to prevent infinite recursion in `toString()`, `equals()`, or `hashCode()`.
* **Comments and Documentation**:
  * Write clear and concise code; avoid inline comments that restate what the code does. Use comments only to explain "why" complex allocation validation logic is written.

### Development Workflow Rules

* **Branch Naming Conventions**:
  * Features: `feat/[issue-number]-[description]` (e.g., `feat/EMP-01-employee-crud`).
  * Bug fixes: `fix/[issue-number]-[description]` (e.g., `fix/AL-04-allocation-overload-error`).
  * Tasks/Refactors: `refactor/[issue-number]-[description]`.
* **Commit Message Format**:
  * Follow **Conventional Commits**:
    * `feat: add employee workload query endpoint`
    * `fix: prevent allocation to completed projects`
    * `docs: update setup steps in readme`
    * `test: add unit tests for allocation service`
* **Pull Request (PR) Checklist**:
  * Build must succeed without warnings (`./mvnw clean package`).
  * All unit and integration tests must pass.
  * No syntax warnings or compile-time linter errors in IDE.
* **Database Updates**:
  * Store SQL schema creation scripts in `src/main/resources/schema.sql`.
  * Do NOT modify existing migration/schema scripts after they are pushed; write a new script for schema alterations.

### Critical Don't-Miss Rules

* **Anti-Patterns to Avoid**:
  * **Incorrect Allocation Update Math**: When updating an existing allocation, DO NOT sum the new value with the old database state blindly. You must subtract the *current record's allocation percent* from the sum before checking if the new sum exceeds 100%.
  * **Status Validation Bypass**: Do NOT allow creating or updating resource allocations for projects with `COMPLETED` status.
  * **N+1 Query Issue**: Do NOT execute separate database queries inside a loop when calculating employee utilization or workload reports. Use aggregate SQL functions (`SUM`, `GROUP BY`) or `JOIN FETCH` queries in JPA Repositories.
* **Edge Cases to Handle**:
  * **Self-Allocation Excludes**: Ensure the validation logic excludes the current allocation record ID during update checks so that updating the role or start date of an allocation at 100% does not trigger an "Allocation Exceeded" error.
  * **Date Logic**: Verify that an allocation's `start_date` is less than or equal to its `end_date`.
  * **Allocation Range**: Ensure that `0 < allocation_percent <= 100`. Strictly reject values <= 0 or > 100 using API validations.
* **Security & Exception Rules**:
  * Verify that both `employee_id` and `project_id` actually exist before attempting to save an allocation. If not found, throw `EmployeeNotFoundException` or `ProjectNotFoundException` (resulting in `404 Not Found`).
  * If the total allocation exceeds 100%, throw `AllocationExceededException` resulting in a `400 Bad Request` with payload `{"message": "Employee allocation exceeds 100%"}`.
* **Performance Gotchas**:
  * Mark `@ManyToOne` relations in the `Allocation` entity (e.g., `employee`, `project`) as `FetchType.LAZY`. By default, Hibernate uses `EAGER` for single-ended associations, causing massive overhead.

---

## Usage Guidelines

**For AI Agents:**

- Read this file before implementing any code.
- Follow ALL rules exactly as documented.
- When in doubt, prefer the more restrictive option.
- Update this file if new patterns emerge.

**For Humans:**

- Keep this file lean and focused on agent needs.
- Update when technology stack changes.
- Review quarterly for outdated rules.
- Remove rules that become obvious over time.

Last Updated: 2026-07-15
