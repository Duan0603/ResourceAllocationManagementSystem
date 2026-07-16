package com.company.resourcealloc.repository;

import com.company.resourcealloc.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findByEmail(String email);

    @Query("SELECT DISTINCT e FROM Employee e JOIN e.skills s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skillName, '%')) ORDER BY e.employeeId ASC")
    List<Employee> findBySkillName(@Param("skillName") String skillName);

    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.skills ORDER BY e.employeeId ASC")
    List<Employee> findAllWithSkills();
}
