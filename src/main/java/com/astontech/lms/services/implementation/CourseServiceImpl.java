package com.astontech.lms.services.implementation;

import com.astontech.lms.domain.Course;
import com.astontech.lms.repositories.CourseRepository;
import com.astontech.lms.services.CourseService;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    final private CourseRepository courseRepository;
    final private MongoTemplate mongoTemplate;

    public CourseServiceImpl(CourseRepository courseRepository, MongoTemplate mongoTemplate) {
        this.courseRepository = courseRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateAdditionalMaterial(String title, int index, String markdown) {
        Query query = new Query().addCriteria(Criteria.where("title").is(title));
        Update update = new Update().set(String.format("content.%s.materialsMarkdown", index), markdown);
        UpdateResult result = mongoTemplate.updateFirst(query, update, Course.class);
        log.info(String.format("Modified Count = %s", result.getModifiedCount()));
        return courseRepository.findByTitle(title).orElse(null);
    }
}
