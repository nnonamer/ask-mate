package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewAnswerDTO;
import com.codecool.askmateoop.dao.model.Answer;

import java.util.List;

public interface AnswersDAO {
    List<Answer> getAllAnswersForQuestion(int id);
    boolean deleteAnswerById(int id);
    int addNewAnswer(NewAnswerDTO answer);
}
