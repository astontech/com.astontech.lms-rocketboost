package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    private String id;
    private String title;
    private String description;
    private String URL;
    private String materialsMarkdown;
    private Double duration;
    private Double currentTime;
    private Boolean completed;
    private String contentType;


    public Video(String id, String title, String description, String URL, Double duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.URL = URL;
        this.duration = duration;
    }
}
