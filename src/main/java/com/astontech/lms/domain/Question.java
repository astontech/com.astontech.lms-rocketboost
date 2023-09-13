package com.astontech.lms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    private String id;
    private String title;
    private List<String> options;
    private List<String> selected;
    private List<String> answers;
    private List<String> tags;
    private String difficulty;
    // either MULTIPLE for multiple choice or SINGLE for single choice
    private String questionType;
    private List<Integer> panel = Arrays.asList(0,1);

    public Question(String title, List<String> options, List<String> answers, List<String> tags, String difficulty, String questionType) {
        this.title = title;
        this.options = options;
        this.answers = answers;
        this.tags = tags;
        this.difficulty = difficulty;
        this.questionType = questionType;
    }
}
