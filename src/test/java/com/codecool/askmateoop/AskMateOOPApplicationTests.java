package com.codecool.askmateoop;

import database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AskMateOOPApplicationTests {

    @Value("${askmate.database.url}")
    private String dbUrl;

    @Value("${askmate.database.username}")
    private String dbUser;

    @Value("${askmate.database.password}")
    private String dbPassword;

    @Test
    void testDatabaseConnection() {
        DatabaseConnection databaseConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword);
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT 1")) {

            assertTrue(rs.next(), "Database should return a result");
            System.out.println("✅ Database connection successful!");
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void contextLoads() {

    }
}
