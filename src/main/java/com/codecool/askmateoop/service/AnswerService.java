package com.codecool.askmateoop.service;

import com.codecool.askmateoop.dao.AnswersDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    private final AnswersDAO answersDAO;

    @Autowired
    private AnswerService(AnswersDAO answersDAO) {
        this.answersDAO = answersDAO;
    }


}

