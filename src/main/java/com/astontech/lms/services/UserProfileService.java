package com.astontech.lms.services;

import com.astontech.lms.domain.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {

    Optional<UserProfile> findById(String id);

    UserProfile findByUsername(String username);

    UserProfile getProfile(String username);

    List<UserProfile> findAll();

    UserProfile save(UserProfile userProfile);

    UserProfile updateProfilePaths(String path, String username);

    void deleteAll();

    void deleteByUsername(String username);
}
