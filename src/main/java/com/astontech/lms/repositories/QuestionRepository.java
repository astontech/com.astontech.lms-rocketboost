package com.astontech.lms.repositories;

import com.astontech.lms.domain.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends MongoRepository<Question, String> {

    Optional<Question> findByTitle(String title);
    List<Question> findAllByTagsContaining(String tag);
}
