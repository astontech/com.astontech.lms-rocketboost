package com.astontech.lms.services;

import com.astontech.lms.domain.Quiz;
import com.astontech.lms.domain.QuizDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuizService {

    Optional<Quiz> findQuiz(String title);
    Optional<Quiz> findQuizById(String id);
    List<Quiz> findQuizzes(String tag);
    List<Quiz> findAllQuizzes();
    Map<String, Object> createQuiz(String id);
    Quiz saveQuiz(QuizDTO quiz);
    Quiz updateQuiz(QuizDTO quizDTO);
    void deleteQuiz(String quizId);
}
