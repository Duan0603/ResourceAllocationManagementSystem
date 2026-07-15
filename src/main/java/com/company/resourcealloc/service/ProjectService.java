package com.company.resourcealloc.service;

import com.company.resourcealloc.dto.ProjectRequest;
import com.company.resourcealloc.model.Project;

import java.util.List;

public interface ProjectService {
    Project createProject(ProjectRequest request);
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    Project updateProject(Long id, ProjectRequest request);
    void deleteProject(Long id);
}
