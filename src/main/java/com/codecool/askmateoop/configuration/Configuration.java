package com.codecool.askmateoop.configuration;

import com.codecool.askmateoop.dao.QuestionsDAO;
import com.codecool.askmateoop.dao.QuestionsDaoJdbc;
import database.DatabaseConnection;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class Configuration {

//    TODO: Add the url of your database to the Environment Variables of the Run Configuration
//    @Value("${askmate.database.url}")
//    private String databaseUrl;

    @Bean
    public DatabaseConnection databaseConnection(){
        return new DatabaseConnection();
    }

    @Bean
    public QuestionsDAO questionsDAO(DatabaseConnection databaseConnection) {
        return new QuestionsDaoJdbc(databaseConnection);
    }

}
