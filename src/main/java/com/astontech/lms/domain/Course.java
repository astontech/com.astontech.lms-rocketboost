package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    private String id;
    private String title;
    private String desc;
    private String courseImg;
    // KEY VALUE PAIR?
    // IF VIDEO or LAB, EMBED DOCUMENT as it will only be used for this specific course...
    // IF QUIZ, REFERENCE DOCUMENT as it will be used independently of the Course
    private List<Object> content;
    private Time duration;
}
