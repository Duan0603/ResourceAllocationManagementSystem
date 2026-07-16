package com.company.resourcealloc.controller;

import com.company.resourcealloc.dto.EmployeeRequest;
import com.company.resourcealloc.dto.EmployeeWorkloadResponse;
import com.company.resourcealloc.dto.ResourceSearchResponse;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.model.Skill;
import com.company.resourcealloc.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        Employee created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/{id}/workload")
    public ResponseEntity<EmployeeWorkloadResponse> getEmployeeWorkload(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeWorkload(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        Employee updated = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<Void> addSkillsToEmployee(@PathVariable Long id, @RequestBody List<String> skills) {
        employeeService.addSkillsToEmployee(id, skills);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<Skill>> getEmployeeSkills(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeSkills(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceSearchResponse>> searchEmployeesBySkill(@RequestParam String skill) {
        return ResponseEntity.ok(employeeService.searchEmployeesBySkill(skill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
