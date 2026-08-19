package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.dao.model.User;
import database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UsersDaoJdbc implements UsersDAO {
    private final DatabaseConnection databaseConnection;

    @Autowired
    public UsersDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int addNewUser(NewUserDTO user) {
        String sql = "INSERT INTO users (username, password, registration_date) VALUES(?, ?, CURRENT_DATE) RETURNING id";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.username());
            statement.setString(2, user.password());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user: " + user.username(), e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = """
                SELECT id, username, password, registration_date, points
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getDate("registration_date").toLocalDate(),
                            (Integer) resultSet.getObject("points")
                    );
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by username: " + username, e);
        }
    }

    @Override
    public int getUserByPoints(int id) {
        String sql = "SELECT COUNT(*) FROM "
    }
}
