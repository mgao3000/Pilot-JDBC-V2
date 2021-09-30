package com.devmountain.training.jdbc;

import com.devmountain.training.model.MajorModel;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

public class MajorDaoJDBCTest extends AbstractDaoJDBCTest {
    private Logger logger = LoggerFactory.getLogger(MajorDaoJDBCTest.class);

//    private static MajorDao majorDao;
//
//    private static StudentDao studentDao;
//
//    private static ProjectDao projectDao;
//
//    private static StudentProjectDao studentProjectDao;
//
//    private String tempMajorName = null;

    @BeforeClass
    public static void setupOnce() {
        majorDao = new MajorDaoJDBCImpl();
        studentDao = new StudentDaoJDBCImpl();
        projectDao = new ProjectDaoJDBCImpl();
        studentProjectDao = new StudentProjectDaoJDBCImpl();
    }

    @AfterClass
    public static void teardownOnce() {
        majorDao = null;
        studentDao = null;
        projectDao = null;
        studentProjectDao = null;
    }

    @Before
    public void setup() {
        tempMajorName = "Major-" + getRandomInt(1, 1000);
        tempLoginName = "Student-login-" + getRandomInt(1, 1000);
        tempProjectName = "Project-" + getRandomInt(1, 1000);
        randomEmail = "test-" + getRandomInt(1, 1000) + "@devmountain.com";
    }

    @After
    public void teardown() {

    }

    @Test
    public void getMajorsTest() {
        List<MajorModel> majorModelList = majorDao.getMajors();
        int i = 1;
        for(MajorModel major : majorModelList) {
            logger.info("No.{} Major = {}", i, major);
            i++;
        }
    }

    @Test
    public void getMajorByIdTest() {
        /*
         * Pick up a random MajorModel from DB
         */
        MajorModel randomMajor = getRandomMajorModel();
        if(randomMajor == null) {
            logger.error("there is no major being found in database, please double check DB connection!");
        } else {
            Long majorId = randomMajor.getId();
            MajorModel retrievedMajorModel = majorDao.getMajorById(majorId);
            assertMajorModels(randomMajor, retrievedMajorModel);
        }
    }

    @Test
    public void getMajorByNameTest() {
        /*
         * Pick up a random MajorModel from DB
         */
        MajorModel randomMajor = getRandomMajorModel();
        if(randomMajor == null) {
            logger.error("there is no major being found in database, please double check DB connection!");
        } else {
            String majorName = randomMajor.getName();
            MajorModel retrievedMajorModel = majorDao.getMajorByName(majorName);
            assertMajorModels(randomMajor, retrievedMajorModel);
        }
    }

    @Test
    public void getMajorsWithChildrenTest() {
        List<MajorModel> majorModelList = majorDao.getMajorsWithChildren();
        displayMajors(majorModelList);
    }

    @Test
    public void getMajorAndStudentsAndProjectsByMajorIdTest() {
        /*
         * Pick up a random MajorModel from DB
         */
        MajorModel randomMajor = getRandomMajorModel();
        if(randomMajor == null) {
            logger.error("there is no major being found in database, please double check DB connection!");
        } else {
            Long majorId = randomMajor.getId();
            MajorModel retrievedMajorModel = majorDao.getMajorAndStudentsAndProjectsByMajorId(majorId);
            assertMajorModels(randomMajor, retrievedMajorModel);
            displayMajor(retrievedMajorModel);
        }
    }

    @Test
    public void getMajorAndStudentsAndProjectsByMajorNameTest() {
        /*
         * Pick up a random MajorModel from DB
         */
        MajorModel randomMajor = getRandomMajorModel();
        if(randomMajor == null) {
            logger.error("there is no major being found in database, please double check DB connection!");
        } else {
            String majorName = randomMajor.getName();
            MajorModel retrievedMajorModel = majorDao.getMajorAndStudentsAndProjectsByMajorName(majorName);
            assertMajorModels(randomMajor, retrievedMajorModel);
            displayMajor(retrievedMajorModel);
        }
    }


