package com.project.quiz_application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private Integer id;
    private String questionText;
    private ArrayList<String> options;
    private String answer;

    public String getOptionsAsString() {
        return String.join(",", options);
    }

    public void setOptionsFromString(String options) {
        this.options = new ArrayList<>(Arrays.asList(options.split(",")));
    }

    @Override
    public String toString() {
        return "ID: " + id
                + "\nQuestion: " +  questionText
                +"\nOptions: " + getOptionsAsString()
                +"\nCorrect answer: " + answer;
    }
}
