package com.company.resourcealloc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @NotBlank(message = "Project code is required")
    @Size(max = 20, message = "Project code must not exceed 20 characters")
    @Column(name = "project_code", unique = true, nullable = false, length = 20)
    private String projectCode;

    @NotBlank(message = "Project name is required")
    @Size(max = 200, message = "Project name must not exceed 200 characters")
    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName;

    @NotBlank(message = "Customer is required")
    @Size(max = 100, message = "Customer must not exceed 100 characters")
    @Column(name = "customer", nullable = false, length = 100)
    private String customer;

    @NotNull(message = "Project status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectStatus status;
}
