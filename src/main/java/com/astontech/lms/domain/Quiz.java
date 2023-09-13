package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {

    @Id
    private String id;
    private String title;
    private List<String> questionIds;
    private List<String> tags;
    private String description;
    private Boolean completed;
    private String contentType = "QUIZ";

    public Quiz(String title, List<String> questionIds, List<String> tags, String description) {
        this.title = title;
        this.questionIds = questionIds;
        this.tags = tags;
        this.description = description;
    }
}
