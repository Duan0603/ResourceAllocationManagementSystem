package com.company.resourcealloc.service;

import com.company.resourcealloc.dto.EmployeeRequest;
import com.company.resourcealloc.dto.EmployeeWorkloadResponse;
import com.company.resourcealloc.model.Employee;

import com.company.resourcealloc.dto.ResourceSearchResponse;
import com.company.resourcealloc.model.Skill;

import java.util.List;

public interface EmployeeService {
    Employee createEmployee(EmployeeRequest request);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    EmployeeWorkloadResponse getEmployeeWorkload(Long id);
    Employee updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    void addSkillsToEmployee(Long employeeId, List<String> skillNames);
    List<Skill> getEmployeeSkills(Long employeeId);
    List<ResourceSearchResponse> searchEmployeesBySkill(String skillName);
}
