package com.astontech.lms.services;

import com.astontech.lms.domain.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    Optional<Question> findQuestion(String title);
    List<Question> findQuestions(String tag);
}
