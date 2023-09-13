package com.astontech.lms.repositories;

import com.astontech.lms.domain.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends MongoRepository<Quiz, String> {

    Optional<Quiz> findByTitle(String title);
    List<Quiz> findAllByTagsContaining(String tag);
}
