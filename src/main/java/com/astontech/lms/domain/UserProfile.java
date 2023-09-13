package com.astontech.lms.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "All information about the user")
public class UserProfile {

    @Id
    String id;
    String firstName;
    String lastName;
    String username;
    String role;
    List<Object> paths;
    List<Note> notes;
    Settings settings;

    public UserProfile(String username, String role) {
        this.username = username;
        this.role = role;

        String[] name = parseName(username);
        this.firstName = name[0];
        this.lastName = name[1];

        this.paths = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.settings = new Settings(false);
    }

    private String[] parseName(String username) {
        String[] name = username.split("\\.");

        // Strip numbers from name
        // Since name is derived from email address, if the user had a number in their name it would become part of their name.
        for (int i = 0; i < name.length; i++) {
            name[i] = name[i].replaceAll("[0-9]", "");
        }

        return name;
    }

}
