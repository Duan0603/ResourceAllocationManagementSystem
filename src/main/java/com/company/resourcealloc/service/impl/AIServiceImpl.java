package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.model.Allocation;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.repository.AllocationRepository;
import com.company.resourcealloc.repository.EmployeeRepository;
import com.company.resourcealloc.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final EmployeeRepository employeeRepository;
    private final AllocationRepository allocationRepository;

    @Override
    public Map<String, Object> getRecommendations(String query) {
        log.info("Processing AI Resource Recommendation for query: {}", query);
        
        // 1. Extract target availability percentage from query (e.g., "50%", "50 available", "tối thiểu 50")
        int minAvailable = 0;
        Pattern pctPattern = Pattern.compile("(\\d+)\\s*(%|available|khả dụng|trống)");
        Matcher pctMatcher = pctPattern.matcher(query);
        if (pctMatcher.find()) {
            minAvailable = Integer.parseInt(pctMatcher.group(1));
        } else {
            // Try matching any isolated number in the query
            Pattern numPattern = Pattern.compile("(\\d+)");
            Matcher numMatcher = numPattern.matcher(query);
            if (numMatcher.find()) {
                minAvailable = Integer.parseInt(numMatcher.group(1));
            }
        }

        // 2. Extract potential role keywords (e.g., "Java", "Developer", "Tester", "PM")
        String targetRole = "";
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("java")) {
            targetRole = "java";
        } else if (lowerQuery.contains("tester") || lowerQuery.contains("test")) {
            targetRole = "tester";
        } else if (lowerQuery.contains("developer") || lowerQuery.contains("dev")) {
            targetRole = "developer";
        } else if (lowerQuery.contains("pm") || lowerQuery.contains("manager")) {
            targetRole = "pm";
        } else if (lowerQuery.contains("ba") || lowerQuery.contains("business analyst")) {
            targetRole = "ba";
        }

        // 3. Fetch all resources and calculate workload
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Map<String, Object>> recommendedList = new ArrayList<>();

        for (Employee emp : allEmployees) {
            String empRole = emp.getRole() != null ? emp.getRole().toLowerCase() : "";
            
            // Filter by role if a role is identified in the query
            if (!targetRole.isEmpty() && !empRole.contains(targetRole)) {
                continue;
            }

            // Calculate availability
            List<Allocation> allocations = allocationRepository.findByEmployeeEmployeeId(emp.getEmployeeId());
            int totalAllocated = allocations.stream().mapToInt(Allocation::getAllocationPercent).sum();
            int available = Math.max(0, 100 - totalAllocated);

            // Filter by availability
            if (available >= minAvailable) {
                Map<String, Object> rec = new HashMap<>();
                rec.put("employee", emp.getFullName());
                rec.put("role", emp.getRole());
                rec.put("available", available);
                recommendedList.add(rec);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("recommendedResources", recommendedList);
        return response;
    }

    @Override
    public Map<String, Object> detectRisks(String query) {
        log.info("Processing AI Risk Detection for query: {}", query);
        
        // Default target role to check
        String targetRole = "java"; 
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("tester") || lowerQuery.contains("test")) {
            targetRole = "tester";
        } else if (lowerQuery.contains("developer") || lowerQuery.contains("dev")) {
            targetRole = "developer";
        } else if (lowerQuery.contains("pm") || lowerQuery.contains("manager")) {
            targetRole = "pm";
        }

        // 1. Fetch matching team members
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> team = new ArrayList<>();
        for (Employee emp : allEmployees) {
            String role = emp.getRole() != null ? emp.getRole().toLowerCase() : "";
            if (role.contains(targetRole)) {
                team.add(emp);
            }
        }

        if (team.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("riskReport", "Risk:\n- Không tìm thấy nhân sự nào thuộc nhóm vai trò: " + targetRole.toUpperCase() + ".\n- Cần bổ sung nhân sự mới cho dự án.");
            return response;
        }

        // 2. Compute utilization statistics for the team
        int totalPossibleCapacity = team.size() * 100;
        int totalAllocatedCapacity = 0;
        int availableOver50Count = 0;

        for (Employee emp : team) {
            List<Allocation> allocations = allocationRepository.findByEmployeeEmployeeId(emp.getEmployeeId());
            int allocated = allocations.stream().mapToInt(Allocation::getAllocationPercent).sum();
            totalAllocatedCapacity += allocated;
            
            int available = 100 - allocated;
            if (available >= 50) {
                availableOver50Count++;
            }
        }

        int avgUtilizationPercent = (int) Math.round(((double) totalAllocatedCapacity / totalPossibleCapacity) * 100);

        // 3. Construct report text
        String report = String.format("Risk:\n- Team %s đang sử dụng %d%% capacity.\n- Chỉ còn %d resource available trên 50%%.", 
                targetRole.toUpperCase(), avgUtilizationPercent, availableOver50Count);

        Map<String, Object> response = new HashMap<>();
        response.put("riskReport", report);
        return response;
    }
}
