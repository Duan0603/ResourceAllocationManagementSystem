package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.dto.AllocationRequest;
import com.company.resourcealloc.dto.AllocationResponse;
import com.company.resourcealloc.exception.AllocationExceededException;
import com.company.resourcealloc.exception.EmployeeNotFoundException;
import com.company.resourcealloc.exception.ProjectNotFoundException;
import com.company.resourcealloc.model.Allocation;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.model.Project;
import com.company.resourcealloc.model.ProjectStatus;
import com.company.resourcealloc.repository.AllocationRepository;
import com.company.resourcealloc.repository.EmployeeRepository;
import com.company.resourcealloc.repository.ProjectRepository;
import com.company.resourcealloc.service.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public AllocationResponse createAllocation(AllocationRequest request) {
        log.info("Creating resource allocation for employee: {}, project: {}", request.employeeId(), request.projectId());
        
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + request.employeeId()));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + request.projectId()));

        // Business Rule 3: Cannot allocate to a COMPLETED project
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot allocate employee to a COMPLETED project");
        }

        // Business Rule 2: Total allocation must not exceed 100%
        validateTotalAllocation(employee.getEmployeeId(), null, request.allocationPercent());

        Allocation allocation = Allocation.builder()
                .employee(employee)
                .project(project)
                .allocationPercent(request.allocationPercent())
                .roleInProject(request.roleInProject())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(com.company.resourcealloc.model.AllocationStatus.PENDING)
                .build();

        Allocation saved = allocationRepository.save(allocation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public AllocationResponse updateAllocation(Long id, AllocationRequest request) {
        log.info("Updating resource allocation id: {}", id);
        
        Allocation existing = allocationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found with id: " + id));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + request.employeeId()));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + request.projectId()));

        // Business Rule 3: Cannot allocate to a COMPLETED project
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot allocate employee to a COMPLETED project");
        }

        // Business Rule 2: Total allocation must not exceed 100% (excluding the current allocation being updated)
        validateTotalAllocation(employee.getEmployeeId(), id, request.allocationPercent());

        existing.setEmployee(employee);
        existing.setProject(project);
        existing.setAllocationPercent(request.allocationPercent());
        existing.setRoleInProject(request.roleInProject());
        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());

        Allocation updated = allocationRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAllocation(Long id) {
        log.info("Removing resource allocation id: {}", id);
        if (!allocationRepository.existsById(id)) {
            throw new IllegalArgumentException("Allocation not found with id: " + id);
        }
        allocationRepository.deleteById(id);
    }

    @Override
    public List<AllocationResponse> getAllAllocations() {
        return allocationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AllocationResponse activateAllocation(Long id) {
        log.info("Activating resource allocation id: {}", id);
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found with id: " + id));

        if (allocation.getStatus() == com.company.resourcealloc.model.AllocationStatus.ENDED) {
            throw new IllegalArgumentException("Ended allocation cannot be activated");
        }
        if (allocation.getStatus() != com.company.resourcealloc.model.AllocationStatus.PENDING) {
            throw new IllegalArgumentException("Only allocation with status PENDING can be activated");
        }

        allocation.setStatus(com.company.resourcealloc.model.AllocationStatus.ACTIVE);
        Allocation saved = allocationRepository.save(allocation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public AllocationResponse endAllocation(Long id) {
        log.info("Ending resource allocation id: {}", id);
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found with id: " + id));

        allocation.setStatus(com.company.resourcealloc.model.AllocationStatus.ENDED);
        Allocation saved = allocationRepository.save(allocation);
        return mapToResponse(saved);
    }

    private void validateTotalAllocation(Long employeeId, Long excludeAllocationId, Integer newPercent) {
        List<Allocation> existing = allocationRepository.findByEmployeeEmployeeId(employeeId);
        
        int currentTotal = existing.stream()
                .filter(a -> excludeAllocationId == null || !Objects.equals(a.getAllocationId(), excludeAllocationId))
                .filter(a -> a.getStatus() != com.company.resourcealloc.model.AllocationStatus.ENDED)
                .mapToInt(Allocation::getAllocationPercent)
                .sum();
        
        if (currentTotal + newPercent > 100) {
            throw new AllocationExceededException("Employee allocation exceeds 100%");
        }
    }

    private AllocationResponse mapToResponse(Allocation allocation) {
        return new AllocationResponse(
                allocation.getAllocationId(),
                allocation.getEmployee().getEmployeeId(),
                allocation.getEmployee().getFullName(),
                allocation.getEmployee().getEmployeeCode(),
                allocation.getProject().getProjectId(),
                allocation.getProject().getProjectName(),
                allocation.getProject().getProjectCode(),
                allocation.getAllocationPercent(),
                allocation.getRoleInProject(),
                allocation.getStartDate(),
                allocation.getEndDate(),
                allocation.getStatus()
        );
    }
}
