package com.devmountain.training.jdbc;

import com.devmountain.training.dao.StudentDao;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import com.devmountain.training.util.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDaoJDBCImpl implements StudentDao {
    private Logger logger = LoggerFactory.getLogger(StudentDaoJDBCImpl.class);

    private final String SQL_INSERT_STUDENT = "INSERT INTO STUDENT (login_name, password, first_name, last_name, email, address, enrolled_DATE, major_id) VALUES (?, ?, ?,?,?,?,?,?)";
    ;
    private final String SQL_UPDATE_STUDENT = "UPDATE STUDENT SET login_name=?, password=?, email=?, address=? where id=?";

    private final String SQL_DELETE_STUDENT_BY_LOGIN_NAME = "DELETE FROM STUDENT where LOGIN_NAME=?";
    private final String SQL_DELETE_STUDENT_BY_ID = "DELETE FROM STUDENT where ID=?";
    private final String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID = "DELETE from STUDENT_PROJECT WHERE STUDENT_ID = ?";

    private final String SQL_SELECT_ALL_STUDENTS = "SELECT * FROM STUDENT";
    private final String SQL_SELECT_STUDENTS_BY_MAJOR_ID = "SELECT * FROM STUDENT where major_id = ?";
    private final String SQL_SELECT_STUDENT_BY_ID = "SELECT * FROM STUDENT where id = ?";
    private final String SQL_SELECT_STUDENT_BY_LOGIN_NAME = "SELECT * FROM STUDENT where login_name = ?";
    private final String SQL_SELECT_STUDENT_ID_BY_LOGIN_NAME = "SELECT ID FROM STUDENT where login_name = ?";
    private final String SQL_SELECT_PROJECTS_BY_STUDENT_ID = "SELECT p.* FROM PROJECT p, STUDENT_PROJECT sp where p.ID=sp.PROJECT_ID and sp.STUDENT_ID=?";
    private final String SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID = "SELECT NAME from MAJOR WHERE ID = ?";

    @Override
    public StudentModel save(StudentModel student, Long majorId) {
        StudentModel savedStudent = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for saving a studentModel...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Insert statement...");
//            preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
            preparedStatement = dbConnection.prepareStatement(SQL_INSERT_STUDENT, Statement.RETURN_GENERATED_KEYS);
//            preparedStatement.setLong(1, department.getId());
            preparedStatement.setString(1, student.getLoginName());
            preparedStatement.setString(2, student.getPassword());
            preparedStatement.setString(3, student.getFirstName());
            preparedStatement.setString(4, student.getLastName());
            preparedStatement.setString(5, student.getEmail());
            preparedStatement.setString(6, student.getAddress());
            preparedStatement.setTimestamp(7, student.getEnrolledDateInTimestamp());
            preparedStatement.setLong(8, majorId);

            int row = preparedStatement.executeUpdate();
            if (row > 0)
                savedStudent = student;

            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                Long generatedId = rs.getLong(1);
                if (generatedId != null)
                    savedStudent.setId(generatedId);
            }

        } catch (SQLException e) {
            logger.error("SQLException is caught when trying to save a student. The input student =" + student + ", the error = " + e.getMessage());
        } finally {
            //STEP 3: finally block used to close resources
            try {
                if (rs != null) rs.close();
                if (preparedStatement != null) preparedStatement.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return savedStudent;
    }

    @Override
    public StudentModel update(StudentModel student) {
        StudentModel updatedStudent = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for updating a studentModel");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Updating statement...");

            preparedStatement = dbConnection.prepareStatement(SQL_UPDATE_STUDENT);
            preparedStatement.setString(1, student.getLoginName());
            preparedStatement.setString(2, student.getPassword());
            preparedStatement.setString(3, student.getEmail());
            preparedStatement.setString(4, student.getAddress());
            preparedStatement.setLong(5, student.getId());

            int row = preparedStatement.executeUpdate();
            if (row > 0)
                updatedStudent = student;

        } catch (Exception e) {
            logger.error("SQLException is caught when trying to update a Student. The input student =" + student + ", the error = " + e.getMessage());
        } finally {
            //STEP 3: finally block used to close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return updatedStudent;
    }

    @Override
    public boolean deleteByLoginName(String loginName) {
        boolean isStudentDeleted = false;
        Connection dbConnection = null;
        PreparedStatement deleteStudentByLoginNamePS = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: retrieve student ID by login_name
            Long studentId = retrieveStudentIdByLoginName(dbConnection, loginName);

            if (studentId == null) {
                return isStudentDeleted;
            }
            //Step 3: disable auto-commit mode
            dbConnection.setAutoCommit(false);

            //Step 4: first delete the relationship in student_project table using studentId
            deleteStudentProjectIdsByStudentId(dbConnection, studentId);
//            if (!deleteResultFlag) {
//                dbConnection.rollback();
//                return isStudentDeleted;
//            }

            //Step 5: prepare deleteStudentByLoginNamePS
            deleteStudentByLoginNamePS = dbConnection.prepareStatement(SQL_DELETE_STUDENT_BY_LOGIN_NAME);
            deleteStudentByLoginNamePS.setString(1, loginName);

            //STEP 6: Execute a query
            logger.debug("Deleting a studentModel by student loginName statement...");

            int row = deleteStudentByLoginNamePS.executeUpdate();
            if (row > 0) {
                isStudentDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete a Student by LoginName. The input loginName ={}, the error={}",
                    loginName, se.getMessage());
        } finally {
            //STEP 7: finally block used to close resources
            try {
                if (deleteStudentByLoginNamePS != null) deleteStudentByLoginNamePS.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close deleteStudentByLoginNamePS or dbConnection, error = " + se.getMessage());
            }
        }
        return isStudentDeleted;
    }

    private void deleteStudentProjectIdsByStudentId(Connection dbConnection, Long studentId) {
        PreparedStatement deleteStudentIdsByStudentIdPS = null;
//        boolean isDeleteSuccessful = false;
        try {
            deleteStudentIdsByStudentIdPS = dbConnection.prepareStatement(
                    SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID);
            deleteStudentIdsByStudentIdPS.setLong(1, studentId);

            //Execute a query
            logger.debug("Deleting all StudentId/projectId relationships by studentId statement...");

            deleteStudentIdsByStudentIdPS.executeUpdate();
//            if(row > 0)
//                isDeleteSuccessful = true;
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete all Student.project relationship by studentId. The input studentId ={}, the error={}",
                    studentId, se.getMessage());
        }finally {
            //Finally block used to close resources
            try {
                if(deleteStudentIdsByStudentIdPS != null) deleteStudentIdsByStudentIdPS.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet or deleteStudentIdsByStudentIdPS, error = " + se.getMessage());
            }
        }
