package com.astontech.lms.services.implementation;

import com.astontech.lms.domain.Course;
import com.astontech.lms.domain.Path;
import com.astontech.lms.domain.PathDTO;
import com.astontech.lms.repositories.CourseRepository;
import com.astontech.lms.repositories.PathRepository;
import com.astontech.lms.repositories.QuizRepository;
import com.astontech.lms.services.PathService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PathServiceImplementation implements PathService {

    PathRepository pathRepository;
    CourseRepository courseRepository;
    QuizRepository quizRepository;

    public PathServiceImplementation(PathRepository pathRepository,
                                     QuizRepository quizRepository,
                                     CourseRepository courseRepository) {
        this.pathRepository = pathRepository;
        this.courseRepository = courseRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public List<Object> findAll() {
        List<Path> paths = pathRepository.findAll();
        List<Object> makeShiftPaths = new ArrayList<>();
        paths.forEach((Path path) -> makeShiftPaths.add(createPath(path.getId())));
        return makeShiftPaths;
    }

    @Override
    public Optional<Path> findById(String id) {
        return pathRepository.findById(id);
    }

    @Override
    public Optional<Path> findByName(String name) {
        return pathRepository.findByTitle(name);
    }

    @Override
    public Path savePathFromDTO(PathDTO pathDTO) throws Exception {
        if (!pathRepository.findByTitle(pathDTO.getTitle()).isPresent()) {
            Path path = createPathObject(pathDTO);
            pathDTO.getCourses().forEach((Course course) -> {
                if (!courseRepository.findByTitle(course.getTitle()).isPresent()) {
                    course = courseRepository.save(course);
                }
                path.getCourseTitles().add(course.getTitle());
            });

            return this.pathRepository.save(path);
        }
        throw new Exception();
    }

    @Override
    public Path updatePathFromDTO(PathDTO pathDTO) {
        Path path = createPathObject(pathDTO);
        pathDTO.getCourses().forEach((Course course) -> {
            course = courseRepository.save(course);
            path.getCourseTitles().add(course.getTitle());
        });

        return this.pathRepository.save(path);
    }

    private Path createPathObject(PathDTO pathDTO) {
        Path path = new Path();
        path.setId(pathDTO.getId());
        path.setDuration(pathDTO.getDuration());
        path.setPathImg(pathDTO.getPathImg());
        path.setShortTitle(pathDTO.getShortTitle());
        path.setTitle(pathDTO.getTitle());
        path.setCourseTitles(new ArrayList<>());
        return path;
    }

    @Override
    public void deletePathById(String id) {
        pathRepository.deleteById(id);
    }

    @Override
    public void deletePath(Path path) {
        pathRepository.delete(path);
    }

    @Override
    public void deleteAll() {
        pathRepository.deleteAll();
    }

    @Override
    public Map<String, Object> createPath(String id) {
        Map<String, Object> makeShiftPath = new HashMap<>();
        Path path = pathRepository.findById(id).orElse(null);
        List<Course> courses = new ArrayList<>();
        path.getCourseTitles().forEach((String courseTitle) -> {
            courses.add(courseRepository.findByTitle(courseTitle).orElse(null));
        });

        // find Quiz Ids and then fetch the full object from the db and replace the Id with the object
        for (int i = 0; i < courses.size(); i++) {
            for (int j = 0; j < courses.get(i).getContent().size(); j++) {
                if (courses.get(i).getContent().get(j).getClass() == String.class) {
                    courses.get(i)
                            .getContent()
                            .set(j, quizRepository.findById(courses.get(i).getContent().get(j).toString()));
                }
            }
        }


        makeShiftPath.put("id", path.getId());
        makeShiftPath.put("duration", path.getDuration());
        makeShiftPath.put("title", path.getTitle());
        makeShiftPath.put("shortTitle", path.getShortTitle());
        makeShiftPath.put("pathImg", path.getPathImg());
        makeShiftPath.put("courses", courses);

        return makeShiftPath;
    }
}
