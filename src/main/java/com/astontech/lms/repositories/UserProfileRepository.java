package com.astontech.lms.repositories;

import com.astontech.lms.domain.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    Optional<UserProfile> findUserProfileByUsername(String username);

    void deleteByUsername(String username);
}
