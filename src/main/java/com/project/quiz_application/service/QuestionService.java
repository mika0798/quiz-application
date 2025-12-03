package com.project.quiz_application.service;

import com.project.quiz_application.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionService {
    private final Map<Integer, Question> questions = new HashMap<>();
    private Integer nextId = 1;

    public ArrayList<Question> getQuizzesList() {
        return new ArrayList<>(questions.values());
    }

    public Question getQuizById(int id) {
        return questions.get(Integer.valueOf(id));
    }

    public int getNextId() {
        return nextId++;
    }

    public boolean addQuiz(Question question) {
        Integer id = question.getId();
        if (questions.containsKey(id)) {
            return false;
        } else {
            questions.put(id, question);
            return true;
        }
    }

    public boolean editQuiz(Question question) {
        Integer id = question.getId();
        if (questions.containsKey(id)) {
            questions.put(id, question);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteQuizById(int id) {
        if (questions.containsKey(id)) {
            questions.remove(id);
            return true;
        } else  {
            return false;
        }
    }

    public int submitQuiz(ArrayList<Question> submission) {
        int count = 0;
        for (Question sub : submission) {
            Question res = questions.get(sub.getId());
            if (sub.getAnswer().equals(res.getAnswer())) {
                count++;
            }
        }
        return count;
    }
}
