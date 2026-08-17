CREATE DATABASE askmate;

CREATE TABLE IF NOT EXISTS questions (
    id INTEGER PRIMARY KEY,
    title TEXT not null,
    description TEXT not null
);

CREATE TABLE IF NOT EXISTS answers (
    id INTEGER PRIMARY KEY,
    question_id INTEGER ,
    messsage TEXT not null,
    submission_time DATE not null,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    username TEXT not null,
    password TEXT not null,
    registration_date DATE not null,
    points INTEGER
);