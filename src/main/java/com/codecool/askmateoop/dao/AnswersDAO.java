package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.dao.model.Answer;

import java.util.List;

public interface AnswersDAO {
    List<Answer> getAllAnswersForQuestion(int id);
}
