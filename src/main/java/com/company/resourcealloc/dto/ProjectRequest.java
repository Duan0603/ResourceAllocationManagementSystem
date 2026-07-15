package com.company.resourcealloc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
    @NotBlank(message = "Project code is required")
    @Size(max = 20, message = "Project code must not exceed 20 characters")
    String projectCode,

    @NotBlank(message = "Project name is required")
    @Size(max = 200, message = "Project name must not exceed 200 characters")
    String projectName,

    @NotBlank(message = "Customer is required")
    @Size(max = 100, message = "Customer must not exceed 100 characters")
    String customer,

    @NotBlank(message = "Project status is required (PLANNING, ACTIVE, COMPLETED)")
    String status
) {}
