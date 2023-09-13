package com.astontech.lms.rest;

import com.astontech.lms.domain.Quiz;
import com.astontech.lms.domain.QuizDTO;
import com.astontech.lms.services.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRestController {

    QuizService quizService;

    public QuizRestController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Quiz>> getQuizzes() {
        List<Quiz> quizList = quizService.findAllQuizzes();
        return ResponseEntity.ok(quizList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuizById(@PathVariable String id) {
        Map<String, Object> quiz = quizService.createQuiz(id);
        if (quiz != null) {
            return ResponseEntity.ok(quiz);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/")
    public ResponseEntity<Quiz> saveNewQuiz(@RequestBody QuizDTO quizDTO) {
        Quiz newQuiz = quizService.saveQuiz(quizDTO);
        if (newQuiz == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(newQuiz);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@RequestBody QuizDTO quizDTO) {
        Quiz quiz = quizService.updateQuiz(quizDTO);
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteQuiz(@PathVariable("id") String quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok().body(null);
    }
}
