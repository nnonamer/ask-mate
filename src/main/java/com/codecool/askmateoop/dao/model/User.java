package com.codecool.askmateoop.dao.model;

import java.time.LocalDate;

public record User(int id, String username, String password, LocalDate registrationDate, Integer points) {
}
