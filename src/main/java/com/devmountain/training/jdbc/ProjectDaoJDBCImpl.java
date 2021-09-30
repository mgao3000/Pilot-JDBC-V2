package com.devmountain.training.jdbc;

import com.devmountain.training.dao.ProjectDao;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import com.devmountain.training.util.JDBCUtils;
import com.devmountain.training.util.SQLStatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDaoJDBCImpl implements ProjectDao {
    private Logger logger = LoggerFactory.getLogger(ProjectDaoJDBCImpl.class);

//    private final String SQL_INSERT_PROJECT = "INSERT INTO PROJECT (NAME, DESCRIPTION, CREATE_DATE) VALUES (?, ?, ?)";
//    private final String SQL_UPDATE_PROJECT = "UPDATE PROJECT SET name=?, description=? , create_date=? where id=?";
//
//    private final String SELECT_ALL_PROJECT = "SELECT * FROM PROJECT";
//    private final String SQL_SELECT_PROJECT_ID_BY_NAME = "SELECT ID FROM PROJECT WHERE NAME=?";
//    private final String SQL_SELECT_PROJECT_BY_ID = "SELECT * FROM PROJECT where id = ?";
//    private final String SQL_SELECT_PROJECT_BY_NAME = "SELECT * FROM PROJECT where name = ?";
//    private final String SELECT_STUDENTS_BY_PROJECT_ID = "SELECT s.* FROM STUDENT s, STUDENT_PROJECT sp where s.ID=sp.STUDENT_ID and sp.PROJECT_ID=?";
//    private final String SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID = "SELECT NAME from MAJOR WHERE ID = ?";
//
//    private final String SQL_DELETE_PROJECT_BY_ID = "DELETE FROM PROJECT where ID=?";
//    private final String SQL_DELETE_PROJECT_BY_NAME = "DELETE FROM PROJECT where NAME=?";
//    private final String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_PROJECT_ID = "DELETE from STUDENT_PROJECT WHERE PROJECT_ID = ?";



    @Override
    public ProjectModel save(ProjectModel project) {
        ProjectModel savedProject = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for saving a projectModel...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Insert statement...");
//            String SQL_INSERT_PROJECT = "INSERT INTO PROJECT (NAME, DESCRIPTION, CREATE_DATE) VALUES (?, ?, ?)";
//            preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_INSERT_PROJECT, Statement.RETURN_GENERATED_KEYS);
//            preparedStatement.setLong(1, project.getId());
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setTimestamp(3, project.getCreateDateInTimestamp());

            int row = preparedStatement.executeUpdate();
            if(row > 0)
                savedProject = project;

            rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                Long generatedId = rs.getLong(1);
                if(generatedId != null)
                    savedProject.setId(generatedId);
            }

        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to save a project. The input project =" + project + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 3: finally block used to close resources
            try {
                if(rs != null) rs.close();
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return savedProject;
    }

    @Override
    public ProjectModel update(ProjectModel project) {
        ProjectModel updatedProject = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for updating a projectModel");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Updating statement...");

//            String SQL_UPDATE_PROJECT = "UPDATE PROJECT SET name=?, description=? , create_date=? where id=?";
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_UPDATE_PROJECT);
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setTimestamp(3, project.getCreateDateInTimestamp());
            preparedStatement.setLong(4, project.getId());

            int row = preparedStatement.executeUpdate();
            if(row > 0)
                updatedProject = project;

        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to update a Project. The input project =" + project + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 3: finally block used to close resources
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return updatedProject;
    }

    @Override
    public boolean deleteByName(String projectName) {
        boolean isProjectDeleted = false;
        Connection dbConnection = null;
        PreparedStatement deleteProjectByNamePS = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: select projectId using projectName
            Long projectId = retrieveProjectIdByProjectName(dbConnection, projectName);

            if(projectId == null) {
                return isProjectDeleted;
            }
            //Step 3: disable auto-commit mode
            dbConnection.setAutoCommit(false);

            //Step 4: first delete the relationship in student_project table using projectId
//            String SQL_DELETE_ALL_STUDENT_IDS_BY_PROJECT_ID = "DELETE from STUDENT_PROJECT WHERE PROJECT_ID = ?";
            deleteStudentIdsByProjectId(dbConnection, projectId);
//            if(!deleteResultFlag) {
//                dbConnection.rollback();
//                return isProjectDeleted;
//            }

            //Step 5: prepare psDeleteProjectByName
//            String SQL_DELETE_PROJECT_BY_NAME = "DELETE FROM PROJECT where NAME=?";
            deleteProjectByNamePS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_PROJECT_BY_NAME);
            deleteProjectByNamePS.setString(1, projectName);

            //STEP 6: Execute a query
            logger.debug("Deleting a projectModel by projectName statement...");

            int row = deleteProjectByNamePS.executeUpdate();
            if(row > 0) {
                isProjectDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to delete a Project by ProjectName. The input ProjectName =" + projectName + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 7: finally block used to close resources
            try {
                if(deleteProjectByNamePS != null) deleteProjectByNamePS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close deleteProjectByNamePS or dbConnection, error = " + se.getMessage());
            }
        }
        return isProjectDeleted;
    }

    private void deleteStudentIdsByProjectId(Connection dbConnection, Long projectId) {
        PreparedStatement deleteStudentIdsByProjectIdPS = null;
//        boolean isDeleteSuccessful = false;
        try {
            deleteStudentIdsByProjectIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_PROJECT_ID);
            deleteStudentIdsByProjectIdPS.setLong(1, projectId);

            //Execute a query
            logger.debug("Deleting all StudentIds by projectId statement...");

            deleteStudentIdsByProjectIdPS.executeUpdate();
//            if(row > 0)
//                isDeleteSuccessful = true;
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete all StudentIds by ProjectId. The input ProjectId = {}, the error={}", projectId,
                    se.getMessage());
        }finally {
            //Finally block used to close resources
            try {
                if(deleteStudentIdsByProjectIdPS != null) deleteStudentIdsByProjectIdPS.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet or deleteStudentIdsByProjectIdPS, error = " + se.getMessage());
            }
        }
