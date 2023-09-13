package com.astontech.lms.services.implementation;

import com.astontech.lms.domain.Course;
import com.astontech.lms.domain.Path;
import com.astontech.lms.domain.UserProfile;
import com.astontech.lms.repositories.CourseRepository;
import com.astontech.lms.repositories.PathRepository;
import com.astontech.lms.repositories.UserProfileRepository;
import com.astontech.lms.services.UserProfileService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserProfileServiceImplementation implements UserProfileService {

    UserProfileRepository userProfileRepository;
    PathRepository pathRepository;
    CourseRepository courseRepository;

    public UserProfileServiceImplementation(UserProfileRepository userProfileRepository, PathRepository pathRepository, CourseRepository courseRepository) {
        this.userProfileRepository = userProfileRepository;
        this.pathRepository = pathRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Optional<UserProfile> findById(String id) {
        return userProfileRepository.findById(id);
    }

    @Override
    public UserProfile findByUsername(String username) {
        return userProfileRepository.findUserProfileByUsername(username).orElse(null);
    }

    @Override
    public UserProfile getProfile(String username) {
        username = removeEmailSignature(username);
        return userProfileRepository.findUserProfileByUsername(username).orElseGet(UserProfile::new);
    }

    @Override
    public List<UserProfile> findAll() {
        return userProfileRepository.findAll();
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public UserProfile updateProfilePaths(String path, String username) {
        UserProfile userProfile = userProfileRepository.findUserProfileByUsername(username).orElse(null);

        String pathTitle = getPathTitle(path);
        Path newPath = pathRepository.findByTitle(pathTitle).orElse(null);
        List<Course> pathCourse = new ArrayList<>();
        newPath.getCourseTitles().forEach(c -> {
            pathCourse.add(courseRepository.findByTitle(c).orElse(null));
        });
        Map<String, Object> object = new HashMap<>();
        object.put("duration", 0);
        object.put("courses", pathCourse);
        object.put("id", newPath.getId());
        object.put("shortTitle", newPath.getShortTitle());
        object.put("title", newPath.getTitle());

        List<Object> userPaths = userProfile.getPaths();
        userPaths.add(object);
        userProfile.setPaths(userPaths);

        userProfileRepository.save(userProfile);

        return userProfile;
    }

    private String getPathTitle(String path) {
        String[] split = path.replaceAll("-", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s.substring(0, 1).toUpperCase(Locale.ROOT))
                    .append(s.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public void deleteAll() {
        userProfileRepository.deleteAll();
    }

    @Override
    public void deleteByUsername(String username) {
        userProfileRepository.deleteByUsername(username);
    }

    public static String removeEmailSignature(String name) {
        return name.contains("@") ? name.substring(0, name.indexOf("@")) : name;
    }


}