    @Test
    public void saveMajorTest() {
        MajorModel major = createMajorByName(tempMajorName);
        MajorModel savedMajor = majorDao.save(major);
        assertEquals(major.getName(), savedMajor.getName());
        assertEquals(major.getDescription(), savedMajor.getDescription());
        /*
         * Now clean up the saved Major from DB Major table
         */
        boolean deleteSuccessfulFlag = majorDao.delete(savedMajor);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteMajorTest() {
        MajorModel major = createMajorByName(tempMajorName);
        MajorModel savedMajor = majorDao.save(major);
        /*
         * Now delete the saved Major from DB Major table
         */
        boolean deleteSuccessfulFlag = majorDao.delete(savedMajor);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteMajorWithChildrenTest() {
        //Step 0: create a temp Major
        MajorModel major = createMajorByName(tempMajorName);
        MajorModel majorSaved = majorDao.save(major);

        //Step 1.1: create a temp Student No.1
        StudentModel student1 = createStudentByLoginNameAndEmail(tempLoginName, randomEmail);
//        student1.setMajorId(majorSaved.getId());
        StudentModel studentSaved1 = studentDao.save(student1, majorSaved.getId());

        //Step 1.2  create a temp project No.1
        ProjectModel project1 = createProjectByName(tempProjectName);
        ProjectModel projectDSaved1 = projectDao.save(project1);

        //Step 1.3: create a temp project No.2
        ProjectModel project2 = createProjectByName(tempProjectName+"-2");
        ProjectModel projectDSaved2 = projectDao.save(project2);

        //Step 1.4: set relationship for student No.1 with project No.1 and No.2
        studentProjectDao.addStudentProjectRelationship(studentSaved1.getId(), projectDSaved1.getId());
        studentProjectDao.addStudentProjectRelationship(studentSaved1.getId(), projectDSaved2.getId());

        //Step 2.1: create a temp Student No.2
        StudentModel student2 = createStudentByLoginNameAndEmail(tempLoginName+"-2", 2 + randomEmail);
//        student2.setMajorId(majorSaved.getId());
        StudentModel studentSaved2 = studentDao.save(student2, majorSaved.getId());

        //Step 2.2  create a temp project No.3
        ProjectModel project3 = createProjectByName(tempProjectName+"-3");
        ProjectModel projectDSaved3 = projectDao.save(project3);

        //Step 2.3: create a temp project No.4
        ProjectModel project4 = createProjectByName(tempProjectName+"-4");
        ProjectModel projectDSaved4 = projectDao.save(project4);

        //Step 2.4: set relationship for student No.2 with project No.3 and No.4
        studentProjectDao.addStudentProjectRelationship(studentSaved2.getId(), projectDSaved3.getId());
        studentProjectDao.addStudentProjectRelationship(studentSaved2.getId(), projectDSaved4.getId());

        /*
         * Now delete the saved Major from DB Major table
         */
        boolean deleteSuccessfulFlag = majorDao.delete(majorSaved);
        assertEquals(true, deleteSuccessfulFlag);

        //Now delete those temporarily created projects using project ID
        deleteSuccessfulFlag = projectDao.delete(projectDSaved1);
        assertEquals(true, deleteSuccessfulFlag);

        deleteSuccessfulFlag = projectDao.delete(projectDSaved2);
        assertEquals(true, deleteSuccessfulFlag);

        deleteSuccessfulFlag = projectDao.delete(projectDSaved3);
        assertEquals(true, deleteSuccessfulFlag);

        deleteSuccessfulFlag = projectDao.delete(projectDSaved4);
        assertEquals(true, deleteSuccessfulFlag);

        //Now start to do multiple selection to make sure new created major, students are gone!
        MajorModel retrievedMajor = majorDao.getMajorById(majorSaved.getId());
        assertNull(retrievedMajor);

        StudentModel retrievedStudent = studentDao.getStudentById(studentSaved1.getId());
        assertNull(retrievedStudent);
        retrievedStudent = studentDao.getStudentById(studentSaved2.getId());
        assertNull(retrievedStudent);

        ProjectModel retrievedProject = projectDao.getProjectById(projectDSaved1.getId());
        assertNull(retrievedProject);
        retrievedProject = projectDao.getProjectById(projectDSaved2.getId());
        assertNull(retrievedProject);
        retrievedProject = projectDao.getProjectById(projectDSaved3.getId());
        assertNull(retrievedProject);
        retrievedProject = projectDao.getProjectById(projectDSaved4.getId());
        assertNull(retrievedProject);
    }

    @Test
    public void updateMajorTest() {
        MajorModel originalMajorModel = getRandomMajorModel();
        String currentMajorDesc = originalMajorModel.getDescription();
        String modifiedMajorDesc = currentMajorDesc + "---newUpdate";
        originalMajorModel.setDescription(modifiedMajorDesc);
        /*
         * Now start doing update operation
         */
        MajorModel updatedMajorModel = majorDao.update(originalMajorModel);
        assertMajorModels(originalMajorModel, updatedMajorModel);

        /*
         * now reset MajorModel description back to the original value
         */
        originalMajorModel.setDescription(currentMajorDesc);
        updatedMajorModel = majorDao.update(originalMajorModel);
        assertMajorModels(originalMajorModel, updatedMajorModel);
    }

//    private void assertMajorModels(MajorModel randomMajor, MajorModel retrievedMajorModel) {
//        assertEquals(randomMajor.getId(), retrievedMajorModel.getId());
//        assertEquals(randomMajor.getName(), retrievedMajorModel.getName());
//        assertEquals(randomMajor.getDescription(), retrievedMajorModel.getDescription());
//    }
//
//    private MajorModel createMajorByName(String name) {
//        MajorModel major = new MajorModel();
//        major.setName(name);
//        major.setDescription(name + "--description");
//        return major;
//    }
//
//    private MajorModel getRandomMajorModel() {
//        List<MajorModel> majorModelList = majorDao.getMajors();
//        MajorModel randomMajor = null;
//        if(majorModelList != null && majorModelList.size() > 0) {
//            int randomIndex = getRandomInt(0, majorModelList.size());
//            randomMajor = majorModelList.get(randomIndex);
//        }
//        return randomMajor;
//    }
//
//    private void displayMajors(List<MajorModel> majorList) {
//        logger.info("The total number of Majors is: {}", majorList.size());
//        int index = 1;
//        for(MajorModel major : majorList) {
//            logger.info("No.{} Major:", index);
//            displayMajor(major);
//            index++;
//        }
//    }
//
//    private void displayMajor(MajorModel major) {
//        logger.info("Major detail={}", major);
//        displayStudentList(major.getStudentList());
//    }
//
//    private void displayStudentList(List<StudentModel> studentList) {
//        logger.info("The total number of Students is: {}", studentList.size());
//        int index = 1;
//        for(StudentModel student : studentList) {
//            logger.info("No.{} Student:", index);
//            displayStudent(student);
//            index++;
//        }
//    }
//
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
//
//
//    /**
//     * This helper method return a random int value in a range between
//     * min (inclusive) and max (exclusive)
//     * @param min
//     * @param max
//     * @return
//     */
//    private int getRandomInt(int min, int max) {
//        Random random = new Random();
//        return random.nextInt(max - min) + min;
//    }

}
