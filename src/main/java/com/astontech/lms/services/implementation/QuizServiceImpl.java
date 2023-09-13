package com.astontech.lms.services.implementation;

import com.astontech.lms.domain.Question;
import com.astontech.lms.domain.Quiz;
import com.astontech.lms.domain.QuizDTO;
import com.astontech.lms.repositories.QuestionRepository;
import com.astontech.lms.repositories.QuizRepository;
import com.astontech.lms.services.QuizService;
import com.astontech.lms.services.UserProfileService;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private MongoOperations mongoOperations;
    final private QuizRepository quizRepository;
    final private QuestionRepository questionRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           MongoOperations mongoOperations) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<Quiz> findQuiz(String title) {
        return quizRepository.findByTitle(title);
    }

    @Override
    public Optional<Quiz> findQuizById(String id) {
        return quizRepository.findById(id);
    }

    @Override
    public List<Quiz> findQuizzes(String tag) {
        return quizRepository.findAllByTagsContaining(tag);
    }

    @Override
    public List<Quiz> findAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    public Map<String, Object> createQuiz(String id) {
        Map<String, Object> makeShiftQuiz = new HashMap<>();
        Quiz quiz = quizRepository.findById(id).orElse(null);
        List<Question> questions = new ArrayList<>();

        if (quiz != null) {
            quiz.getQuestionIds().forEach((String questionId) -> {
                questions.add(questionRepository.findById(questionId).orElse(null));
            });

            makeShiftQuiz.put("id", quiz.getId());
            makeShiftQuiz.put("title", quiz.getTitle());
            makeShiftQuiz.put("questions", questions);
            makeShiftQuiz.put("tags", quiz.getTags());
            makeShiftQuiz.put("description", quiz.getDescription());
            makeShiftQuiz.put("contentType", quiz.getContentType());
        }

        return makeShiftQuiz;
    }

    @Override
    public Quiz saveQuiz(QuizDTO quizDTO) {
        return quizRepository.save(transformDtoToQuiz(quizDTO));
    }

    private Quiz transformDtoToQuiz(QuizDTO quizDTO) {
        Quiz quiz = null;
        if (quizDTO.getId() != null) {
            quiz = quizRepository.findById(quizDTO.getId()).orElseGet(Quiz::new);
        } else {
            quiz = new Quiz();
        }

        quiz.setTitle(quizDTO.getTitle());
        quiz.setTags(quizDTO.getTags());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setCompleted(false);
        quiz.setQuestionIds(new ArrayList<>());

        for (int i = 0; i < quizDTO.getQuestions().size(); i++) {
            Question q = questionRepository.save(quizDTO.getQuestions().get(i));
            if (!quiz.getQuestionIds().contains(q.getId())) {
                quiz.getQuestionIds().add(q.getId());
            }
        }
        return quiz;
    }

    @Override
    public Quiz updateQuiz(QuizDTO quizDTO) {
        Quiz updatedQuiz = saveQuiz(quizDTO);

        JSONObject quizJson = new JSONObject(updatedQuiz);
        Bson quizBson = new Document(quizJson.toMap());
        Bson filter = Filters.and(Filters.eq("paths.courses.content.id", quizDTO.getId()));
        Bson update = Updates.set("paths.0.courses.$[].content.$[c]", quizBson);

        UpdateOptions updateOptions = new UpdateOptions();
        List<Bson> arrayFilters = new ArrayList();
        Bson arrayElement = Filters.eq("c.id", quizDTO.getId());
        arrayFilters.add(arrayElement);
        updateOptions.arrayFilters(arrayFilters);

        mongoOperations.getCollection("userProfile").updateMany(filter, update, updateOptions);
        return updatedQuiz;
    }

    @Override
    public void deleteQuiz(String quizId) {
        quizRepository.deleteById(quizId);

        // remove from UserProfile
        Bson filter = Filters.and(Filters.eq("paths.courses.content.id", quizId));
        Document id = new Document("id", quizId);
        Document content = new Document("paths.0.courses.$[].content", id);
        Document delete = new Document("$pull", content);
        mongoOperations.getCollection("userProfile").updateMany(filter, delete);
    }
}
