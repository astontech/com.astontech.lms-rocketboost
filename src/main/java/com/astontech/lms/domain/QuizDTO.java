package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {

    private String id;
    private String title;
    private List<Question> questions;
    private List<String> tags;
    private String description;
}
