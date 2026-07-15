package com.company.resourcealloc.dto;

public record EmployeeWorkloadResponse(
    Long employeeId,
    String employeeName,
    Integer totalAllocation,
    Integer available
) {}
