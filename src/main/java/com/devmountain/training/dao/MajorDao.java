package com.devmountain.training.dao;

import com.devmountain.training.model.MajorModel;

import java.util.List;

public interface MajorDao {
    MajorModel save(MajorModel major);
    MajorModel update(MajorModel major);
    boolean deleteByName(String majorName);
    boolean deleteById(Long majorId);
    boolean delete(MajorModel major);
    List<MajorModel> getMajors();
    MajorModel getMajorById(Long id);
    MajorModel getMajorByName(String majorName);

    List<MajorModel> getMajorsWithChildren();
    MajorModel getMajorAndStudentsAndProjectsByMajorId(Long majorId);
    MajorModel getMajorAndStudentsAndProjectsByMajorName(String majorName);

}
