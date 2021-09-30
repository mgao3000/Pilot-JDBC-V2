package com.devmountain.training.dao;

import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;

public interface StudentProjectDao {
    boolean deleteStudentProjectRelationship(StudentModel student, ProjectModel project);
    boolean deleteStudentProjectRelationship(long studentId, long projectId);
    boolean addStudentProjectRelationship(StudentModel student, ProjectModel project);
    boolean addStudentProjectRelationship(long studentId, long projectId);
}
