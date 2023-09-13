package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathDTO {

    String id;
    List<Course> courses;
    Time duration;
    String pathImg;
    String shortTitle;
    String title;

}
