package com.project.quiz_application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private Integer id;
    @NotBlank(message="Question text cannot be blank")
    private String questionText;

    @NotEmpty(message="Options list cannot be blank")
    private ArrayList<String> options = new ArrayList<>();

    @NotBlank(message="Answer cannot be blank")
    private String answer;

    public String getOptionsAsString() {
        return options == null ? "" : String.join(",", options);
    }

    public void setOptionsAsString(String optionStr) {
        if (optionStr == null || optionStr.equals("")) {
            this.options = new ArrayList<>();
        } else {
            this.options = Arrays.stream(optionStr.split("\\s*,\\s*"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    @Override
    public String toString() {
        return "ID: " + id
                + "\nQuestion: " +  questionText
                +"\nOptions: " + getOptionsAsString()
                +"\nCorrect answer: " + answer;
    }
}
