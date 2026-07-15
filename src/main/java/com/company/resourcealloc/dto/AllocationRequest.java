package com.company.resourcealloc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AllocationRequest(
    @NotNull(message = "Employee ID is required")
    Long employeeId,

    @NotNull(message = "Project ID is required")
    Long projectId,

    @NotNull(message = "Allocation percent is required")
    @Min(value = 1, message = "Allocation percent must be greater than 0")
    @Max(value = 100, message = "Allocation percent must not exceed 100")
    Integer allocationPercent,

    @Size(max = 100, message = "Role in project must not exceed 100 characters")
    String roleInProject,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate
) {}
