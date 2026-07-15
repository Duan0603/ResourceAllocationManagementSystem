package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.dto.EmployeeRequest;
import com.company.resourcealloc.dto.EmployeeWorkloadResponse;
import com.company.resourcealloc.exception.EmployeeNotFoundException;
import com.company.resourcealloc.model.Allocation;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.repository.AllocationRepository;
import com.company.resourcealloc.repository.EmployeeRepository;
import com.company.resourcealloc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AllocationRepository allocationRepository;

    @Override
    @Transactional
    public Employee createEmployee(EmployeeRequest request) {
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
        return employeeRepository.findAll();
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
    public Employee updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = getEmployeeById(id);
        
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
