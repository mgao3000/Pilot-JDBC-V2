package com.devmountain.training.dao;

import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;

import java.util.List;

public interface StudentDao {
    StudentModel save(StudentModel student, Long majorId);
    StudentModel update(StudentModel student);
    boolean deleteByLoginName(String loginName);
    boolean deleteById(Long studentId);
    boolean delete(StudentModel student);
    List<StudentModel> getStudents();
    List<StudentModel> getStudentsByMajorId(Long majorId);
    StudentModel getStudentById(Long id);
    StudentModel getStudentByLoginName(String loginName);

    List<StudentModel> getStudentsWithAssociatedProjects();
    List<StudentModel> getStudentsWithAssociatedProjectsByMajorId(Long majorId);
    StudentModel getStudentWithAssociatedProjectsByStudentId(Long studentId);
    StudentModel getStudentWithAssociatedProjectsByLoginName(String loginName);
}
