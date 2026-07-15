package com.company.resourcealloc.repository;

import com.company.resourcealloc.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByEmployeeEmployeeId(Long employeeId);
}
