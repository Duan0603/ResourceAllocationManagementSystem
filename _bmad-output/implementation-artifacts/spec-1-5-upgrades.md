---
title: 'Resource Allocation Management System v1.5 Upgrades'
type: 'feature'
created: '2026-07-16'
status: 'done'
baseline_commit: 'NO_VCS'
review_loop_iteration: 0
context: ['D:/OJT/week1/_bmad-output/project-context.md']
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The system lacks core features required for version 1.5: Skill Management (managing employee skills), Resource Search (searching employees by skill), Allocation Status Workflow (managing pending, active, and ended states of allocations), and the correct naming conventions in the Available Capacity workload API.

**Approach:**
1. Extend database schema and JPA entities to support Skill entities with Many-to-Many employee association.
2. Implement APIs for adding and fetching employee skills, and searching employees by skill.
3. Update Allocation entity with a status column, and add endpoints to activate and end allocations, implementing status-transition logic.
4. Correct response JSON fields in the workload API.
5. Update UI dashboard with skills display, skill management, and allocation workflow controls.

## Boundaries & Constraints

**Always:**
- Default new allocations to `PENDING` status.
- Only allow `PENDING` allocations to be changed to `ACTIVE`.
- Strictly forbid `ENDED` allocations from being changed back to `ACTIVE`.
- Exclude `ENDED` allocations from the active workload total calculations.
- Maintain Java 17 features, such as records for new DTOs (e.g. `SkillRequest`, `ResourceSearchResponse`).
- Maintain constructor-based dependency injection in controllers and services.

**Ask First:**
- Adding extra tables beyond `skill` and `employee_skill`.

**Never:**
- Allow overallocation above 100% across PENDING and ACTIVE allocations.
- Exclude the current allocation ID from update-capacity checks.
- Modify existing test baseline assertions for completed projects.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Add Skills | POST /employees/1/skills with `["Java", "Spring"]` | HTTP 200: success, returns list of skills | Return HTTP 404 if employee not found |
| Search by Skill | GET /employees/search?skill=Java | HTTP 200: `[{"employeeName": "Nguyen Van A", "available": 40}]` | Return empty list if no matches |
| Activate Pending | PUT /allocations/1/activate (PENDING state) | HTTP 200: returns updated allocation in ACTIVE state | N/A |
| Activate Ended | PUT /allocations/1/activate (ENDED state) | HTTP 400 | Return HTTP 400 "Ended allocation cannot be activated" |
| Capacity Check | Employee has 60% ACTIVE and 50% PENDING allocation | Reject creation of 50% allocation (total would be 110%) | Throw AllocationExceededException, return HTTP 400 |

</frozen-after-approval>

## Code Map

- `src/main/resources/schema.sql` -- Database creation scripts, updated for skills and allocation status.
- `src/main/java/com/company/resourcealloc/model/Skill.java` -- JPA Entity mapping the `skill` table.
- `src/main/java/com/company/resourcealloc/model/AllocationStatus.java` -- Enum for allocation states (PENDING, ACTIVE, ENDED).
- `src/main/java/com/company/resourcealloc/model/Employee.java` -- Updated Employee entity with Many-to-Many mapping to Skill.
- `src/main/java/com/company/resourcealloc/model/Allocation.java` -- Updated Allocation entity with AllocationStatus.
- `src/main/java/com/company/resourcealloc/repository/SkillRepository.java` -- Repository interface for Skill entity.
- `src/main/java/com/company/resourcealloc/repository/EmployeeRepository.java` -- Updated to add query by skill.
- `src/main/java/com/company/resourcealloc/dto/EmployeeWorkloadResponse.java` -- Updated record properties (`totalAllocation` -> `allocated`).
- `src/main/java/com/company/resourcealloc/dto/ResourceSearchResponse.java` -- New response DTO for resource searching.
- `src/main/java/com/company/resourcealloc/dto/AllocationResponse.java` -- Updated to include status.
- `src/main/java/com/company/resourcealloc/service/EmployeeService.java` -- Updated for skills management.
- `src/main/java/com/company/resourcealloc/service/impl/EmployeeServiceImpl.java` -- Updated logic for skill mapping and correct workload.
- `src/main/java/com/company/resourcealloc/service/AllocationService.java` -- Updated for activation and end workflows.
- `src/main/java/com/company/resourcealloc/service/impl/AllocationServiceImpl.java` -- Updated logic for status transitions and workload checks.
- `src/main/java/com/company/resourcealloc/controller/EmployeeController.java` -- Updated with new skill endpoints.
- `src/main/java/com/company/resourcealloc/controller/AllocationController.java` -- Updated with activate and end endpoints.
- `src/main/resources/static/index.html` -- Updated UI dashboard displaying skills and status workflows.

## Tasks & Acceptance

**Execution:**
- [x] `src/main/resources/schema.sql` -- Add skill tables and allocation status column.
- [x] `src/main/java/com/company/resourcealloc/model/Skill.java` -- Create Skill entity.
- [x] `src/main/java/com/company/resourcealloc/model/AllocationStatus.java` -- Create AllocationStatus enum.
- [x] `src/main/java/com/company/resourcealloc/model/Employee.java` -- Map skills to Employee.
- [x] `src/main/java/com/company/resourcealloc/model/Allocation.java` -- Map status to Allocation.
- [x] `src/main/java/com/company/resourcealloc/repository/SkillRepository.java` -- Create SkillRepository interface.
- [x] `src/main/java/com/company/resourcealloc/repository/EmployeeRepository.java` -- Add findBySkillName query.
- [x] `src/main/java/com/company/resourcealloc/dto/EmployeeWorkloadResponse.java` -- Update record property name.
- [x] `src/main/java/com/company/resourcealloc/dto/ResourceSearchResponse.java` -- Create search result record.
- [x] `src/main/java/com/company/resourcealloc/dto/AllocationResponse.java` -- Add status field.
- [x] `src/main/java/com/company/resourcealloc/service/EmployeeService.java` -- Define skill operations.
- [x] `src/main/java/com/company/resourcealloc/service/impl/EmployeeServiceImpl.java` -- Implement skill operations and update workload calculations.
- [x] `src/main/java/com/company/resourcealloc/service/AllocationService.java` -- Define status workflow.
- [x] `src/main/java/com/company/resourcealloc/service/impl/AllocationServiceImpl.java` -- Implement status transitions and validate workload.
- [x] `src/main/java/com/company/resourcealloc/controller/EmployeeController.java` -- Expose skill and search endpoints.
- [x] `src/main/java/com/company/resourcealloc/controller/AllocationController.java` -- Expose status endpoints.
- [x] `src/main/resources/static/index.html` -- Add skills visual interface and status transitions buttons.
- [x] `src/test/java/com/company/resourcealloc/service/impl/AllocationServiceImplTest.java` -- Add tests for status transitions and workload logic.

**Acceptance Criteria:**
- Given a running database, when calling `GET /employees/search?skill=Java`, then the matching list of employees is returned with their available capacity.
- Given a PENDING allocation, when calling `PUT /allocations/{id}/activate`, then it transitions to ACTIVE.
- Given an ENDED allocation, when calling `PUT /allocations/{id}/activate`, then a Bad Request error is returned.
- Given an employee workload query, when requesting `GET /employees/{id}/workload`, then the JSON body contains `allocated` and `available`.

## Verification

**Commands:**
- `mvn clean package` -- Verify compilation and all tests pass.
- `mvn test` -- Run unit and workflow tests.
