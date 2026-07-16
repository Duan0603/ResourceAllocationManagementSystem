package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.dto.AllocationRequest;
import com.company.resourcealloc.dto.AllocationResponse;
import com.company.resourcealloc.exception.AllocationExceededException;
import com.company.resourcealloc.model.Allocation;
import com.company.resourcealloc.model.AllocationStatus;
import com.company.resourcealloc.model.Employee;
import com.company.resourcealloc.model.Project;
import com.company.resourcealloc.model.ProjectStatus;
import com.company.resourcealloc.repository.AllocationRepository;
import com.company.resourcealloc.repository.EmployeeRepository;
import com.company.resourcealloc.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceImplTest {

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private AllocationServiceImpl allocationService;

    private Employee employee;
    private Project activeProject;
    private Project completedProject;
    private AllocationRequest request;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId(1L)
                .employeeCode("EMP001")
                .fullName("Tuan Ho Anh")
                .email("tuanha@company.com")
                .build();

        activeProject = Project.builder()
                .projectId(2L)
                .projectCode("NCG")
                .projectName("Next Gen Core")
                .status(ProjectStatus.ACTIVE)
                .build();

        completedProject = Project.builder()
                .projectId(3L)
                .projectCode("OLD")
                .projectName("Finished Project")
                .status(ProjectStatus.COMPLETED)
                .build();

        request = new AllocationRequest(
                1L,
                2L,
                60,
                "Backend Developer",
                LocalDate.now(),
                LocalDate.now().plusMonths(3)
        );
    }

    @Test
    void givenValidRequest_whenCreateAllocation_thenSucceed() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(activeProject));
        when(allocationRepository.findByEmployeeEmployeeId(1L)).thenReturn(Collections.emptyList());

        Allocation savedAllocation = Allocation.builder()
                .allocationId(10L)
                .employee(employee)
                .project(activeProject)
                .allocationPercent(60)
                .roleInProject("Backend Developer")
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
        when(allocationRepository.save(any(Allocation.class))).thenReturn(savedAllocation);

        // Act
        AllocationResponse response = allocationService.createAllocation(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.allocationId()).isEqualTo(10L);
        assertThat(response.allocationPercent()).isEqualTo(60);
        verify(allocationRepository, times(1)).save(any(Allocation.class));
    }

    @Test
    void givenCompletedProject_whenCreateAllocation_thenThrowIllegalArgumentException() {
        // Arrange
        AllocationRequest invalidRequest = new AllocationRequest(
                1L, 3L, 40, "Dev", LocalDate.now(), LocalDate.now().plusMonths(1)
        );
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(3L)).thenReturn(Optional.of(completedProject));

        // Act & Assert
        assertThatThrownBy(() -> allocationService.createAllocation(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot allocate employee to a COMPLETED project");

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void givenOverallocation_whenCreateAllocation_thenThrowAllocationExceededException() {
        // Arrange
        // Existing allocation is 50%
        Allocation existingAlloc = Allocation.builder()
                .allocationPercent(50)
                .status(AllocationStatus.PENDING)
                .build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(activeProject));
        when(allocationRepository.findByEmployeeEmployeeId(1L)).thenReturn(List.of(existingAlloc));

        // Requesting 60% (total = 110%)
        // Act & Assert
        assertThatThrownBy(() -> allocationService.createAllocation(request))
                .isInstanceOf(AllocationExceededException.class)
                .hasMessageContaining("Employee allocation exceeds 100%");

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void givenPendingAllocation_whenActivateAllocation_thenSucceed() {
        // Arrange
        Allocation pendingAlloc = Allocation.builder()
                .allocationId(10L)
                .employee(employee)
                .project(activeProject)
                .allocationPercent(50)
                .status(AllocationStatus.PENDING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        when(allocationRepository.findById(10L)).thenReturn(Optional.of(pendingAlloc));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AllocationResponse response = allocationService.activateAllocation(10L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(AllocationStatus.ACTIVE);
        verify(allocationRepository, times(1)).save(pendingAlloc);
    }

    @Test
    void givenEndedAllocation_whenActivateAllocation_thenThrowIllegalArgumentException() {
        // Arrange
        Allocation endedAlloc = Allocation.builder()
                .allocationId(10L)
                .status(AllocationStatus.ENDED)
                .build();
        when(allocationRepository.findById(10L)).thenReturn(Optional.of(endedAlloc));

        // Act & Assert
        assertThatThrownBy(() -> allocationService.activateAllocation(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ended allocation cannot be activated");

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void givenActiveAllocation_whenEndAllocation_thenSucceed() {
        // Arrange
        Allocation activeAlloc = Allocation.builder()
                .allocationId(10L)
                .employee(employee)
                .project(activeProject)
                .allocationPercent(50)
                .status(AllocationStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();
        when(allocationRepository.findById(10L)).thenReturn(Optional.of(activeAlloc));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AllocationResponse response = allocationService.endAllocation(10L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(AllocationStatus.ENDED);
        verify(allocationRepository, times(1)).save(activeAlloc);
    }
}
