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
public class Path {

    @Id
    String id;
    String title;
    String shortTitle;
    String pathImg;
    Time duration;
    List<String> courseTitles;

    public Path(String title, String shortTitle, String pathImg) {
        this.title = title;
        this.shortTitle = shortTitle;
        this.pathImg = pathImg;
    }
}
