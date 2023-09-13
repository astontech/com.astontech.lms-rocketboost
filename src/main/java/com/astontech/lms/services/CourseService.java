package com.astontech.lms.services;

import com.astontech.lms.domain.Course;

public interface CourseService {

    Course save(Course course);
    Course updateAdditionalMaterial(String title, int index, String markdown);
}
