package com.devmountain.training.jdbc;

import com.devmountain.training.dao.MajorDao;
import com.devmountain.training.model.MajorModel;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import com.devmountain.training.util.JDBCUtils;
import com.devmountain.training.util.SQLStatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDaoJDBCImpl implements MajorDao {
    private Logger logger = LoggerFactory.getLogger(MajorDaoJDBCImpl.class);

//    private final String SQL_INSERT_MAJOR = "INSERT INTO MAJOR (NAME, DESCRIPTION) VALUES (?,?)";
//    private final String SQL_UPDATE_MAJOR = "UPDATE MAJOR SET name=?, description=? where id=?";
//
//    private final String SQL_DELETE_MAJOR_BY_NAME = "DELETE FROM MAJOR where NAME=?";
//    private final String SQL_DELETE_MAJOR_BY_ID = "DELETE FROM MAJOR where ID=?";
//    private final String SQL_DELETE_STUDENT_BY_ID = "DELETE FROM STUDENT where ID=?";
//    private final String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID = "DELETE FROM STUDENT_PROJECT WHERE STUDENT_ID = ?";
//
//    private final String SQL_SELECT_ALL_MAJORS = "SELECT * FROM MAJOR";
//    private final String SQL_SELECT_MAJOR_BY_ID = "SELECT * FROM MAJOR where id = ?";
//    private final String SQL_SELECT_MAJOR_BY_MAME = "SELECT * FROM MAJOR where name = ?";
//    private final String SQL_SELECT_MAJOR_ID_BY_MAJOR_NAME = "SELECT ID FROM MAJOR where name = ?";
//    private final String SQL_SELECT_STUDENTS_BY_MAJOR_ID = "SELECT * FROM STUDENT where MAJOR_ID = ?";
//    private final String SQL_SELECT_PROJECTS_BY_STUDENT_ID = "SELECT p.* FROM PROJECT p, STUDENT_PROJECT sp where p.ID=sp.PROJECT_ID and sp.STUDENT_ID=?";

    @Override
    public MajorModel save(MajorModel major) {
        MajorModel savedMajor = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for saving a majorModel...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Insert statement...");
//            String SQL_INSERT = "INSERT INTO DEPARTMENT (ID, NAME, DESCRIPTION, LOCATION) VALUES (?,?,?,?)";;
//            String SQL_INSERT = "INSERT INTO MAJOR (NAME, DESCRIPTION) VALUES (?,?)";;
//            preparedStatement = conn.prepareStatement(SQL_INSERT);
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_INSERT_MAJOR, Statement.RETURN_GENERATED_KEYS);
//            preparedStatement.setLong(1, major.getId());
            preparedStatement.setString(1, major.getName());
            preparedStatement.setString(2, major.getDescription());

            int row = preparedStatement.executeUpdate();
            if(row > 0)
                savedMajor = major;

            rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                Long generatedId = rs.getLong(1);
                if(generatedId != null)
                    savedMajor.setId(generatedId);
            }

        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to save a Major. The input major =" + major + ", the error = " + e.getMessage());
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
        return savedMajor;
    }

    @Override
    public MajorModel update(MajorModel major) {
        MajorModel updatedMajor = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for updating a majorModel");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Updating statement...");

