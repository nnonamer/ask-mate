package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.controller.dto.UserDTO;
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
                SELECT id, username, password, registration_date
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
                            resultSet.getDate("registration_date").toLocalDate()
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
        String sql = """
                   SELECT (
                            (SELECT COUNT(*) FROM questions
                                             WHERE user_id = ?) * 5 +
                            (SELECT COUNT(*) FROM answers a JOIN questions q ON a.question_id = q.id
                                             WHERE q.user_id = ? AND a.user_id != q.user_id) +
                            (SELECT COUNT(*) FROM answers a JOIN questions q ON a.question_id = q.id
                                             WHERE a.user_id = ? and q.user_id != ?) * 10
                        ) AS points
                """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            stmt.setInt(3, id);
            stmt.setInt(4, id);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("points");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection()){
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();
            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getDate("registration_date").toLocalDate()
            );
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
