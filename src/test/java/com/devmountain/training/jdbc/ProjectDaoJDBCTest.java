package com.devmountain.training.jdbc;

import com.devmountain.training.dao.ProjectDao;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class ProjectDaoJDBCTest extends AbstractDaoJDBCTest {
    private Logger logger = LoggerFactory.getLogger(ProjectDaoJDBCTest.class);

//    private static ProjectDao projectDao;

//    private String tempProjectName = null;

    @BeforeClass
    public static void setupOnce() {
        projectDao = new ProjectDaoJDBCImpl();
    }

    @AfterClass
    public static void teardownOnce() {
        projectDao = null;
    }

    @Before
    public void setup() {
        tempProjectName = "Project-" + getRandomInt(1, 1000);
    }

    @After
    public void teardown() {

    }

    @Test
    public void getProjectsTest() {
        List<ProjectModel> projectModelList = projectDao.getProjects();
        displayProjects(projectModelList);
//        int i = 1;
//        for(ProjectModel project : projectModelList) {
//            logger.info("No.{} Project = {}", i, project);
//            i++;
//        }
    }

    @Test
    public void getProjectByIdTest() {
        /*
         * Pick up a random ProjectModel from DB
         */
        ProjectModel randomProject = getRandomProjectModel();
        if(randomProject == null) {
            logger.error("there is no project being found in database, please double check DB connection!");
        } else {
            Long projectId = randomProject.getId();
            ProjectModel retrievedProjectModel = projectDao.getProjectById(projectId);
            assertProjectModels(randomProject, retrievedProjectModel);
        }
    }

    @Test
    public void getProjectByNameTest() {
        /*
         * Pick up a random ProjectModel from DB
         */
        ProjectModel randomProject = getRandomProjectModel();
        if(randomProject == null) {
            logger.error("there is no Project being found in database, please double check DB connection!");
        } else {
            String projectName = randomProject.getName();
            ProjectModel retrievedProjectModel = projectDao.getProjectByName(projectName);
            assertProjectModels(randomProject, retrievedProjectModel);
        }
    }

    @Test
    public void saveProjectTest() {
        ProjectModel project = createProjectByName(tempProjectName);
        ProjectModel savedProject = projectDao.save(project);
        assertNotNull(savedProject);
        assertEquals(project.getName(), savedProject.getName());
        assertEquals(project.getDescription(), savedProject.getDescription());
        /*
         * Now clean up the saved Project from DB Major table
         */
        boolean deleteSuccessfulFlag = projectDao.delete(savedProject);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteProjectTest() {
        ProjectModel project = createProjectByName(tempProjectName);
        ProjectModel savedProject = projectDao.save(project);
        /*
         * Now delete the saved Project from DB Major table
         */
        boolean deleteSuccessfulFlag = projectDao.delete(savedProject);
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteProjectByIdTest() {
        ProjectModel project = createProjectByName(tempProjectName);
        ProjectModel savedProject = projectDao.save(project);
        /*
         * Now delete the saved Project from DB Major table
         */
        boolean deleteSuccessfulFlag = projectDao.deleteById(savedProject.getId());
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void deleteProjectByNameTest() {
        ProjectModel project = createProjectByName(tempProjectName);
        ProjectModel savedProject = projectDao.save(project);
        /*
         * Now delete the saved Project from DB Major table
         */
        boolean deleteSuccessfulFlag = projectDao.deleteByName(savedProject.getName());
        assertEquals(true, deleteSuccessfulFlag);
    }

    @Test
    public void updateProjectTest() {
        ProjectModel originalProjectModel = getRandomProjectModel();
        String originalProjectDesc = originalProjectModel.getDescription();
        String modifiedProjectDesc = originalProjectDesc + "---newUpdate";
        originalProjectModel.setDescription(modifiedProjectDesc);
        /*
         * Now start doing update operation
         */
        ProjectModel updatedProjectModel = projectDao.update(originalProjectModel);
        assertProjectModels(originalProjectModel, updatedProjectModel);

        /*
         * now reset ProjectModel description back to the original value
         */
        originalProjectModel.setDescription(originalProjectDesc);
        updatedProjectModel = projectDao.update(originalProjectModel);
        assertProjectModels(originalProjectModel, updatedProjectModel);
    }

    @Test
    public void getProjectsWithAssociatedStudentsTest() {
        List<ProjectModel> projectModelList = projectDao.getProjectsWithAssociatedStudents();
        displayProjects(projectModelList);
    }

    @Test
    public void getProjectWithAssociatedStudentsByIdTest() {
        /*
         * Pick up a random ProjectModel from DB
         */
        ProjectModel randomProject = getRandomProjectModel();
        if(randomProject == null) {
            logger.error("there is no project being found in database, please double check DB connection!");
        } else {
            Long projectId = randomProject.getId();
            ProjectModel retrievedProjectModel = projectDao.getProjectWithAssociatedStudentsById(projectId);
            assertProjectModels(randomProject, retrievedProjectModel);
            displayProject(retrievedProjectModel);
        }

    }

    @Test
    public void getProjectWithAssociatedStudentsByNameTest() {
        /*
         * Pick up a random ProjectModel from DB
         */
        ProjectModel randomProject = getRandomProjectModel();
        if(randomProject == null) {
            logger.error("there is no Project being found in database, please double check DB connection!");
        } else {
            String projectName = randomProject.getName();
            ProjectModel retrievedProjectModel = projectDao.getProjectWithAssociatedStudentsByName(projectName);
            assertProjectModels(randomProject, retrievedProjectModel);
            displayProject(retrievedProjectModel);
        }

    }

//    private void assertProjectModels(ProjectModel randomProjectModel, ProjectModel retrievedProjectModel) {
//        assertEquals(randomProjectModel.getId(), retrievedProjectModel.getId());
//        assertEquals(randomProjectModel.getName(), retrievedProjectModel.getName());
//        assertEquals(randomProjectModel.getDescription(), retrievedProjectModel.getDescription());
//        assertTrue(randomProjectModel.getCreateDate().isEqual(retrievedProjectModel.getCreateDate()));
//    }
//
//    private ProjectModel createProjectByName(String projectName) {
//        ProjectModel project = new ProjectModel();
//        project.setName(projectName);
//        project.setDescription(projectName + "--description");
//        project.setCreateDate(LocalDate.now());
//        return project;
//    }
//
//    private ProjectModel getRandomProjectModel() {
//        List<ProjectModel> projectModelList = projectDao.getProjects();
//        ProjectModel randomProject = null;
//        if(projectModelList != null && projectModelList.size() > 0) {
//            int randomIndex = getRandomInt(0, projectModelList.size());
//            randomProject = projectModelList.get(randomIndex);
//        }
//        return randomProject;
//    }
//
//    private void displayProjects(List<ProjectModel> projectList) {
//        logger.info("The total number of Projects is: {}", projectList.size());
//        int index = 1;
//        for(ProjectModel eachProject : projectList) {
//            logger.info("No.{} project:", index);
//            displayProject(eachProject);
//            index++;
//        }
//    }
//
//    private void displayProject(ProjectModel project) {
//        logger.info("Project detail={}", project);
//        displayStudentList(project.getStudentList());
//    }
//
//    private void displayStudentList(List<StudentModel> studentList) {
//        logger.info("\t The total associated students={}", studentList.size());
//        int index = 1;
//        for(StudentModel student : studentList) {
//            logger.info("No.{} student = {}", index, student);
//            index++;
//        }
//
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