//        return isDeleteSuccessful;
    }

    @Override
        public boolean deleteById (Long studentId){
        boolean isStudentDeleted = false;
        Connection dbConnection = null;
        PreparedStatement deleteStudentByIDPS = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: disable auto-commit mode
            dbConnection.setAutoCommit(false);

            //Step 3: first delete the relationship in student_project table using studentId
            deleteStudentProjectIdsByStudentId(dbConnection, studentId);
//            if (!deleteResultFlag) {
//                dbConnection.rollback();
//                return isStudentDeleted;
//            }

            //Step 4: prepare deleteStudentByLoginNamePS
            deleteStudentByIDPS = dbConnection.prepareStatement(SQL_DELETE_STUDENT_BY_ID);
            deleteStudentByIDPS.setLong(1, studentId);

            //STEP 5: Execute a query
            logger.debug("Deleting a studentModel by student ID statement...");

            int row = deleteStudentByIDPS.executeUpdate();
            if (row > 0) {
                isStudentDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete a Student by ID. The input studentId ={}, the error={}",
                    studentId, se.getMessage());
        } finally {
            //STEP 7: finally block used to close resources
            try {
                if (deleteStudentByIDPS != null) deleteStudentByIDPS.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close deleteStudentByIDPS or dbConnection, error = " + se.getMessage());
            }
        }
        return isStudentDeleted;



