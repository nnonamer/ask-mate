package com.codecool.askmateoop.controller;

import com.codecool.askmateoop.controller.dto.*;
import com.codecool.askmateoop.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/answer")
public class AnswerController {
    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @GetMapping("/{question_id}/all")
    public List<AnswerDTO> getAllQuestions(@PathVariable int question_id) {
        return answerService.getAllAnswersForQuestion(question_id);
    }

    @PostMapping("/")
    public int addNewAnswer(@RequestBody NewAnswerDTO answer) {
        return answerService.addNewAnswer(answer);
    }

    @DeleteMapping("/{id}")
    public boolean deleteAnswerById(@PathVariable int id) {
        return answerService.deleteAnswerById(id);
    }
}
