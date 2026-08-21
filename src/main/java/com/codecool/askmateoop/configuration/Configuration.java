package com.codecool.askmateoop.configuration;

import com.codecool.askmateoop.dao.QuestionsDAO;
import com.codecool.askmateoop.dao.QuestionsDaoJdbc;
import database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class Configuration implements WebMvcConfigurer {
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

}

