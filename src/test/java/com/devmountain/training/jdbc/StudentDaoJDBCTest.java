package com.devmountain.training.jdbc;

import com.devmountain.training.dao.MajorDao;
import com.devmountain.training.dao.StudentDao;
import com.devmountain.training.model.MajorModel;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StudentDaoJDBCTest extends AbstractDaoJDBCTest {
    private Logger logger = LoggerFactory.getLogger(StudentDaoJDBCTest.class);

//    private static StudentDao studentDao;
//    private static MajorDao majorDao;

//    private String tempLoginName = null;

    @BeforeClass
    public static void setupOnce() {
        studentDao = new StudentDaoJDBCImpl();
        majorDao = new MajorDaoJDBCImpl();
    }

    @AfterClass
    public static void teardownOnce() {
        studentDao = null;
        majorDao = null;
    }

    @Before
    public void setup() {
        tempLoginName = "Student-login-" + getRandomInt(1, 1000);
        randomEmail = "test" + getRandomInt(1, 1000) + "@devmountain.com";
    }

    @After
    public void teardown() {

    }

    @Test
    public void getStudentsTest() {
        List<StudentModel> studentList = studentDao.getStudents();
        int i = 1;
        for(StudentModel student : studentList) {
            logger.info("No.{} student = {}", i, student);
            i++;
        }
    }

    @Test
    public void getStudentsByMajorIdTest() {
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        List<StudentModel> studentList = studentDao.getStudentsByMajorId(randomMajorModel.getId());
        int i = 1;
        for(StudentModel student : studentList) {
            logger.info("No.{} student = {}", i, student);
            i++;
        }
    }

    @Test
    public void getStudentByIdTest() {
        /*
         * Pick up a random StudentModel from DB
         */
        StudentModel randomStudent = getRandomStudentModel();
        if(randomStudent == null) {
            logger.error("there is no student being found in database, please double check DB connection!");
        } else {
            Long studentId = randomStudent.getId();
            StudentModel retrievedStudentModel = studentDao.getStudentById(studentId);
            assertStudentModels(randomStudent, retrievedStudentModel);
        }
    }

    @Test
    public void getStudentByLoginNameTest() {
        /*
         * Pick up a random StudentModel from DB
         */
        StudentModel randomStudent = getRandomStudentModel();
        if(randomStudent == null) {
            logger.error("there is no Student being found in database, please double check DB connection!");
        } else {
            String loginName = randomStudent.getLoginName();
            StudentModel retrievedStudentModel = studentDao.getStudentByLoginName(loginName);
            assertStudentModels(randomStudent, retrievedStudentModel);
        }
    }

    @Test
    public void saveStudentTest() {
        StudentModel student = createStudentByLoginNameAndEmail(tempLoginName, randomEmail);
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        StudentModel savedStudent = studentDao.save(student, randomMajorModel.getId());
        student.setMajorId(randomMajorModel.getId());
        assertStudentModels(student, savedStudent);
        /*
         * Now clean up the saved Student from DB Major table
         */
        boolean deleteSuccessfulFlag = studentDao.delete(savedStudent);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteStudentTest() {
        /*
         * create a temp Student to test deletion
         */
        StudentModel student = createStudentByLoginNameAndEmail(tempLoginName, randomEmail);
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        StudentModel savedStudent = studentDao.save(student, randomMajorModel.getId());
        /*
         * Now delete the saved Project from DB Major table
         */
        boolean deleteSuccessfulFlag = studentDao.delete(savedStudent);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteStudentByIdTest() {
        /*
         * create a temp Student to test deletion
         */
        StudentModel student = createStudentByLoginNameAndEmail(tempLoginName, randomEmail);
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        StudentModel savedStudent = studentDao.save(student, randomMajorModel.getId());
        /*
         * Now delete the saved Student using student ID from DB Major table
         */
        boolean deleteSuccessfulFlag = studentDao.deleteById(savedStudent.getId());
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteStudentByLoginNameTest() {
        /*
         * create a temp Student to test deletion
         */
        StudentModel student = createStudentByLoginNameAndEmail(tempLoginName, randomEmail);
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        StudentModel savedStudent = studentDao.save(student, randomMajorModel.getId());
        /*
         * Now delete the saved Student using student ID from DB Major table
         */
        boolean deleteSuccessfulFlag = studentDao.deleteByLoginName(savedStudent.getLoginName());
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void updateStudentTest() {
        StudentModel originalStudentModel = getRandomStudentModel();

        String originalStudentAddress = originalStudentModel.getAddress();
        String modifiedStudentAddress = originalStudentAddress + "---Modified Address";
        originalStudentModel.setAddress(modifiedStudentAddress);
        /*
         * Now start doing update operation
         */
        StudentModel updatedStudentModel = studentDao.update(originalStudentModel);
        assertStudentModels(originalStudentModel, updatedStudentModel);

        /*
         * now reset StudentModel address back to the original value
         */
        originalStudentModel.setAddress(originalStudentAddress);
        updatedStudentModel = studentDao.update(originalStudentModel);
        assertStudentModels(originalStudentModel, updatedStudentModel);
    }

    @Test
    public void getStudentsWithAssociatedProjectsTest() {
        List<StudentModel> studentList = studentDao.getStudentsWithAssociatedProjects();
        displayStudents(studentList);
    }

    @Test
    public void getStudentsWithAssociatedProjectsByMajorIdTest() {
        /*
         * Now need to use majorDao to randomly select a valid Major
         */
        MajorModel randomMajorModel = getRandomMajorModel();

        List<StudentModel> studentList = studentDao.getStudentsWithAssociatedProjectsByMajorId(randomMajorModel.getId());
        displayStudents(studentList);
    }

    @Test
    public void getStudentWithAssociatedProjectsByStudentIdTest() {
        /*
         * Pick up a random StudentModel from DB
         */
        StudentModel randomStudent = getRandomStudentModel();
        if(randomStudent == null) {
            logger.error("there is no student being found in database, please double check DB connection!");
        } else {
            Long studentId = randomStudent.getId();
            StudentModel retrievedStudentModel = studentDao.getStudentWithAssociatedProjectsByStudentId(studentId);
            assertStudentModels(randomStudent, retrievedStudentModel);
            displayStudent(retrievedStudentModel);
        }
    }

    @Test
    public void getStudentWithAssociatedProjectsByLoginNameTest() {
        /*
         * Pick up a random StudentModel from DB
         */
        StudentModel randomStudent = getRandomStudentModel();
        if(randomStudent == null) {
            logger.error("there is no Student being found in database, please double check DB connection!");
        } else {
            String loginName = randomStudent.getLoginName();
            StudentModel retrievedStudentModel = studentDao.getStudentWithAssociatedProjectsByLoginName(loginName);
            assertStudentModels(randomStudent, retrievedStudentModel);
            displayStudent(retrievedStudentModel);
        }
    }

//    private void displayStudents(List<StudentModel> studentList) {
//        logger.info("The total number of Students is: {}", studentList.size());
//        int index = 1;
//        for(StudentModel student : studentList) {
//            logger.info("No.{} Student:", index);
//            displayStudent(student);
//            index++;
//        }
//    }

//    private void displayStudent(StudentModel student) {
//        logger.info("Student detail={}", student);
//        displayProjectList(student.getProjectList());
//    }
//
//    private void displayProjectList(List<ProjectModel> projectList) {
//        logger.info("\t The total associated projects={}", projectList.size());
//        int index = 1;
//        for (ProjectModel project : projectList) {
//            logger.info("No.{} project = {}", index, project);
//            index++;
//        }
//    }

//    private void assertStudentModels(StudentModel randomStudentModel, StudentModel retrievedStudentModel) {
//        assertEquals(randomStudentModel.getId(), retrievedStudentModel.getId());
//        assertEquals(randomStudentModel.getMajorId(), retrievedStudentModel.getMajorId());
//        assertEquals(randomStudentModel.getLoginName(), retrievedStudentModel.getLoginName());
//        assertEquals(randomStudentModel.getPassword(), retrievedStudentModel.getPassword());
//        assertEquals(randomStudentModel.getFirstName(), retrievedStudentModel.getFirstName());
//        assertEquals(randomStudentModel.getLastName(), retrievedStudentModel.getLastName());
//        assertEquals(randomStudentModel.getEmail(), retrievedStudentModel.getEmail());
//        assertEquals(randomStudentModel.getAddress(), retrievedStudentModel.getAddress());
//        assertTrue(randomStudentModel.getEnrolledDate().isEqual(retrievedStudentModel.getEnrolledDate()));
//    }
//
//    private StudentModel createStudentByLoginName(String loginName) {
//        StudentModel student = new StudentModel();
//        student.setLoginName(loginName);
//        student.setPassword("password123456");
//        student.setFirstName("Frist name test");
//        student.setLastName("Last name test");
//        student.setEmail("test@google.com");
//        student.setAddress("123 Dodge Road, Reston, VA 20220");
//        student.setEnrolledDate(LocalDate.now());
//
//        /*
//         * Now using MajorDao to select a valid Major from DB
//         */
//        MajorModel randomMajor = getRandomMajorModel();
//        student.setMajorId(randomMajor.getId());
//
//        return student;
//    }

//    private MajorModel getRandomMajorModel() {
//        List<MajorModel> majorModelList = majorDao.getMajors();
//        MajorModel randomMajor = null;
//        if(majorModelList != null && majorModelList.size() > 0) {
//            int randomIndex = getRandomInt(0, majorModelList.size());
//            randomMajor = majorModelList.get(randomIndex);
//        }
//        return randomMajor;
//    }

//    private StudentModel getRandomStudentModel() {
//        List<StudentModel> studentModelList = studentDao.getStudents();
//        StudentModel randomStudent = null;
//        if(studentModelList != null && studentModelList.size() > 0) {
//            int randomIndex = getRandomInt(0, studentModelList.size());
//            randomStudent = studentModelList.get(randomIndex);
//        }
//        return randomStudent;
//    }

    /**
     * This helper method return a random int value in a range between
     * min (inclusive) and max (exclusive)
     * @param min
     * @param max
     * @return
     */
//    private int getRandomInt(int min, int max) {
//        Random random = new Random();
//        return random.nextInt(max - min) + min;
//    }

}
