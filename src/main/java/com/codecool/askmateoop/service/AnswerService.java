package com.codecool.askmateoop.service;

import com.codecool.askmateoop.controller.dto.AnswerDTO;
import com.codecool.askmateoop.controller.dto.NewAnswerDTO;
import com.codecool.askmateoop.dao.AnswersDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private final AnswersDAO answersDAO;

    @Autowired
    public AnswerService(AnswersDAO answersDAO) {
        this.answersDAO = answersDAO;
    }

    public List<AnswerDTO> getAllAnswersForQuestion(int questionId) {
        return answersDAO.getAllAnswersForQuestion(questionId).stream()
                .map(answer -> new AnswerDTO(
                        answer.id(),
                        answer.questionId(),
                        answer.userId(),
                        answer.message(),
                        answer.submissionTime()
                )).toList();
    }

    public boolean deleteAnswerById(int id) {
        return answersDAO.deleteAnswerById(id);
    }

    public int addNewAnswer(NewAnswerDTO answer) {
        return answersDAO.addNewAnswer(answer);
    }

}

