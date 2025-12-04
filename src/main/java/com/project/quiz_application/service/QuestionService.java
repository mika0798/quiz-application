package com.project.quiz_application.service;

import com.project.quiz_application.model.Question;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final Map<Integer, Question> questions = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(0);

    public ArrayList<Question> getQuizzesList() {
        return new ArrayList<>(questions.values());
    }

    public Question getQuizById(int id) {
        return questions.get(Integer.valueOf(id));
    }

    public int getNextId() {
        return nextId.getAndIncrement();
    }

    public boolean addQuiz(Question question) {
        int id = nextId.getAndIncrement();
        question.setId(id);
        questions.put(question.getId(), question);
        return true;
    }

    public boolean editQuiz(Question question) {
        Integer id = question.getId();
        if (id != null && questions.containsKey(id)) {
            questions.put(id, question);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteQuizById(int id) {
        return questions.remove(id) != null;
    }

    public List<Question> findAllByIds(Collection<Integer> ids) {
        return ids.stream()
                .map(questions::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean isCorrect(Integer id, String givenAnswer) {
        Question q = questions.get(id);
        if (q == null || givenAnswer == null) return false;
        return givenAnswer.equals(q.getAnswer());
    }
}
