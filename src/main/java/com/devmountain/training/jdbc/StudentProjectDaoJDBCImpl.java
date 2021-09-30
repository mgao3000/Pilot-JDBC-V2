package com.devmountain.training.jdbc;

import com.devmountain.training.dao.StudentProjectDao;
import com.devmountain.training.model.ProjectModel;
import com.devmountain.training.model.StudentModel;
import com.devmountain.training.util.JDBCUtils;
import com.devmountain.training.util.SQLStatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class StudentProjectDaoJDBCImpl implements StudentProjectDao {
    private Logger logger = LoggerFactory.getLogger(StudentProjectDaoJDBCImpl.class);

    @Override
    public boolean deleteStudentProjectRelationship(StudentModel student, ProjectModel project) {
        return deleteStudentProjectRelationship(student.getId(), project.getId());
    }

    @Override
    public boolean deleteStudentProjectRelationship(long studentId, long projectId) {
        boolean isStudentProjectRelationshipDeleted = false;
        Connection dbConnection = null;
        PreparedStatement deleteStudentProjectRelationshipByStudentAndProjectsIsdPS = null;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database...");
            dbConnection = JDBCUtils.getConnection();

            //Step 2: prepare deleteStudentProjectRelationshipByStudentAndProjectsIsdPS
//            String SQL_DELETE_STUDENT_PROJECT_RELATIONSHIP_BY_BOTH_IDS = "DELETE FROM STUDENT_PROJECT where STUDENT_ID = ? AND PROJECT_ID = ?";
            deleteStudentProjectRelationshipByStudentAndProjectsIsdPS = dbConnection.prepareStatement(SQLStatementUtils.SQL_DELETE_STUDENT_PROJECT_RELATIONSHIP_BY_BOTH_IDS);
            deleteStudentProjectRelationshipByStudentAndProjectsIsdPS.setLong(1, studentId);
            deleteStudentProjectRelationshipByStudentAndProjectsIsdPS.setLong(2, projectId);

            //STEP 3: Execute a query
            logger.debug("Deleting a student/project relationship by studentId and projectId statement...");

            int row = deleteStudentProjectRelationshipByStudentAndProjectsIsdPS.executeUpdate();
            if(row > 0)
                isStudentProjectRelationshipDeleted = true;
        }
        catch(Exception e){
            logger.error("SQLException is caught when trying to delete a Student/Project relationship by StudentId and ProjectId. " +
                            "The input StudentId ={}, ProjectId ={}, the error={}", studentId, projectId, e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(deleteStudentProjectRelationshipByStudentAndProjectsIsdPS != null) deleteStudentProjectRelationshipByStudentAndProjectsIsdPS.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close deleteStudentProjectRelationshipByStudentAndProjectsIsdPS or dbConnection, error = " + se.getMessage());
            }
        }
        return isStudentProjectRelationshipDeleted;
    }

    @Override
    public boolean addStudentProjectRelationship(StudentModel student, ProjectModel project) {
        return addStudentProjectRelationship(student.getId(), project.getId());
    }

    @Override
    public boolean addStudentProjectRelationship(long studentId, long projectId) {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        boolean isSuccessfulSaved = false;

        try {
            //STEP 1: Open a connection
            logger.info("Connecting to database for saving a student/project relationship ...");
            dbConnection = JDBCUtils.getConnection();

            //STEP 2: prepare PreparedStatement
            logger.debug("Insert statement...");
//            String SQL_INSERT = "INSERT INTO STUDENT_PROJECT (STUDENT_ID, PROJECT_ID) VALUES (?, ?)";;
//            preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
            preparedStatement = dbConnection.prepareStatement(SQLStatementUtils.SQL_INSERT_STUDENT_PROJECT_RELATIONSHIP_BY_BOTH_IDS, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, studentId);
            preparedStatement.setLong(2, projectId);

            //STEP 3: Execute a query
            int row = preparedStatement.executeUpdate();
            if(row > 0)
                isSuccessfulSaved = true;
        }
        catch(SQLException e){
            logger.error("SQLException is caught when trying to insert student/project relationship. " +
                    "The input studentId ={}, projectId ={}, , the error = {}", studentId, projectId,  e.getMessage());
        }
        finally {
            //STEP 4: finally block used to close resources
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            }
            catch(SQLException se) {
                logger.error("SQLException is caught when trying to close preparedStatement or dbConnection, error = " + se.getMessage());
            }
        }
        return isSuccessfulSaved;
    }
}
