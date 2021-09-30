package com.devmountain.training.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectModel {

    private Long id;

    private String name;

    private String description;

    private LocalDate createDate;

    private List<StudentModel> studentList = new ArrayList<StudentModel>();

    public ProjectModel() {
    }

    public ProjectModel(String name, String description) {
        this.name = name;
        this.description = description;
        this.createDate = LocalDate.now();
    }

    public ProjectModel(String name, String description, LocalDate createDate) {
        this.name = name;
        this.description = description;
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public Timestamp getCreateDateInTimestamp() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(createDate, LocalTime.MIDNIGHT));
        return timestamp;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate.toLocalDateTime().toLocalDate();
    }

    public List<StudentModel> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentModel> studentList) {
        this.studentList = studentList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectModel)) return false;
        ProjectModel that = (ProjectModel) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(),
                that.getName()) && Objects.equals(getDescription(),
                that.getDescription()) && Objects.equals(getCreateDate(), that.getCreateDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getCreateDate());
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
