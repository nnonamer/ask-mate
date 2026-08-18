package com.codecool.askmateoop.dao.model;

import java.time.LocalDateTime;

public record Answer(int id, int questionId, int userId, String message, LocalDateTime submissionTime) {
}
