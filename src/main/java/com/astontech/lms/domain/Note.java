package com.astontech.lms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    @Id
    String id;
    String path;
    String course;
    String content;
    String note;
    Date date;
    Double timestamp;
}
