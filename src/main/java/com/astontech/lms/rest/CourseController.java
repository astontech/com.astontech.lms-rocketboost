package com.astontech.lms.rest;

import com.astontech.lms.domain.Course;
import com.astontech.lms.services.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PutMapping("/additionalMaterials")
    public ResponseEntity<Course> updateAdditionalMaterials(@RequestBody Map<String, Object> request) {
        Course course = courseService.updateAdditionalMaterial((String) request.get("title"), (Integer) request.get("index"), (String) request.get("markdown"));
        return ResponseEntity.ok(course);
    }
}
