package com.devmountain.training.jdbc;

import com.devmountain.training.dao.*;
import com.devmountain.training.model.MajorModel;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractDaoJDBCTest {
    private Logger logger = LoggerFactory.getLogger(AbstractDaoJDBCTest.class);

    protected static MajorDao majorDao;

    protected static StudentDao studentDao;

    protected static ProjectDao projectDao;

    protected static StudentProjectDao studentProjectDao;

    protected String tempMajorName;

    protected String tempProjectName;

    protected String tempLoginName = null;

    protected String randomEmail = null;

    protected void assertMajorModels(MajorModel randomMajor, MajorModel retrievedMajorModel) {
        assertEquals(randomMajor.getId(), retrievedMajorModel.getId());
        assertEquals(randomMajor.getName(), retrievedMajorModel.getName());
        assertEquals(randomMajor.getDescription(), retrievedMajorModel.getDescription());
    }

    protected void assertProjectModels(ProjectModel randomProjectModel, ProjectModel retrievedProjectModel) {
        assertEquals(randomProjectModel.getId(), retrievedProjectModel.getId());
        assertEquals(randomProjectModel.getName(), retrievedProjectModel.getName());
        assertEquals(randomProjectModel.getDescription(), retrievedProjectModel.getDescription());
        assertTrue(randomProjectModel.getCreateDate().isEqual(retrievedProjectModel.getCreateDate()));
    }

    protected void assertStudentModels(StudentModel randomStudentModel, StudentModel retrievedStudentModel) {
        assertEquals(randomStudentModel.getId(), retrievedStudentModel.getId());
        assertEquals(randomStudentModel.getMajorId(), retrievedStudentModel.getMajorId());
        assertEquals(randomStudentModel.getLoginName(), retrievedStudentModel.getLoginName());
        assertEquals(randomStudentModel.getPassword(), retrievedStudentModel.getPassword());
        assertEquals(randomStudentModel.getFirstName(), retrievedStudentModel.getFirstName());
        assertEquals(randomStudentModel.getLastName(), retrievedStudentModel.getLastName());
        assertEquals(randomStudentModel.getEmail(), retrievedStudentModel.getEmail());
        assertEquals(randomStudentModel.getAddress(), retrievedStudentModel.getAddress());
        assertTrue(randomStudentModel.getEnrolledDate().isEqual(retrievedStudentModel.getEnrolledDate()));
    }

    protected ProjectModel createProjectByName(String projectName) {
        ProjectModel project = new ProjectModel();
        project.setName(projectName);
        project.setDescription(projectName + "--description");
        project.setCreateDate(LocalDate.now());
        return project;
    }

    protected MajorModel createMajorByName(String name) {
        MajorModel major = new MajorModel();
        major.setName(name);
        major.setDescription(name + "--description");
        return major;
    }

    protected StudentModel createStudentByLoginNameAndEmail(String loginName, String email) {
        StudentModel student = new StudentModel();
        student.setLoginName(loginName);
        student.setPassword("password123456");
        student.setFirstName("Frist name test");
        student.setLastName("Last name test");
        student.setEmail(email);
        student.setAddress("123 Dodge Road, Reston, VA 20220");
        student.setEnrolledDate(LocalDate.now());

        /*
         * Now using MajorDao to select a valid Major from DB
         */
        MajorModel randomMajor = getRandomMajorModel();
        student.setMajorId(randomMajor.getId());

        return student;
    }

    protected ProjectModel getRandomProjectModel() {
        List<ProjectModel> projectModelList = projectDao.getProjects();
        ProjectModel randomProject = null;
        if(projectModelList != null && projectModelList.size() > 0) {
            int randomIndex = getRandomInt(0, projectModelList.size());
            randomProject = projectModelList.get(randomIndex);
        }
        return randomProject;
    }

    protected MajorModel getRandomMajorModel() {
        List<MajorModel> majorModelList = majorDao.getMajors();
        MajorModel randomMajor = null;
        if(majorModelList != null && majorModelList.size() > 0) {
            int randomIndex = getRandomInt(0, majorModelList.size());
            randomMajor = majorModelList.get(randomIndex);
        }
        return randomMajor;
    }

    protected StudentModel getRandomStudentModel() {
        List<StudentModel> studentModelList = studentDao.getStudents();
        StudentModel randomStudent = null;
        if(studentModelList != null && studentModelList.size() > 0) {
            int randomIndex = getRandomInt(0, studentModelList.size());
            randomStudent = studentModelList.get(randomIndex);
        }
        return randomStudent;
    }

    protected void displayStudents(List<StudentModel> studentList) {
        logger.info("The total number of Students is: {}", studentList.size());
        int index = 1;
        for(StudentModel student : studentList) {
            logger.info("No.{} Student:", index);
            displayStudent(student);
            index++;
        }
    }

    protected void displayProjects(List<ProjectModel> projectList) {
        logger.info("The total number of Projects is: {}", projectList.size());
        int index = 1;
        for(ProjectModel eachProject : projectList) {
            logger.info("No.{} project:", index);
            displayProject(eachProject);
            index++;
        }
    }

    protected void displayProject(ProjectModel project) {
        logger.info("Project detail={}", project);
        displayStudentList(project.getStudentList());
    }

    protected void displayStudentList(List<StudentModel> studentList) {
        logger.info("\t The total associated students={}", studentList.size());
        int index = 1;
        for (StudentModel student : studentList) {
            logger.info("No.{} student = {}", index, student);
            index++;
        }
    }

    protected void displayMajors(List<MajorModel> majorList) {
        logger.info("The total number of Majors is: {}", majorList.size());
        int index = 1;
        for(MajorModel major : majorList) {
            logger.info("No.{} Major:", index);
            displayMajor(major);
            index++;
        }
    }

    protected void displayMajor(MajorModel major) {
        logger.info("Major detail={}", major);
        displayStudentList(major.getStudentList());
    }

//    protected void displayStudentList(List<StudentModel> studentList) {
//        logger.info("The total number of Students is: {}", studentList.size());
//        int index = 1;
//        for(StudentModel student : studentList) {
//            logger.info("No.{} Student:", index);
//            displayStudent(student);
//            index++;
//        }
//    }

    protected void displayStudent(StudentModel student) {
        logger.info("Student detail={}", student);
        displayProjectList(student.getProjectList());
    }

    protected void displayProjectList(List<ProjectModel> projectList) {
        logger.info("\t The total associated projects={}", projectList.size());
        int index = 1;
        for (ProjectModel project : projectList) {
            logger.info("No.{} project = {}", index, project);
            index++;
        }
    }


    /**
     * This helper method return a random int value in a range between
     * min (inclusive) and max (exclusive)
     * @param min
     * @param max
     * @return
     */
    public int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}
