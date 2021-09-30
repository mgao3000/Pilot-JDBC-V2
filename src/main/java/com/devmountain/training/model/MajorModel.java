package com.devmountain.training.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MajorModel {

    private Long id;

    private String name;

    private String description;

    private List<StudentModel> studentList = new ArrayList<StudentModel>();

    public MajorModel() {
    }

    public MajorModel(String name, String description) {
        this.name = name;
        this.description = description;
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

    public List<StudentModel> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentModel> studentList) {
        this.studentList = studentList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MajorModel)) return false;
        MajorModel that = (MajorModel) o;
        return getName().equals(that.getName()) && getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription());
    }

    @Override
    public String toString() {
        return "MajorModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
