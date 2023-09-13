package com.astontech.lms.services.implementation;

import com.astontech.lms.domain.Question;
import com.astontech.lms.repositories.QuestionRepository;
import com.astontech.lms.services.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    final private QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Question> findQuestion(String title) {
        return questionRepository.findByTitle(title);
    }

    @Override
    public List<Question> findQuestions(String tag) {
        return questionRepository.findAllByTagsContaining(tag);
    }
}