//            boolean isStudentDeleted = false;
//            Connection dbConnection = null;
//            PreparedStatement preparedStatement = null;
//
//            try {
//                //STEP 1: Open a connection
//                logger.info("Connecting to database...");
//                dbConnection = JDBCUtils.getConnection();
//
//                //STEP 2: Execute a query
//                logger.debug("Deleting a projectModel by projectId statement...");
//
//                preparedStatement = dbConnection.prepareStatement(SQL_DELETE_STUDENT_BY_ID);
//                preparedStatement.setLong(1, studentId);
//
//                int row = preparedStatement.executeUpdate();
//                if (row > 0)
//                    isStudentDeleted = true;
//
//            } catch (Exception e) {
//                logger.error("SQLException is caught when trying to delete a Student by studentId. The input studentId =" + studentId + ", the error = " + e.getMessage());
//            } finally {
//                //STEP 3: finally block used to close resources
//                try {
//                    if (preparedStatement != null) preparedStatement.close();
//                    if (dbConnection != null) dbConnection.close();
//                } catch (SQLException se) {
//                    logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
//                }
//            }
//            return isStudentDeleted;
        }

        @Override
        public boolean delete(StudentModel student){
            Long studentId = student.getId();
            return deleteById(studentId);
        }

        @Override
        public List<StudentModel> getStudents () {
            List<StudentModel> students = new ArrayList<StudentModel>();
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
                rs = stmt.executeQuery(SQL_SELECT_ALL_STUDENTS);

                //STEP 3: Extract data from result set
                while (rs.next()) {
                    students.add(convertResultSetToStudentModel(rs));
                }
            } catch (SQLException e) {
                logger.error("SQLException is caught when trying to select all Students. the error = " + e.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return students;
        }

        @Override
        public List<StudentModel> getStudentsByMajorId (Long majorId){
            List<StudentModel> students = new ArrayList<StudentModel>();
            Connection dbConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                preparedStatement = dbConnection.prepareStatement(SQL_SELECT_STUDENTS_BY_MAJOR_ID);
                preparedStatement.setLong(1, majorId);
                rs = preparedStatement.executeQuery();

                //STEP 3: Extract data from result set
                while (rs.next()) {
                    students.add(convertResultSetToStudentModel(rs));
                }
            } catch (SQLException e) {
                logger.error("SQLException is caught when trying to select all Students by majorId=" + majorId + ". the error = " + e.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (rs != null) rs.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return students;
        }

        @Override
        public StudentModel getStudentById (Long id){
            StudentModel retrievedStudent = null;
            Connection dbConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                preparedStatement = dbConnection.prepareStatement(SQL_SELECT_STUDENT_BY_ID);
                preparedStatement.setLong(1, id);
                rs = preparedStatement.executeQuery();

                //STEP 3: Extract data from result set
                if (rs.next()) {
                    retrievedStudent = convertResultSetToStudentModel(rs);
                }
            } catch (Exception e) {
                logger.error("SQLException is caught when trying to select a student by studentId. The input studentId =" + id + ", the error = " + e.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (rs != null) rs.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }

            return retrievedStudent;
        }

        @Override
        public StudentModel getStudentByLoginName (String loginName){
            StudentModel retrievedStudent = null;
            Connection dbConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                preparedStatement = dbConnection.prepareStatement(SQL_SELECT_STUDENT_BY_LOGIN_NAME);
                preparedStatement.setString(1, loginName);
                rs = preparedStatement.executeQuery();

                //STEP 3: Extract data from result set
                if (rs.next()) {
                    retrievedStudent = convertResultSetToStudentModel(rs);
                }
            } catch (Exception e) {
                logger.error("SQLException is caught when trying to select a student by loginName. The input loginName =" + loginName + ", the error = " + e.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (rs != null) rs.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return retrievedStudent;
        }

        @Override
        public List<StudentModel> getStudentsWithAssociatedProjects () {
            List<StudentModel> students = new ArrayList<StudentModel>();
            Connection dbConnection = null;
            Statement getAllStudentsStatement = null;
            PreparedStatement getProjectsByStudentIdPS = null;
            PreparedStatement getMajorNameByMajorIdPS = null;
            ResultSet allStudentResultSet = null;
            ResultSet projectListByStudentIdResultSet = null;
            ResultSet getMajorNameByMajorIdResultSet = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: prepare getAllStudentsStatement and then Execute the query
                logger.debug("Creating getAllStudentsStatement...");
                getAllStudentsStatement = dbConnection.createStatement();
                allStudentResultSet = getAllStudentsStatement.executeQuery(SQL_SELECT_ALL_STUDENTS);

                //STEP 3: Extract student data from result set
                while(allStudentResultSet.next()) {
                    StudentModel retrievedStudent = convertResultSetToStudentModel(allStudentResultSet);

                    //Step 4: Now prepare getProjectsByStudentIdPS and then execute the query using each studentId
                    getProjectsByStudentIdPS = dbConnection.prepareStatement(SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                    getProjectsByStudentIdPS.setLong(1, retrievedStudent.getId());
                    projectListByStudentIdResultSet = getProjectsByStudentIdPS.executeQuery();

                    //Step 5: Retrieve all projects by column name and then fill into project list
                    List<ProjectModel> projectList = getProjectList(projectListByStudentIdResultSet);

                    retrievedStudent.setProjectList(projectList);

                    //Step 5: use student's major ID to retrieve the associated majorName
                    getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                    getMajorNameByMajorIdPS.setLong(1, retrievedStudent.getMajorId());
                    getMajorNameByMajorIdResultSet = getMajorNameByMajorIdPS.executeQuery();

                    //STEP 6: Extract major name from result set
                    String majorName = getMajorNameByResultSet(getMajorNameByMajorIdResultSet);
                    if(majorName != null)
                        retrievedStudent.setMajorName(majorName);

                    students.add(retrievedStudent);
                }
            }
            catch(SQLException e){
                logger.error("SQLException is caught when trying to select all Students and all of associated projects to each student . the error = " + e.getMessage());
            }
            finally {
                //STEP 6: finally block used to close resources
                try {
                    if (getMajorNameByMajorIdResultSet != null) getMajorNameByMajorIdResultSet.close();
                    if (projectListByStudentIdResultSet != null) projectListByStudentIdResultSet.close();
                    if (allStudentResultSet != null) allStudentResultSet.close();
                    if (getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                    if (getProjectsByStudentIdPS != null) getProjectsByStudentIdPS.close();
                    if (getAllStudentsStatement != null) getAllStudentsStatement.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close resultSet, statement, preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return students;
        }

        @Override
        public List<StudentModel> getStudentsWithAssociatedProjectsByMajorId (Long majorId){
            List<StudentModel> students = new ArrayList<StudentModel>();
            Connection dbConnection = null;
            PreparedStatement getStudentsByMajorIdPS = null;
            PreparedStatement getProjectsByStudentIdPS = null;
            PreparedStatement getMajorNameByMajorIdPS = null;
            ResultSet studentByMajorIdResultSet = null;
            ResultSet projectListByStudentIdResultSet = null;
            ResultSet getMajorNameByMajorIdResultSet = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: prepare getStudentsByMajorIdPS and then Execute the query
                logger.debug("Creating getStudentsByMajorIdPS...");
                getStudentsByMajorIdPS = dbConnection.prepareStatement(SQL_SELECT_STUDENTS_BY_MAJOR_ID);
                getStudentsByMajorIdPS.setLong(1, majorId);
                studentByMajorIdResultSet = getStudentsByMajorIdPS.executeQuery();

                //STEP 3: Extract student data from result set
                while(studentByMajorIdResultSet.next()) {
                    StudentModel retrievedStudent = convertResultSetToStudentModel(studentByMajorIdResultSet);

                    //Step 4: Now prepare getProjectsByStudentIdPS and then execute the query using each studentId
                    getProjectsByStudentIdPS = dbConnection.prepareStatement(SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                    getProjectsByStudentIdPS.setLong(1, retrievedStudent.getId());
                    projectListByStudentIdResultSet = getProjectsByStudentIdPS.executeQuery();

                    //Step 5: Retrieve all projects by column name and then fill into project list
                    List<ProjectModel> projectList = getProjectList(projectListByStudentIdResultSet);

                    retrievedStudent.setProjectList(projectList);

                    //Step 5: use student's major ID to retrieve the associated majorName
                    getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                    getMajorNameByMajorIdPS.setLong(1, retrievedStudent.getMajorId());
                    getMajorNameByMajorIdResultSet = getMajorNameByMajorIdPS.executeQuery();

                    //STEP 6: Extract major name from result set
                    String majorName = getMajorNameByResultSet(getMajorNameByMajorIdResultSet);
                    if(majorName != null)
                        retrievedStudent.setMajorName(majorName);

                    students.add(retrievedStudent);
                }
            }
            catch(SQLException e){
                logger.error("SQLException is caught when trying to select Students and all of associated projects to each student using majorId. the error = " + e.getMessage());
            }
            finally {
                //STEP 6: finally block used to close resources
                try {
                    if (getMajorNameByMajorIdResultSet != null) getMajorNameByMajorIdResultSet.close();
                    if (projectListByStudentIdResultSet != null) projectListByStudentIdResultSet.close();
                    if (studentByMajorIdResultSet != null) studentByMajorIdResultSet.close();
                    if (getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                    if (getProjectsByStudentIdPS != null) getProjectsByStudentIdPS.close();
                    if (getStudentsByMajorIdPS != null) getStudentsByMajorIdPS.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close resultSet, statement, preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return students;
        }

        @Override
        public StudentModel getStudentWithAssociatedProjectsByStudentId (Long studentId){
            StudentModel retrievedStudent = null;
            Connection dbConnection = null;
            PreparedStatement getStudentByStudentIdPS = null;
            PreparedStatement getProjectsByStudentIdPS = null;
            PreparedStatement getMajorNameByMajorIdPS = null;
            ResultSet projectListByStudentIdResultSet = null;
            ResultSet getMajorNameByMajorIdResultSet = null;
            ResultSet getStudentByStudentIdResultSet = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                getStudentByStudentIdPS = dbConnection.prepareStatement(SQL_SELECT_STUDENT_BY_ID);
                getStudentByStudentIdPS.setLong(1, studentId);
                getStudentByStudentIdResultSet = getStudentByStudentIdPS.executeQuery();

                //STEP 3: Extract data from result set
                if (getStudentByStudentIdResultSet.next()) {
                    retrievedStudent = convertResultSetToStudentModel(getStudentByStudentIdResultSet);
                }

                //Step 4: Now prepare getProjectsByStudentIdPS and then execute the query using each studentId
                getProjectsByStudentIdPS = dbConnection.prepareStatement(SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                getProjectsByStudentIdPS.setLong(1, retrievedStudent.getId());
                projectListByStudentIdResultSet = getProjectsByStudentIdPS.executeQuery();

                //Step 5: Retrieve all projects by column name and then fill into project list
                List<ProjectModel> projectList = getProjectList(projectListByStudentIdResultSet);

                retrievedStudent.setProjectList(projectList);

                //Step 5: use student's major ID to retrieve the associated majorName
                getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                getMajorNameByMajorIdPS.setLong(1, retrievedStudent.getMajorId());
                getMajorNameByMajorIdResultSet = getMajorNameByMajorIdPS.executeQuery();

                //STEP 6: Extract major name from result set
                String majorName = getMajorNameByResultSet(getMajorNameByMajorIdResultSet);
                if(majorName != null)
                    retrievedStudent.setMajorName(majorName);


            } catch (Exception se) {
                logger.error("SQLException is caught when trying to select a student by studentId. The input studentId ={}, the error={}",
                        studentId, se.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (projectListByStudentIdResultSet != null) projectListByStudentIdResultSet.close();
                    if (getMajorNameByMajorIdResultSet != null) getMajorNameByMajorIdResultSet.close();
                    if (getStudentByStudentIdResultSet != null) getStudentByStudentIdResultSet.close();
                    if (getStudentByStudentIdPS != null) getStudentByStudentIdPS.close();
                    if (getProjectsByStudentIdPS != null) getProjectsByStudentIdPS.close();
                    if (getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close resultSet, preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }

            return retrievedStudent;
        }

        @Override
        public StudentModel getStudentWithAssociatedProjectsByLoginName (String loginName){
            StudentModel retrievedStudent = null;
            Connection dbConnection = null;
            PreparedStatement getStudentByLoginNamePS = null;
            PreparedStatement getProjectsByStudentIdPS = null;
            PreparedStatement getMajorNameByMajorIdPS = null;
            ResultSet projectListByStudentIdResultSet = null;
            ResultSet getMajorNameByMajorIdResultSet = null;
            ResultSet getStudentByLoginNameResultSet = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                getStudentByLoginNamePS = dbConnection.prepareStatement(SQL_SELECT_STUDENT_BY_LOGIN_NAME);
                getStudentByLoginNamePS.setString(1, loginName);
                getStudentByLoginNameResultSet = getStudentByLoginNamePS.executeQuery();

                //STEP 3: Extract data from result set
                if (getStudentByLoginNameResultSet.next()) {
                    retrievedStudent = convertResultSetToStudentModel(getStudentByLoginNameResultSet);
                }

                //Step 4: Now prepare getProjectsByStudentIdPS and then execute the query using each studentId
                getProjectsByStudentIdPS = dbConnection.prepareStatement(SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                getProjectsByStudentIdPS.setLong(1, retrievedStudent.getId());
                projectListByStudentIdResultSet = getProjectsByStudentIdPS.executeQuery();

                //Step 5: Retrieve all projects by column name and then fill into project list
                List<ProjectModel> projectList = getProjectList(projectListByStudentIdResultSet);

                retrievedStudent.setProjectList(projectList);

                //Step 5: use student's major ID to retrieve the associated majorName
                getMajorNameByMajorIdPS = dbConnection.prepareStatement(SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID);
                getMajorNameByMajorIdPS.setLong(1, retrievedStudent.getMajorId());
                getMajorNameByMajorIdResultSet = getMajorNameByMajorIdPS.executeQuery();

                //STEP 6: Extract major name from result set
                String majorName = getMajorNameByResultSet(getMajorNameByMajorIdResultSet);
                if(majorName != null)
                    retrievedStudent.setMajorName(majorName);

            } catch (Exception se) {
                logger.error("SQLException is caught when trying to select a student by loginName. The input loginName ={}, the error={}",
                        loginName, se.getMessage());
            } finally {
                //STEP 4: finally block used to close resources
                try {
                    if (projectListByStudentIdResultSet != null) projectListByStudentIdResultSet.close();
                    if (getMajorNameByMajorIdResultSet != null) getMajorNameByMajorIdResultSet.close();
                    if (getStudentByLoginNameResultSet != null) getStudentByLoginNameResultSet.close();
                    if (getStudentByLoginNamePS != null) getStudentByLoginNamePS.close();
                    if (getProjectsByStudentIdPS != null) getProjectsByStudentIdPS.close();
                    if (getMajorNameByMajorIdPS != null) getMajorNameByMajorIdPS.close();
                    if (dbConnection != null) dbConnection.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close resultSet, preparedStatement or dbConnection, error = " + se.getMessage());
                }
            }
            return retrievedStudent;
        }

        private String getMajorNameByResultSet(ResultSet getMajorNameByMajorIdResultSet) throws SQLException {
            String majorName = null;
            if (getMajorNameByMajorIdResultSet.next()) {
                //Retrieve Student ID by column name
                majorName = getMajorNameByMajorIdResultSet.getString("name");
                logger.info("========== retrieve majorName={}", majorName);
            }
            return majorName;
        }


        private List<ProjectModel> getProjectList(ResultSet projectListByStudentIdResultSet) throws SQLException {
            List<ProjectModel> projectList = new ArrayList<ProjectModel>();
            while (projectListByStudentIdResultSet.next()) {
                //Retrieve by column name
                Long id  = projectListByStudentIdResultSet.getLong("id");
                String name = projectListByStudentIdResultSet.getString("name");
                String description = projectListByStudentIdResultSet.getString("description");
                Timestamp createDate = projectListByStudentIdResultSet.getTimestamp("create_date");

                //Fill the object
                ProjectModel project = new ProjectModel();
                project.setId(id);
                project.setName(name);
                project.setDescription(description);
                project.setCreateDate(createDate);

                projectList.add(project);
            }
            return projectList;
        }

        private StudentModel convertResultSetToStudentModel (ResultSet rs) throws SQLException {
            //Retrieve by column name
            Long id = rs.getLong("id");
            String loginName = rs.getString("login_name");
            String password = rs.getString("password");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String email = rs.getString("email");
            String address = rs.getString("address");
            Timestamp enrolledDate = rs.getTimestamp("enrolled_date");
            Long majorId = rs.getLong("major_id");

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

            return student;
        }

        private Long retrieveStudentIdByLoginName(Connection dbConnection, String loginName) {
            PreparedStatement selectStudentIdByLoginNamePS = null;
            ResultSet rs = null;
            Long studentId = null;
            try {
                selectStudentIdByLoginNamePS = dbConnection.prepareStatement(SQL_SELECT_STUDENT_ID_BY_LOGIN_NAME);
                selectStudentIdByLoginNamePS.setString(1, loginName);
                rs = selectStudentIdByLoginNamePS.executeQuery();

                //SExtract Student ID from result set
                if (rs.next()) {
                    //Retrieve Student ID by column name
                    studentId = rs.getLong("id");
                    logger.info("========== retrieve studentId={} using loginName={}", studentId, loginName);
                }
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to select a StudentId by LoginName. The input LoginName = {}, the error={}", loginName,
                    se.getMessage());
            } finally {
                //Finally block used to close resources
                try {
                    if (rs != null) rs.close();
                    if (selectStudentIdByLoginNamePS != null) selectStudentIdByLoginNamePS.close();
                } catch (SQLException se) {
                    logger.error("SQLException is caught when trying to close ResultSet or selectStudentIdByLoginNamePS, error = " + se.getMessage());
                }
            }
            return studentId;
        }
}
