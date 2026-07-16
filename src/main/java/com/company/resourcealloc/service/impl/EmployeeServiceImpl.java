package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.dto.EmployeeRequest;
import com.company.resourcealloc.dto.EmployeeWorkloadResponse;
import com.company.resourcealloc.dto.ResourceSearchResponse;
import com.company.resourcealloc.exception.EmployeeNotFoundException;
import com.company.resourcealloc.model.Allocation;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.model.Skill;
import com.company.resourcealloc.repository.AllocationRepository;
import com.company.resourcealloc.repository.EmployeeRepository;
import com.company.resourcealloc.repository.SkillRepository;
import com.company.resourcealloc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AllocationRepository allocationRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public Employee createEmployee(EmployeeRequest request) {
        if (employeeRepository.findByEmployeeCode(request.employeeCode()).isPresent()) {
            throw new IllegalArgumentException("Employee code already exists: " + request.employeeCode());
        }
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }
        Employee employee = Employee.builder()
                .employeeCode(request.employeeCode())
                .fullName(request.fullName())
                .email(request.email())
                .role(request.role())
                .department(request.department())
                .build();
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllWithSkills();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    @Override
    public EmployeeWorkloadResponse getEmployeeWorkload(Long id) {
        Employee employee = getEmployeeById(id);
        List<Allocation> allocations = allocationRepository.findByEmployeeEmployeeId(id);
        
        int totalAllocation = allocations.stream()
                .filter(a -> a.getStatus() != com.company.resourcealloc.model.AllocationStatus.ENDED)
                .mapToInt(Allocation::getAllocationPercent)
                .sum();
        
        int available = Math.max(0, 100 - totalAllocation);
        
        return new EmployeeWorkloadResponse(
                employee.getEmployeeId(),
                employee.getFullName(),
                totalAllocation,
                available
        );
    }

    @Override
    @Transactional
    public void addSkillsToEmployee(Long employeeId, List<String> skillNames) {
        Employee employee = getEmployeeById(employeeId);
        java.util.Set<Skill> employeeSkills = employee.getSkills();
        for (String name : skillNames) {
            if (name == null || name.trim().isEmpty()) continue;
            String trimmedName = name.trim();
            Skill skill = skillRepository.findByNameIgnoreCase(trimmedName)
                    .orElseGet(() -> skillRepository.save(Skill.builder().name(trimmedName).build()));
            employeeSkills.add(skill);
        }
        employeeRepository.save(employee);
    }

    @Override
    public List<Skill> getEmployeeSkills(Long employeeId) {
        Employee employee = getEmployeeById(employeeId);
        employee.getSkills().size(); // Trigger lazy load
        return new java.util.ArrayList<>(employee.getSkills());
    }

    @Override
    public List<ResourceSearchResponse> searchEmployeesBySkill(String skillName) {
        List<Employee> matchingEmployees = employeeRepository.findBySkillName(skillName);
        return matchingEmployees.stream()
                .map(emp -> {
                    EmployeeWorkloadResponse workload = getEmployeeWorkload(emp.getEmployeeId());
                    return new ResourceSearchResponse(emp.getFullName(), workload.available());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = getEmployeeById(id);
        
        if (!employee.getEmployeeCode().equalsIgnoreCase(request.employeeCode())) {
            if (employeeRepository.findByEmployeeCode(request.employeeCode()).isPresent()) {
                throw new IllegalArgumentException("Employee code already exists: " + request.employeeCode());
            }
        }
        if (!employee.getEmail().equalsIgnoreCase(request.email())) {
            if (employeeRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + request.email());
            }
        }

        employee.setEmployeeCode(request.employeeCode());
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setRole(request.role());
        employee.setDepartment(request.department());
        
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}
