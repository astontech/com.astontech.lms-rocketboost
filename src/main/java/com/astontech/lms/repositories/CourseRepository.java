package com.astontech.lms.repositories;

import com.astontech.lms.domain.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findByTitle(String title);
}
