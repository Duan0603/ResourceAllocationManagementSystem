package com.company.resourcealloc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    String employeeCode,

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    @Size(max = 50, message = "Role must not exceed 50 characters")
    String role,

    @Size(max = 50, message = "Department must not exceed 50 characters")
    String department
) {}