//            String SQL_UPDATE = "UPDATE MAJOR SET name=?, description=? where id=?";
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_UPDATE_MAJOR);
            preparedStatement.setString(1, major.getName());
            preparedStatement.setString(2, major.getDescription());
            preparedStatement.setLong(3, major.getId());

            int row = preparedStatement.executeUpdate();
            if(row > 0)
                updatedMajor = major;

        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to update a Major. The input major =" + major + ", the error = " + e.getMessage());
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
        return updatedMajor;
    }

    /**
     * This method handles the operation using Transaction.
     * After the auto-commit mode is disabled, no SQL statements
     * are committed until you call the method commit explicitly.
     * All statements executed after the previous call to the method
     * commit are included in the current transaction and committed
     * together as a unit.
     * @param majorName
     * @return
     */
    @Override
    public boolean deleteByName(String majorName) {
        boolean isMajorDeleted = false;
        Connection dbConnection = null;

        PreparedStatement selectMajorIdByMajorNamePS = null;
        PreparedStatement selectStudentsByMajorIdPS = null;
        PreparedStatement deleteStudentProjectRelationshipByStudentIdPS = null;
        PreparedStatement deleteStudentByStudentIdPS = null;
        PreparedStatement deleteMajorByIdPS = null;

        ResultSet getMajorIdByMajorNameResultSet = null;
        ResultSet getStudentsByMajorIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: select major ID by major Name
            selectMajorIdByMajorNamePS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_ID_BY_MAJOR_NAME);
            selectMajorIdByMajorNamePS.setString(1, majorName);
            getMajorIdByMajorNameResultSet = selectMajorIdByMajorNamePS.executeQuery();

            Long majorId = null;
            if (getMajorIdByMajorNameResultSet.next()) {
                majorId = getMajorIdByMajorNameResultSet.getLong("ID");
            }
            if(majorId == null) {
                logger.error("Cannot find the Major using the majorName={}",  majorName);
                return isMajorDeleted;
            }

            //Step 3: select students by major ID
            selectStudentsByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_STUDENTS_BY_MAJOR_ID);
            selectStudentsByMajorIdPS.setLong(1, majorId);
            getStudentsByMajorIdResultSet = selectStudentsByMajorIdPS.executeQuery();

            List<StudentModel> studentList = getStudentListFromResultSet(getStudentsByMajorIdResultSet);

            //Step 4: auto-commit is disabled
            dbConnection.setAutoCommit(false);

            //Step 5: Loop through the studentList and use each student ID to delete
            //        STUDENT/PROJECT relationship in STUDENT_PROJECT table
            deleteStudentProjectRelationshipByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID);
            for(StudentModel eachStudent : studentList) {
                deleteStudentProjectRelationshipByStudentId(deleteStudentProjectRelationshipByStudentIdPS, eachStudent.getId());
            }

            //Step 6: Now loop through the studentList and delete each student in STUDENT table
            deleteStudentByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_STUDENT_BY_ID);
            for(StudentModel eachStudent : studentList) {
                boolean deleteSuccessfulFlag = deleteStudentByStudentId(deleteStudentByStudentIdPS, eachStudent.getId());
                if (!deleteSuccessfulFlag) {
                    dbConnection.rollback();
                    return isMajorDeleted;
                }
            }

            //Step 7: finally, delete the major using major ID
            logger.debug("Deleting a majorModel by majorName statement...");

            deleteMajorByIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_MAJOR_BY_ID);
            deleteMajorByIdPS.setLong(1, majorId);

            int row = deleteMajorByIdPS.executeUpdate();
            if(row > 0) {
                isMajorDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }

        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to delete a Major by majorName. The input majorName =" + majorName + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 8: finally block used to close resources
            try {
                if(getMajorIdByMajorNameResultSet != null) getMajorIdByMajorNameResultSet.close();
                if(getStudentsByMajorIdResultSet != null) getStudentsByMajorIdResultSet.close();
                if(selectMajorIdByMajorNamePS != null) selectMajorIdByMajorNamePS.close();
                if(selectStudentsByMajorIdPS != null) selectStudentsByMajorIdPS.close();
                if(deleteStudentProjectRelationshipByStudentIdPS != null) deleteStudentProjectRelationshipByStudentIdPS.close();
                if(deleteStudentByStudentIdPS != null) deleteStudentByStudentIdPS.close();
                if(deleteMajorByIdPS != null) deleteMajorByIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close resultSet, preparedStatement and dbConnection, error = " + se.getMessage());
            }
        }
        return isMajorDeleted;
    }

    private boolean deleteStudentByStudentId(PreparedStatement preparedStatement, Long studentId) {
        boolean isDeleteSuccessful = false;
        try {
            preparedStatement.setLong(1, studentId);

            //Execute a query
            logger.debug("Deleting Student by studentId statement...");

            int row = preparedStatement.executeUpdate();
            if(row > 0)
                isDeleteSuccessful = true;
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete a Student by studentId. The input studentId ={}, the error={}",
                    studentId, se.getMessage());
        }
        return isDeleteSuccessful;
    }

    private void deleteStudentProjectRelationshipByStudentId(PreparedStatement preparedStatement, Long studentId) {
//        boolean isDeleteSuccessful = false;
        try {
            preparedStatement.setLong(1, studentId);

            //Execute a query
            logger.debug("Deleting all StudentId/projectId relationships by studentId statement...");

            preparedStatement.executeUpdate();
//            if(row > 0)
//                isDeleteSuccessful = true;
        } catch (SQLException se) {
            logger.error("SQLException is caught when trying to delete all Student.project relationship by studentId. The input studentId ={}, the error={}",
                    studentId, se.getMessage());
        }
//        return isDeleteSuccessful;
    }


    private List<StudentModel> getStudentListFromResultSet(ResultSet getStudentsByMajorIdResultSet) throws SQLException {
        List<StudentModel> studentList = new ArrayList<StudentModel>();
        while (getStudentsByMajorIdResultSet.next()) {
            //Retrieve by column name
            Long id = getStudentsByMajorIdResultSet.getLong("id");
            String loginName = getStudentsByMajorIdResultSet.getString("login_name");
            String password = getStudentsByMajorIdResultSet.getString("password");
            String firstName = getStudentsByMajorIdResultSet.getString("first_name");
            String lastName = getStudentsByMajorIdResultSet.getString("last_name");
            String email = getStudentsByMajorIdResultSet.getString("email");
            String address = getStudentsByMajorIdResultSet.getString("address");
            Timestamp enrolledDate = getStudentsByMajorIdResultSet.getTimestamp("enrolled_date");
            Long majorId = getStudentsByMajorIdResultSet.getLong("major_id");

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
    public boolean deleteById(Long majorId) {
        boolean isMajorDeleted = false;
        Connection dbConnection = null;

//        PreparedStatement selectMajorIdByMajorNamePS = null;
        PreparedStatement selectStudentsByMajorIdPS = null;
        PreparedStatement deleteStudentProjectRelationshipByStudentIdPS = null;
        PreparedStatement deleteStudentByStudentIdPS = null;
        PreparedStatement deleteMajorByIdPS = null;

        ResultSet getMajorIdByMajorNameResultSet = null;
        ResultSet getStudentsByMajorIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: select students by major ID
            selectStudentsByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_STUDENTS_BY_MAJOR_ID);
            selectStudentsByMajorIdPS.setLong(1, majorId);
            getStudentsByMajorIdResultSet = selectStudentsByMajorIdPS.executeQuery();

            List<StudentModel> studentList = getStudentListFromResultSet(getStudentsByMajorIdResultSet);

            //Step 3: auto-commit is disabled
            dbConnection.setAutoCommit(false);

            //Step 4: Loop through the studentList and use each student ID to delete
            //        STUDENT/PROJECT relationship in STUDENT_PROJECT table
            deleteStudentProjectRelationshipByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID);
            for(StudentModel eachStudent : studentList) {
                deleteStudentProjectRelationshipByStudentId(deleteStudentProjectRelationshipByStudentIdPS, eachStudent.getId());
            }

            //Step 5: Now loop through the studentList and delete each student in STUDENT table
            deleteStudentByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_STUDENT_BY_ID);
            for(StudentModel eachStudent : studentList) {
                boolean deleteSuccessfulFlag = deleteStudentByStudentId(deleteStudentByStudentIdPS, eachStudent.getId());
                if (!deleteSuccessfulFlag) {
                    dbConnection.rollback();
                    return isMajorDeleted;
                }
            }

            //Step 6: finally, delete the major using major ID
            logger.debug("Deleting a majorModel by majorName statement...");

            deleteMajorByIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_MAJOR_BY_ID);
            deleteMajorByIdPS.setLong(1, majorId);

            int row = deleteMajorByIdPS.executeUpdate();
            if(row > 0) {
                isMajorDeleted = true;
                dbConnection.commit();
            } else {
                dbConnection.rollback();
            }

        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to delete a Major by major ID. The input majorId ={}, the error={}",
                    majorId, e.getMessage());
        }
        finally {
            //STEP 7: finally block used to close resources
            try {
                if(getMajorIdByMajorNameResultSet != null) getMajorIdByMajorNameResultSet.close();
                if(getStudentsByMajorIdResultSet != null) getStudentsByMajorIdResultSet.close();
                if(selectStudentsByMajorIdPS != null) selectStudentsByMajorIdPS.close();
                if(deleteStudentProjectRelationshipByStudentIdPS != null) deleteStudentProjectRelationshipByStudentIdPS.close();
                if(deleteStudentByStudentIdPS != null) deleteStudentByStudentIdPS.close();
                if(deleteMajorByIdPS != null) deleteMajorByIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close resultSet, preparedStatement and dbConnection, error = " + se.getMessage());
            }
        }
        return isMajorDeleted;
    }

    @Override
    public boolean delete(MajorModel major) {
        Long majorId = major.getId();
        return deleteById(majorId);
    }

    @Override
    public List<MajorModel> getMajors() {
        List<MajorModel> majors = new ArrayList<MajorModel>();
        Connection dbConnection = null;
        Statement getAllMajorsStatement = null;
        ResultSet getAllMajorsResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            getAllMajorsStatement = dbConnection.createStatement();
            getAllMajorsResultSet = getAllMajorsStatement.executeQuery(SQLStatementUtils.SQL_SELECT_ALL_MAJORS);

            //STEP 3: Extract data from result set
            while(getAllMajorsResultSet.next()) {
                //Retrieve by column name
                Long id  = getAllMajorsResultSet.getLong("id");
                String name = getAllMajorsResultSet.getString("name");
                String description = getAllMajorsResultSet.getString("description");

                //Fill the object
                MajorModel major = new MajorModel();
                major.setId(id);
                major.setName(name);
                major.setDescription(description);

                majors.add(major);
            }
        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to select all Majors without the associated students. the error = " + e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if (getAllMajorsResultSet != null) getAllMajorsResultSet.close();
                if (getAllMajorsStatement != null) getAllMajorsStatement.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet, Statement, and dbConnection, error = " + se.getMessage());
            }
        }
        return majors;
    }

    @Override
    public MajorModel getMajorById(Long id) {
            MajorModel retrievedMajor = null;
            Connection dbConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;

            try {
                //STEP 1: Open a connection
                logger.info("Connecting to database...");
                dbConnection = JDBCUtils.getConnection();

                //STEP 2: Execute a query
                logger.debug("Creating statement...");
                preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_BY_ID);
                preparedStatement.setLong(1, id);
                rs = preparedStatement.executeQuery();

                //STEP 3: Extract data from result set
                if(rs.next()) {
                    //Retrieve by column name
                    Long deptId  = rs.getLong("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");

                    //Fill the object
                    retrievedMajor = new MajorModel();
                    retrievedMajor.setId(deptId);
                    retrievedMajor.setName(name);
                    retrievedMajor.setDescription(description);
                }
            }
            catch(Exception e){
                logger.error("SQLException is caught when trying to select a Major by majorId. The input majorId =" + id + ", the error = " + e.getMessage());
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

            return retrievedMajor;
    }

    @Override
    public MajorModel getMajorByName(String majorName) {
        MajorModel retrievedMajor = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_BY_MAME);
            preparedStatement.setString(1, majorName);
            rs = preparedStatement.executeQuery();

            //STEP 3: Extract data from result set
            if(rs.next()) {
                //Retrieve by column name
                Long deptId  = rs.getLong("id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                //Fill the object
                retrievedMajor = new MajorModel();
                retrievedMajor.setId(deptId);
                retrievedMajor.setName(name);
                retrievedMajor.setDescription(description);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a Major by majorId. The input majorName =" + majorName + ", the error = " + e.getMessage());
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

        return retrievedMajor;
    }

    @Override
    public List<MajorModel> getMajorsWithChildren() {
        List<MajorModel> majors = new ArrayList<MajorModel>();
        Connection dbConnection = null;
        Statement getAllMajorsStatement = null;
        PreparedStatement getStudentListByMajorIdPS = null;
        PreparedStatement getAssociatedProjectListByStudentIdPS = null;
        ResultSet getAllMajorsResultSet = null;
        ResultSet getStudentListByMajorIdResultSet = null;
        ResultSet getAssociatedProjectListByStudentIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            getAllMajorsStatement = dbConnection.createStatement();
            getAllMajorsResultSet = getAllMajorsStatement.executeQuery(SQLStatementUtils.SQL_SELECT_ALL_MAJORS);

            //STEP 3: Extract data from result set
            while(getAllMajorsResultSet.next()) {
                //Retrieve by column name
                Long id  = getAllMajorsResultSet.getLong("id");
                String name = getAllMajorsResultSet.getString("name");
                String description = getAllMajorsResultSet.getString("description");

                //Fill the object
                MajorModel major = new MajorModel();
                major.setId(id);
                major.setName(name);
                major.setDescription(description);

                //Step 4: use each Major ID to retrieve the associated student list
                getStudentListByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_STUDENTS_BY_MAJOR_ID);
                getStudentListByMajorIdPS.setLong(1, major.getId());
                getStudentListByMajorIdResultSet = getStudentListByMajorIdPS.executeQuery();

                List<StudentModel> studentList = getStudentListFromResultSet(getStudentListByMajorIdResultSet);

                //Step 5: loop through the retrieved student list, use each student ID to select the associated project list if
                // there is a student/project relationship exists
                for(StudentModel eachStudent : studentList) {
                    //First set major name value for each Student
                    eachStudent.setMajorName(major.getName());
                    //Now to retrieve the potential associated project list
                    getAssociatedProjectListByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                    getAssociatedProjectListByStudentIdPS.setLong(1, eachStudent.getId());
                    getAssociatedProjectListByStudentIdResultSet = getAssociatedProjectListByStudentIdPS.executeQuery();

                    //Step 5: Retrieve all projects by column name and then fill into project list
                    List<ProjectModel> projectList = getProjectList(getAssociatedProjectListByStudentIdResultSet);

                    eachStudent.setProjectList(projectList);
                }

                major.setStudentList(studentList);
                majors.add(major);
            }
        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to select all Majors with the associated students. the error = " + e.getMessage());
        }
        finally {
            //STEP 6: finally block used to close resources
            try {
                if (getAllMajorsResultSet != null) getAllMajorsResultSet.close();
                if (getStudentListByMajorIdResultSet != null) getStudentListByMajorIdResultSet.close();
                if (getAssociatedProjectListByStudentIdResultSet != null) getAssociatedProjectListByStudentIdResultSet.close();
                if (getAllMajorsStatement != null) getAllMajorsStatement.close();
                if (getStudentListByMajorIdPS != null) getStudentListByMajorIdPS.close();
                if (getAssociatedProjectListByStudentIdPS != null) getAssociatedProjectListByStudentIdPS.close();
                if (dbConnection != null) dbConnection.close();
            } catch (SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet, Statement, preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return majors;
    }

    @Override
    public MajorModel getMajorAndStudentsAndProjectsByMajorId(Long majorId) {
        MajorModel retrievedMajor = null;
        Connection dbConnection = null;
        PreparedStatement getMajorByMajorIdPS = null;
        PreparedStatement getStudentListByMajorIdPS = null;
        PreparedStatement getAssociatedProjectListByStudentIdPS = null;
        ResultSet getMajorByMajorIdResultSet = null;
        ResultSet getStudentListByMajorIdResultSet = null;
        ResultSet getAssociatedProjectListByStudentIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            getMajorByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_BY_ID);
            getMajorByMajorIdPS.setLong(1, majorId);
            getMajorByMajorIdResultSet = getMajorByMajorIdPS.executeQuery();

            //STEP 3: Extract data from result set
            if(getMajorByMajorIdResultSet.next()) {
                //Retrieve by column name
                Long deptId  = getMajorByMajorIdResultSet.getLong("id");
                String name = getMajorByMajorIdResultSet.getString("name");
                String description = getMajorByMajorIdResultSet.getString("description");

                //Fill the object
                retrievedMajor = new MajorModel();
                retrievedMajor.setId(deptId);
                retrievedMajor.setName(name);
                retrievedMajor.setDescription(description);

                //Step 4: use each Major ID to retrieve the associated student list
                getStudentListByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_STUDENTS_BY_MAJOR_ID);
                getStudentListByMajorIdPS.setLong(1, retrievedMajor.getId());
                getStudentListByMajorIdResultSet = getStudentListByMajorIdPS.executeQuery();

                List<StudentModel> studentList = getStudentListFromResultSet(getStudentListByMajorIdResultSet);

                //Step 5: loop through the retrieved student list, use each student ID to select the associated project list if
                // there is a student/project relationship exists
                for(StudentModel eachStudent : studentList) {
                    //First set major name value for each Student
                    eachStudent.setMajorName(retrievedMajor.getName());
                    //Now to retrieve the potential associated project list
                    getAssociatedProjectListByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                    getAssociatedProjectListByStudentIdPS.setLong(1, eachStudent.getId());
                    getAssociatedProjectListByStudentIdResultSet = getAssociatedProjectListByStudentIdPS.executeQuery();

                    //Step 5: Retrieve all projects by column name and then fill into project list
                    List<ProjectModel> projectList = getProjectList(getAssociatedProjectListByStudentIdResultSet);

                    eachStudent.setProjectList(projectList);
                }

                retrievedMajor.setStudentList(studentList);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a Major by majorId. The input majorId ={}, the error={}",
                    majorId, e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(getMajorByMajorIdResultSet != null) getMajorByMajorIdResultSet.close();
                if(getStudentListByMajorIdResultSet != null) getStudentListByMajorIdResultSet.close();
                if(getAssociatedProjectListByStudentIdResultSet != null) getAssociatedProjectListByStudentIdResultSet.close();
                if(getMajorByMajorIdPS != null) getMajorByMajorIdPS.close();
                if(getStudentListByMajorIdPS != null) getStudentListByMajorIdPS.close();
                if(getAssociatedProjectListByStudentIdPS != null) getAssociatedProjectListByStudentIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close resultSet, preparedStatement and dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedMajor;
    }

    @Override
    public MajorModel getMajorAndStudentsAndProjectsByMajorName(String majorName) {
        MajorModel retrievedMajor = null;
        Connection dbConnection = null;
        PreparedStatement getMajorByMajorNamePS = null;
        PreparedStatement getStudentListByMajorIdPS = null;
        PreparedStatement getAssociatedProjectListByStudentIdPS = null;
        ResultSet getMajorByMajorNameResultSet = null;
        ResultSet getStudentListByMajorIdResultSet = null;
        ResultSet getAssociatedProjectListByStudentIdResultSet = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: Execute a query
            logger.debug("Creating statement...");
            getMajorByMajorNamePS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_MAJOR_BY_MAME);
            getMajorByMajorNamePS.setString(1, majorName);
            getMajorByMajorNameResultSet = getMajorByMajorNamePS.executeQuery();

            //STEP 3: Extract data from result set
            if(getMajorByMajorNameResultSet.next()) {
                //Retrieve by column name
                Long deptId  = getMajorByMajorNameResultSet.getLong("id");
                String name = getMajorByMajorNameResultSet.getString("name");
                String description = getMajorByMajorNameResultSet.getString("description");

                //Fill the object
                retrievedMajor = new MajorModel();
                retrievedMajor.setId(deptId);
                retrievedMajor.setName(name);
                retrievedMajor.setDescription(description);

                //Step 4: use each Major ID to retrieve the associated student list
                getStudentListByMajorIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_STUDENTS_BY_MAJOR_ID);
                getStudentListByMajorIdPS.setLong(1, retrievedMajor.getId());
                getStudentListByMajorIdResultSet = getStudentListByMajorIdPS.executeQuery();

                List<StudentModel> studentList = getStudentListFromResultSet(getStudentListByMajorIdResultSet);

                //Step 5: loop through the retrieved student list, use each student ID to select the associated project list if
                // there is a student/project relationship exists
                for(StudentModel eachStudent : studentList) {
                    //First set major name value for each Student
                    eachStudent.setMajorName(retrievedMajor.getName());
                    //Now to retrieve the potential associated project list
                    getAssociatedProjectListByStudentIdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_SELECT_PROJECTS_BY_STUDENT_ID);
                    getAssociatedProjectListByStudentIdPS.setLong(1, eachStudent.getId());
                    getAssociatedProjectListByStudentIdResultSet = getAssociatedProjectListByStudentIdPS.executeQuery();

                    //Step 5: Retrieve all projects by column name and then fill into project list
                    List<ProjectModel> projectList = getProjectList(getAssociatedProjectListByStudentIdResultSet);

                    eachStudent.setProjectList(projectList);
                }

                retrievedMajor.setStudentList(studentList);
            }
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to select a Major by majorId. The input majorName =" + majorName + ", the error = " + e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(getMajorByMajorNameResultSet != null) getMajorByMajorNameResultSet.close();
                if(getStudentListByMajorIdResultSet != null) getStudentListByMajorIdResultSet.close();
                if(getAssociatedProjectListByStudentIdResultSet != null) getAssociatedProjectListByStudentIdResultSet.close();
                if(getMajorByMajorNamePS != null) getMajorByMajorNamePS.close();
                if(getStudentListByMajorIdPS != null) getStudentListByMajorIdPS.close();
                if(getAssociatedProjectListByStudentIdPS != null) getAssociatedProjectListByStudentIdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close ResultSet, preparedStatement and dbConnection, error = " + se.getMessage());
            }
        }

        return retrievedMajor;
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


}
