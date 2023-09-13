package com.astontech.lms.bootstrap;

import com.astontech.lms.domain.*;
import com.astontech.lms.repositories.CourseRepository;
import com.astontech.lms.repositories.QuestionRepository;
import com.astontech.lms.repositories.QuizRepository;
import com.astontech.lms.services.PathService;
import com.astontech.lms.services.UserProfileService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class SeedData implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SeedData.class);

    PathService pathService;
    CourseRepository courseRepository;
    UserProfileService userProfileService;
    QuizRepository quizRepository;
    QuestionRepository questionRepository;
    ExcelReader excelReader;

    @Value("${seed.reset.database}")
    boolean seedResetDb;
    @Value("${seed.excel.reader}")
    boolean useExcelReader;
    @Value("${seed.create.quizzes}")
    boolean createQuizzes;
    @Value("${seed.generate.test-paths}")
    boolean generateTestPaths;

    public SeedData(PathService pathService,
                    UserProfileService userProfileService,
                    CourseRepository courseRepository,
                    QuizRepository quizRepository,
                    QuestionRepository questionRepository,
                    ExcelReader excelReader) {
        this.pathService = pathService;
        this.courseRepository = courseRepository;
        this.userProfileService = userProfileService;
        this.excelReader = excelReader;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (seedResetDb) resetDatabase();
        if (useExcelReader) runExcelImport();
        if (createQuizzes) createQuizzes();
//        if (generateTestPaths) generateTestPathData();
    }

    public void runExcelImport() {
        try {

            excelReader.importJavaPathFromExcel();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    public void createQuizzes() {
        List<String> questionIds = new ArrayList<>();

        // create answers
        List<String> questionOneAnswers = Arrays.asList("It compiles the codes",
                "It provides the ability to write and develop new code",
                "It provides the runtime environment needed to run a jar");

        //create questions
        Question question1 = new Question("What is the JDK?",
                questionOneAnswers,
                Arrays.asList(questionOneAnswers.get(1)),
                Arrays.asList("Java"),
                "EASY",
                "SINGLE");
        Question question2 = new Question("What is the JVM?",
                questionOneAnswers,
                Arrays.asList(questionOneAnswers.get(2)),
                Arrays.asList("Java"),
                "EASY",
                "SINGLE");
        Question question3 = new Question("What is the JRE?",
                questionOneAnswers,
                Arrays.asList(questionOneAnswers.get(2), questionOneAnswers.get(0)),
                Arrays.asList("Java"),
                "EASY",
                "MULTIPLE");

        // save questions
        for (int i = 0; i < 1; i++) {
            String id = questionRepository.save(question1).getId();
            questionIds.add(id);
            id = questionRepository.save(question2).getId();
            questionIds.add(id);
            id = questionRepository.save(question3).getId();
            questionIds.add(id);
        }

        // create quiz
        Quiz quiz1 = new Quiz("Java Basics",
                questionIds,
                Arrays.asList("Java"),
                "Basic Java Trivia Questions");
        quiz1 = quizRepository.save(quiz1);

        // get and save quiz in course
        Course oopCourse = courseRepository.findByTitle("Java OOP - Principles").orElse(null);
        if (oopCourse != null) {
            oopCourse.getContent().add(quiz1.getId());
            courseRepository.save(oopCourse);
        }
    }

    public void resetDatabase() {
        this.pathService.deleteAll();
        this.courseRepository.deleteAll();
        this.userProfileService.deleteAll();
        this.quizRepository.deleteAll();
        this.questionRepository.deleteAll();
    }

//    public void generateTestPathData() {
//        IntStream.range(1, 5)
//                .forEach(i -> {
//                    Path path = new Path();
//                    path.setId(Integer.toString(i));
//                    path.setTitle("SeedData-" + i);
//                    path.setShortTitle(Integer.toString(i));
//                    path.setCoursesTitles(new ArrayList<>());
//                    path.setDuration(new Time(0,0,0));
//                    pathService.savePath(path);
//                });
//
//        IntStream.range(1, 5)
//                .forEach(i -> {
//                    UserProfile userProfile = new UserProfile();
//                    userProfile.setId(Integer.toString(i));
//                    userProfile.setFirstName("User-" + i);
//                    userProfile.setUsername("user-" + i);
//                    userProfileService.save(userProfile);
//                });
//
//        List<Course> courses = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            List<Video> videos = new ArrayList<>();
//            Video video = new Video();
//            Video video2 = new Video();
//            video.setId("SeedData-Video-" + i);
//            video2.setId("SeedData-Video-" + i + "2");
//            video.setTitle("Video-" + i);
//            video2.setTitle("Video-" + i + "2");
//            video.setDesc("This video was generated by SeedData.java");
//            video2.setDesc("This video was generated by SeedData.java");
//            video.setURL("https://aston-lms-videos.s3.amazonaws.com/01-Database/A-01-IDE-Install.mp4");
//            video2.setURL("https://aston-lms-videos.s3.amazonaws.com/05 - Web App - Stack 2/A03 - Service Layer.mp4");
//            video.setDuration(645.26);
//            video2.setDuration(1255.86);
//            videos.add(video);
//            videos.add(video2);
//
//            Course course = new Course();
//            course.setTitle("SeedData Course-" + i);
//            course.setDesc("This description was generated by SeedData.java");
//            course.setContent(videos);
//            courses.add(course);
//        }
//
//        Path path = pathService.findAll().get(0);
//        path.setCourses(courses);
//        pathService.savePath(path);
//    }


}
