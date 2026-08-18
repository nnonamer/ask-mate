package com.codecool.askmateoop.service;

import com.codecool.askmateoop.controller.dto.AnswerDTO;
import com.codecool.askmateoop.controller.dto.NewAnswerDTO;
import com.codecool.askmateoop.controller.dto.NewQuestionDTO;
import com.codecool.askmateoop.controller.dto.QuestionDTO;
import com.codecool.askmateoop.dao.AnswersDAO;
import com.codecool.askmateoop.dao.model.Answer;
import com.codecool.askmateoop.dao.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerService {
    private final AnswersDAO answersDAO;

    @Autowired
    private AnswerService(AnswersDAO answersDAO) {
        this.answersDAO = answersDAO;
    }

    public List<AnswerDTO> getAllAnswersForQuestion(int questionId) {
        List<Answer> allAnswers = answersDAO.getAllAnswersForQuestion(questionId);
        // TODO convert data to AnswerDTO
        return List.of(new AnswerDTO(1, 1, 1, "Example Answer", LocalDateTime.now()));
    }

    public boolean deleteAnswerById(int id) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int addNewAnswer(NewAnswerDTO answer) {
        // TODO
        throw new UnsupportedOperationException();
    }

}

