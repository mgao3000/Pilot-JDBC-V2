package com.devmountain.training.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentModel {
    private Long id;

    private String loginName;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private LocalDate enrolledDate;

    private Long majorId;

    private String majorName;

    private List<ProjectModel> projectList = new ArrayList<ProjectModel>();

    public StudentModel() {
    }

    public StudentModel(String loginName, String password, String firstName,
                        String lastName, String email, String address,
                        LocalDate enrolledDate, Long majorId) {
        this.loginName = loginName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.enrolledDate = enrolledDate;
        this.majorId = majorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getEnrolledDate() {
        return enrolledDate;
    }

    public Timestamp getEnrolledDateInTimestamp() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(enrolledDate, LocalTime.MIDNIGHT));
        return timestamp;
    }

    public void setEnrolledDate(LocalDate enrolledDate) {
        this.enrolledDate = enrolledDate;
    }

    public void setEnrolledDate(Timestamp enrolledDate) {
        this.enrolledDate = enrolledDate.toLocalDateTime().toLocalDate();
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public List<ProjectModel> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectModel> projectList) {
        this.projectList = projectList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentModel)) return false;
        StudentModel that = (StudentModel) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getLoginName(),
                that.getLoginName()) && Objects.equals(getPassword(),
                that.getPassword()) && Objects.equals(getFirstName(),
                that.getFirstName()) && Objects.equals(getLastName(),
                that.getLastName()) && Objects.equals(getEmail(),
                that.getEmail()) && Objects.equals(getAddress(),
                that.getAddress()) && Objects.equals(getEnrolledDate(),
                that.getEnrolledDate()) && Objects.equals(getMajorId(),
                that.getMajorId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLoginName(), getPassword(), getFirstName(),
                getLastName(), getEmail(), getAddress(), getEnrolledDate(), getMajorId());
    }

    @Override
    public String toString() {
        return "StudentModel{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", enrolledDate=" + enrolledDate +
                ", majorName=" + majorName +
                ", majorId=" + majorId +
                '}';
    }
}
