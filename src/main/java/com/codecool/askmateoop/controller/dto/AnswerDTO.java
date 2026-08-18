package com.codecool.askmateoop.controller.dto;

import java.time.LocalDateTime;

public record AnswerDTO(int id, int questionId, int userId, String message, LocalDateTime submissionTime){}
