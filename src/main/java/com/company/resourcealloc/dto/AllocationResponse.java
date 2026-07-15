package com.company.resourcealloc.dto;

import java.time.LocalDate;

public record AllocationResponse(
    Long allocationId,
    Long employeeId,
    String employeeName,
    String employeeCode,
    Long projectId,
    String projectName,
    String projectCode,
    Integer allocationPercent,
    String roleInProject,
    LocalDate startDate,
    LocalDate endDate
) {}
