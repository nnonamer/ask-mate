package com.codecool.askmateoop.configuration;

import com.codecool.askmateoop.dao.QuestionsDAO;
import com.codecool.askmateoop.dao.QuestionsDaoJdbc;
import database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class Configuration {
    @Value("${askmate.database.url}")
    private String dbUrl;

    @Value("${askmate.database.username}")
    private String dbUsername;

    @Value("${askmate.database.password}")
    private String dbPassword;

    @Bean
    public DatabaseConnection databaseConnection(){
        return new DatabaseConnection(dbUrl, dbUsername, dbPassword);
    }

    @Bean
    public QuestionsDAO questionsDAO(DatabaseConnection databaseConnection) {
        return new QuestionsDaoJdbc(databaseConnection);
    }

}
