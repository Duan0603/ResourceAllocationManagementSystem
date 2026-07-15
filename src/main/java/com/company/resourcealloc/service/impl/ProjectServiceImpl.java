package com.company.resourcealloc.service.impl;

import com.company.resourcealloc.dto.ProjectRequest;
import com.company.resourcealloc.exception.ProjectNotFoundException;
import com.company.resourcealloc.model.Project;
import com.company.resourcealloc.model.ProjectStatus;
import com.company.resourcealloc.repository.ProjectRepository;
import com.company.resourcealloc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public Project createProject(ProjectRequest request) {
        ProjectStatus status;
        try {
            status = ProjectStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.status() + ". Allowed: PLANNING, ACTIVE, COMPLETED");
        }

        Project project = Project.builder()
                .projectCode(request.projectCode())
                .projectName(request.projectName())
                .customer(request.customer())
                .status(status)
                .build();
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }

    @Override
    @Transactional
    public Project updateProject(Long id, ProjectRequest request) {
        Project project = getProjectById(id);

        ProjectStatus status;
        try {
            status = ProjectStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.status() + ". Allowed: PLANNING, ACTIVE, COMPLETED");
        }

        project.setProjectCode(request.projectCode());
        project.setProjectName(request.projectName());
        project.setCustomer(request.customer());
        project.setStatus(status);

        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
