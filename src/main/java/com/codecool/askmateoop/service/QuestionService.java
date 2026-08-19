package com.codecool.askmateoop.service;

import com.codecool.askmateoop.controller.dto.NewQuestionDTO;
import com.codecool.askmateoop.controller.dto.QuestionDTO;
import com.codecool.askmateoop.dao.QuestionsDAO;
import com.codecool.askmateoop.dao.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class QuestionService {

    private final QuestionsDAO questionsDAO;

    @Autowired
    public QuestionService(QuestionsDAO questionsDAO) {
        this.questionsDAO = questionsDAO;
    }

    public List<QuestionDTO> getAllQuestions() {
       return questionsDAO.getAllQuestions().stream().map(q ->  new QuestionDTO(
               q.id(),
               q.title(),
               q.description(),
               q.created()
       )).toList();
    }

    public QuestionDTO getQuestionById(int id) {
       Question question = questionsDAO.getQuestionById(id);
       return new QuestionDTO(question.id(), question.title(), question.description(), question.created());
    }

    public boolean deleteQuestionById(int id) {
       return questionsDAO.deleteQuestionById(id);
    }

    public int addNewQuestion(NewQuestionDTO question) {
        return questionsDAO.addNewQuestion(question);
    }

    public List<QuestionDTO> searchQuestions(String searchTerm){
        return questionsDAO.searchQuestions(searchTerm).stream().map(q -> new QuestionDTO(
                q.id(),
                q.title(),
                q.description(),
                q.created()
        )).toList();
    }
}
