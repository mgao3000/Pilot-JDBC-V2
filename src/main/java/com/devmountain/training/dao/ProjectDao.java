package com.devmountain.training.dao;

import com.devmountain.training.model.ProjectModel;

import java.util.List;

public interface ProjectDao {
    ProjectModel save(ProjectModel project);
    ProjectModel update(ProjectModel project);
    boolean deleteByName(String projectName);
    boolean deleteById(Long projectId);
    boolean delete(ProjectModel project);
    List<ProjectModel> getProjects();
    ProjectModel getProjectById(Long id);
    ProjectModel getProjectByName(String projectName);
    List<ProjectModel> getProjectsWithAssociatedStudents();
    ProjectModel getProjectWithAssociatedStudentsById(Long projectId);
    ProjectModel getProjectWithAssociatedStudentsByName(String projectName);
}