//        return isDeleteSuccessful;
    }

    private Long retrieveProjectIdByProjectName(Connection dbConnection, String projectName) {
        PreparedStatement selectProjectIdByProjectNamePS = null;
        ResultSet rs = null;
        Long projectId = null;
        try {
//            String SQL_SELECT_PROJECT_ID_BY_NAME = "SELECT ID FROM PROJECT WHERE NAME=?";
            selectProjectIdByProjectNamePS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECT_ID_BY_NAME);
            selectProjectIdByProjectNamePS.setString(1, projectName);
            rs = selectProjectIdByProjectNamePS.executeQuery();

            //STEP 3: Extract data from result set
            if(rs.next()) {
                //Retrieve by column name
                projectId = rs.getLong("id");
                logger.info("========== retrieve projectId={} using projectName={}", projectId, projectName);
            }
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to select a ProjectId by ProjectName. The input ProjectName = {}, the error={}", projectName,
                    se.getMessage());
        } finally {
            //Finally block used to close resources
            try {
                if(rs != null) rs.close();;
                if(selectProjectIdByProjectNamePS != null) selectProjectIdByProjectNamePS.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet or selectProjectIdByProjectNamePS, error = " + se.getMessage());
            }
        }
        return projectId;
    }

    @Override
    public boolean deleteById(Long projectId) {
        boolean isProjectDeleted = false;
        Connection dbConnection = null;
        PreparedStatement deleteProjectByIdPS = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: disable auto-commit mode
            dbConnection.setAutoCommit(false);

            //Step 3: first delete the relationship in student_project table using projectId
            deleteStudentIdsByProjectId(dbConnection, projectId);
//            if(!deleteResultFlag) {
//                dbConnection.rollback();
//                return isProjectDeleted;
//            }

            //Step 4: prepare psDeleteProjectByName
//            String SQL_DELETE_PROJECT_BY_ID = "DELETE FROM PROJECT where ID=?";
            deleteProjectByIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_PROJECT_BY_ID);
            deleteProjectByIdPS.setLong(1, projectId);

            //STEP 5: Execute a query
            logger.debug("Deleting a projectModel by projectId statement...");

            int row = deleteProjectByIdPS.executeUpdate();
            if(row > 0) {
                isProjectDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to delete a Project by ProjectId. The input ProjectId ={}, the error={}", projectId,
                    e.getMessage());
        }
        finally {
            //STEP 6: finally block used to close resources
            try {
                if(deleteProjectByIdPS != null) deleteProjectByIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close deleteProjectByIdPS or dbConnection, error = " + se.getMessage());
            }
        }
        return isProjectDeleted;
    }

    @Override
    public boolean delete(ProjectModel project) {
        Long projectId = project.getId();
        return deleteById(projectId);
    }

    @Override
    public List<ProjectModel> getProjects() {
        List<ProjectModel> projects = new ArrayList<ProjectModel>();
        Connection dbConnection = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery(SQLStatementUtils.SELECT_ALL_PROJECT);

            //STEP 3: Extract data from result set
            while(rs.next()) {
                //Retrieve by column name
                Long id  = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Timestamp createDate = rs.getTimestamp("create_date");

                //Fill the object
                ProjectModel project = new ProjectModel();
                project.setId(id);
                project.setName(name);
                project.setDescription(description);
                project.setCreateDate(createDate);
                projects.add(project);
            }
        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to select all Projects. the error = " + e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return projects;
    }

    @Override
    public ProjectModel getProjectById(Long id) {
        ProjectModel retrievedProject = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
//            String  SQL_SELECT_PROJECT_BY_ID = "SELECT * FROM PROJECT where id = ?";
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECT_BY_ID);
            preparedStatement.setLong(1, id);
            rs = preparedStatement.executeQuery();

            //STEP 3: Extract data from result set
            if(rs.next()) {
                //Retrieve by column name
                Long deptId  = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Timestamp createDate = rs.getTimestamp("create_date");

                //Fill the object
                retrievedProject = new ProjectModel();
                retrievedProject.setId(deptId);
                retrievedProject.setName(name);
                retrievedProject.setDescription(description);
                retrievedProject.setCreateDate(createDate);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a project by projectId. The input projectId =" + id + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(rs != null) rs.close();
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedProject;
    }

    @Override
    public ProjectModel getProjectByName(String projectName) {
        ProjectModel retrievedProject = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
//            String SQL_SELECT_PROJECT_BY_NAME = "SELECT * FROM PROJECT where name = ?";
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECT_BY_NAME);
            preparedStatement.setString(1, projectName);
            rs = preparedStatement.executeQuery();

            //STEP 3: Extract data from result set
            if(rs.next()) {
                //Retrieve by column name
                Long deptId  = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Timestamp createDate = rs.getTimestamp("create_date");

                //Fill the object
                retrievedProject = new ProjectModel();
                retrievedProject.setId(deptId);
                retrievedProject.setName(name);
                retrievedProject.setDescription(description);
                retrievedProject.setCreateDate(createDate);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a project by projectName. The input projectName =" + projectName + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(rs != null) rs.close();
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedProject;
    }

    @Override
    public List<ProjectModel> getProjectsWithAssociatedStudents() {
        List<ProjectModel> projects = new ArrayList<ProjectModel>();
        Connection dbConnection = null;
        Statement getAllProjectsStatement = null;
        PreparedStatement getStudentsByProjectIdPS = null;
        PreparedStatement getMajorNameByMajorIdPS = null;
        ResultSet allProjectResultSet = null;
        ResultSet studentListByProjectIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: prepare getAllProjectsStatement and then Execute the query
            logger.debug("Creating statement...");
            getAllProjectsStatement = dbConnection.createStatement();
//            String SELECT_ALL_PROJECT = "SELECT * FROM PROJECT";
            allProjectResultSet = getAllProjectsStatement.executeQuery(SQLStatementUtils.SELECT_ALL_PROJECT);

//            String SELECT_STUDENTS_BY_PROJECT_ID = "SELECT s* FROM STUDENT s, STUDENT_PROJECT sp where s.ID=sp.STUDENT_ID and sp.PROJECT_ID=?";

            //STEP 3: Extract project data from result set
            while(allProjectResultSet.next()) {
                //Retrieve by column name
                Long id  = allProjectResultSet.getLong("id");
                String name = allProjectResultSet.getString("name");
                String description = allProjectResultSet.getString("description");
                Timestamp createDate = allProjectResultSet.getTimestamp("create_date");

                //Fill the object
                ProjectModel project = new ProjectModel();
                project.setId(id);
                project.setName(name);
                project.setDescription(description);
                project.setCreateDate(createDate);

                //Step 4: Now prepare getStudentsByProjectIdPS and then execute the query using each projectId
                getStudentsByProjectIdPS = dbConnection.prepareStatement(SQLStatementUtils.SELECT_STUDENTS_BY_PROJECT_ID);
                getStudentsByProjectIdPS.setLong(1, project.getId());
                studentListByProjectIdResultSet = getStudentsByProjectIdPS.executeQuery();

                //Step 5: Retrieve all students by column name and then fill into student list
                List<StudentModel> studentList = getStudentList(studentListByProjectIdResultSet);

                //Step 6: use student's major ID to retrieve the associated majorName
                getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                setStudentMajorNameByMajorId(getMajorNameByMajorIdPS, studentList);

                project.setStudentList(studentList);
                projects.add(project);
            }
        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to select all Projects and all of associated students to each project . the error = " + e.getMessage());
        }
        finally {
            //STEP 7: finally block used to close resources
            try {
                if (studentListByProjectIdResultSet != null) studentListByProjectIdResultSet.close();
                if (allProjectResultSet != null) allProjectResultSet.close();
                if (getStudentsByProjectIdPS != null) getStudentsByProjectIdPS.close();
                if (getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                if (getAllProjectsStatement != null) getAllProjectsStatement.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close resultSet, statement, preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return projects;
    }

    private void setStudentMajorNameByMajorId(PreparedStatement getMajorNameByMajorIdPS, List<StudentModel> studentList)  {
        ResultSet getMajorNameByMajorIdResultSet = null;
        for(StudentModel student : studentList)  {
            try {
                getMajorNameByMajorIdPS.setLong(1, student.getMajorId());
                getMajorNameByMajorIdResultSet = getMajorNameByMajorIdPS.executeQuery();

                //Extract major name from result set
                String majorName = null;
                if (getMajorNameByMajorIdResultSet.next()) {
                    //Retrieve Student ID by column name
                    majorName = getMajorNameByMajorIdResultSet.getString("name");
                    logger.info("========== retrieve majorName={}", majorName);
                }
                if(majorName != null)
                    student.setMajorName(majorName);
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to select majorName by majorId={},  the error = {}",
                        student.getMajorId(), se.getMessage());
            } finally {
                //STEP 6: finally block used to close resources
                try {
                    if (getMajorNameByMajorIdResultSet != null) getMajorNameByMajorIdResultSet.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close resultSet, error = " + se.getMessage());
                }
            }

        }
    }

    private List<StudentModel> getStudentList(ResultSet rs) throws SQLException {
        List<StudentModel> studentList = new ArrayList<StudentModel>();
        while (rs.next()) {
            //Retrieve by column name
            Long id  = rs.getLong("id");
            String loginName = rs.getString("login_name");
            String password = rs.getString("password");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String address = rs.getString("address");
            Timestamp enrolledDate = rs.getTimestamp("enrolled_date");
            Long majorId  = rs.getLong("major_id");

            //Fill the object
            StudentModel student = new StudentModel();
            student.setId(id);
            student.setLoginName(loginName);
            student.setPassword(password);
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setEmail(email);
            student.setAddress(address);
            student.setEnrolledDate(enrolledDate);
            student.setMajorId(majorId);

            studentList.add(student);
        }
        return studentList;
    }

    @Override
    public ProjectModel getProjectWithAssociatedStudentsById(Long projectId) {
        ProjectModel retrievedProject = null;
        Connection dbConnection = null;
        PreparedStatement getProjectByProjectIDPS = null;
        PreparedStatement getStudentsByProjectIdPS = null;
        PreparedStatement getMajorNameByMajorIdPS = null;
        ResultSet projectResultSet = null;
        ResultSet studentListByProjectIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Prepare getProjectByProjectNamePS and Execute a query to retrieve a project
            logger.debug("Creating statement...");
            getProjectByProjectIDPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECT_BY_ID);
            getProjectByProjectIDPS.setLong(1, projectId);
            projectResultSet = getProjectByProjectIDPS.executeQuery();

            //STEP 3: Extract data from result set
            if(projectResultSet.next()) {
                //Retrieve by column name
                Long deptId  = projectResultSet.getLong("id");
                String name = projectResultSet.getString("name");
                String description = projectResultSet.getString("description");
                Timestamp createDate = projectResultSet.getTimestamp("create_date");

                //Fill the object
                retrievedProject = new ProjectModel();
                retrievedProject.setId(deptId);
                retrievedProject.setName(name);
                retrievedProject.setDescription(description);
                retrievedProject.setCreateDate(createDate);

                //Step 4: Now prepare getStudentsByProjectIdPS and then execute the query using the retrieved projectId
                getStudentsByProjectIdPS = dbConnection.prepareStatement(SQLStatementUtils.SELECT_STUDENTS_BY_PROJECT_ID);
                getStudentsByProjectIdPS.setLong(1, projectId);
                studentListByProjectIdResultSet = getStudentsByProjectIdPS.executeQuery();

                //Step 5: Retrieve all students by column name and then fill into student list
                List<StudentModel> studentList = getStudentList(studentListByProjectIdResultSet);

                //Step 6: use student's major ID to retrieve the associated majorName
                getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                setStudentMajorNameByMajorId(getMajorNameByMajorIdPS, studentList);

                retrievedProject.setStudentList(studentList);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a project and all of the associated students by projectId. " +
                    "The input projectId ={}, the error={}", projectId, e.getMessage());
        }
        finally {
            //STEP 7: finally block used to close resources
            try {
                if(studentListByProjectIdResultSet != null) studentListByProjectIdResultSet.close();
                if(projectResultSet != null) projectResultSet.close();
                if(getStudentsByProjectIdPS != null) getStudentsByProjectIdPS.close();
                if(getProjectByProjectIDPS != null) getProjectByProjectIDPS.close();
                if(getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close resultSet, preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedProject;
    }

    @Override
    public ProjectModel getProjectWithAssociatedStudentsByName(String projectName) {
        ProjectModel retrievedProject = null;
        Connection dbConnection = null;
        PreparedStatement getProjectByProjectNamePS = null;
        PreparedStatement getStudentsByProjectIdPS = null;
        PreparedStatement getMajorNameByMajorIdPS = null;
        ResultSet projectResultSet = null;
        ResultSet studentListByProjectIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Prepare getProjectByProjectNamePS and Execute a query to retrieve a project
            logger.debug("Creating statement...");
//            String SELECT_A_PROJECT_BY_PROJECT_NAME = "SELECT * FROM PROJECT where name = ?";
            getProjectByProjectNamePS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECT_BY_NAME);
            getProjectByProjectNamePS.setString(1, projectName);
            projectResultSet = getProjectByProjectNamePS.executeQuery();

            //STEP 3: Extract data from result set
            if(projectResultSet.next()) {
                //Retrieve by column name
                Long deptId  = projectResultSet.getLong("id");
                String name = projectResultSet.getString("name");
                String description = projectResultSet.getString("description");
                Timestamp createDate = projectResultSet.getTimestamp("create_date");

                //Fill the object
                retrievedProject = new ProjectModel();
                retrievedProject.setId(deptId);
                retrievedProject.setName(name);
                retrievedProject.setDescription(description);
                retrievedProject.setCreateDate(createDate);

                //Step 4: Now prepare getStudentsByProjectIdPS and then execute the query using the retrieved projectId
                getStudentsByProjectIdPS = dbConnection.prepareStatement(SQLStatementUtils.SELECT_STUDENTS_BY_PROJECT_ID);
                getStudentsByProjectIdPS.setLong(1, retrievedProject.getId());
                studentListByProjectIdResultSet = getStudentsByProjectIdPS.executeQuery();

                //Step 5: Retrieve all students by column name and then fill into student list
                List<StudentModel> studentList = getStudentList(studentListByProjectIdResultSet);

                //Step 6: use student's major ID to retrieve the associated majorName
                getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                setStudentMajorNameByMajorId(getMajorNameByMajorIdPS, studentList);

                retrievedProject.setStudentList(studentList);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a project and all of the associated students by projectName. The input projectName =" + projectName + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 7: finally block used to close resources
            try {
                if(studentListByProjectIdResultSet != null) studentListByProjectIdResultSet.close();
                if(projectResultSet != null) projectResultSet.close();
                if(getStudentsByProjectIdPS != null) getStudentsByProjectIdPS.close();
                if(getProjectByProjectNamePS != null) getProjectByProjectNamePS.close();
                if(getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedProject;
    }
}
