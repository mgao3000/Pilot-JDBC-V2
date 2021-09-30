package com.devmountain.training.util;

public class SQLStatementUtils {
    /*
     *  SQL statements for StudentProjectDaoJDBCImpl.class
     */
    public final static String SQL_DELETE_STUDENT_PROJECT_RELATIONSHIP_BY_BOTH_IDS = "DELETE FROM STUDENT_PROJECT where STUDENT_ID = ? AND PROJECT_ID = ?";
    public final static String SQL_INSERT_STUDENT_PROJECT_RELATIONSHIP_BY_BOTH_IDS = "INSERT INTO STUDENT_PROJECT (STUDENT_ID, PROJECT_ID) VALUES (?, ?)";;

    /*
     *  SQL statements for StudentDaoJDBCImpl.class
     */
    public final static String SQL_INSERT_STUDENT = "INSERT INTO STUDENT (login_name, password, first_name, last_name, email, address, enrolled_DATE, major_id) VALUES (?, ?, ?,?,?,?,?,?)";

    public final static String SQL_UPDATE_STUDENT = "UPDATE STUDENT SET login_name=?, password=?, email=?, address=? where id=?";

    public final static String SQL_DELETE_STUDENT_BY_LOGIN_NAME = "DELETE FROM STUDENT where LOGIN_NAME=?";
    public final static String SQL_DELETE_STUDENT_BY_ID = "DELETE FROM STUDENT where ID=?";
    public final static String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID = "DELETE from STUDENT_PROJECT WHERE STUDENT_ID = ?";

    public final static String SQL_SELECT_ALL_STUDENTS = "SELECT * FROM STUDENT";
    public final static String SQL_SELECT_STUDENTS_BY_MAJOR_ID = "SELECT * FROM STUDENT where major_id = ?";
    public final static String SQL_SELECT_STUDENT_BY_ID = "SELECT * FROM STUDENT where ID = ?";
    public final static String SQL_SELECT_STUDENT_BY_LOGIN_NAME = "SELECT * FROM STUDENT where login_name = ?";
    public final static String SQL_SELECT_STUDENT_ID_BY_LOGIN_NAME = "SELECT ID FROM STUDENT where login_name = ?";
    public final static String SQL_SELECT_PROJECTS_BY_STUDENT_ID = "SELECT p.* FROM PROJECT p, STUDENT_PROJECT sp where p.ID=sp.PROJECT_ID and sp.STUDENT_ID=?";


    /*
     *  SQL statements for ProjectDaoJDBCImpl.class
     */
    public final static String SQL_INSERT_PROJECT = "INSERT INTO PROJECT (NAME, DESCRIPTION, CREATE_DATE) VALUES (?, ?, ?)";
    public final static String SQL_UPDATE_PROJECT = "UPDATE PROJECT SET name=?, description=? , create_date=? where id=?";

    public final static String SELECT_ALL_PROJECT = "SELECT * FROM PROJECT";
    public final static String SQL_SELECT_PROJECT_ID_BY_NAME = "SELECT ID FROM PROJECT WHERE NAME=?";
    public final static String SQL_SELECT_PROJECT_BY_ID = "SELECT * FROM PROJECT where ID = ?";
    public final static String SQL_SELECT_PROJECT_BY_NAME = "SELECT * FROM PROJECT where name = ?";
    public final static String SELECT_STUDENTS_BY_PROJECT_ID = "SELECT s.* FROM STUDENT s, STUDENT_PROJECT sp where s.ID=sp.STUDENT_ID and sp.PROJECT_ID=?";
//    private final String SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID = "SELECT NAME from MAJOR WHERE ID = ?";

    public final static String SQL_DELETE_PROJECT_BY_ID = "DELETE FROM PROJECT where ID=?";
    public final static String SQL_DELETE_PROJECT_BY_NAME = "DELETE FROM PROJECT where NAME=?";
    public final static String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_PROJECT_ID = "DELETE from STUDENT_PROJECT WHERE PROJECT_ID = ?";


    /*
     *  SQL statements for MajorDaoJDBCImpl.class
     */
    public final static String SQL_INSERT_MAJOR = "INSERT INTO MAJOR (NAME, DESCRIPTION) VALUES (?,?)";
    public final static String SQL_UPDATE_MAJOR = "UPDATE MAJOR SET name=?, description=? where id=?";

    public final static String SQL_DELETE_MAJOR_BY_NAME = "DELETE FROM MAJOR where NAME=?";
    public final static String SQL_DELETE_MAJOR_BY_ID = "DELETE FROM MAJOR where ID=?";
//    public final static String SQL_DELETE_ALL_STUDENT_AND_PROJECT_IDS_BY_STUDENT_ID = "DELETE FROM STUDENT_PROJECT WHERE STUDENT_ID = ?";

    public final static String SQL_SELECT_MAJOR_NAME_BY_MAJOR_ID = "SELECT NAME from MAJOR WHERE ID = ?";
    public final static String SQL_SELECT_ALL_MAJORS = "SELECT * FROM MAJOR";
    public final static String SQL_SELECT_MAJOR_BY_ID = "SELECT * FROM MAJOR where id = ?";
    public final static String SQL_SELECT_MAJOR_BY_MAME = "SELECT * FROM MAJOR where name = ?";
    public final static String SQL_SELECT_MAJOR_ID_BY_MAJOR_NAME = "SELECT ID FROM MAJOR where name = ?";
//    public final static String SQL_SELECT_STUDENTS_BY_MAJOR_ID = "SELECT * FROM STUDENT where MAJOR_ID = ?";
//    public final static String SQL_SELECT_PROJECTS_BY_STUDENT_ID = "SELECT p.* FROM PROJECT p, STUDENT_PROJECT sp where p.ID=sp.PROJECT_ID and sp.STUDENT_ID=?";

}
