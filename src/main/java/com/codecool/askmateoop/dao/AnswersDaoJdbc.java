package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewAnswerDTO;
import com.codecool.askmateoop.dao.model.Answer;
import database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnswersDaoJdbc implements AnswersDAO {
    private final DatabaseConnection databaseConnection;

    @Autowired
    public AnswersDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Answer> getAllAnswersForQuestion(int id) {
        String sql = """
                SELECT id, question_id, user_id, message, submission_time
                FROM answers
                WHERE question_id = ?
                ORDER BY submission_time DESC
                """;

        List<Answer> answers = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Answer answer = new Answer(
                            resultSet.getInt("id"),
                            resultSet.getInt("question_id"),
                            resultSet.getInt("user_id"),
                            resultSet.getString("message"),
                            resultSet.getTimestamp("submission_time").toLocalDateTime()
                    );

                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get answers for question with id: " + id, e);
        }

        return answers;
    }

    @Override
    public boolean deleteAnswerById(int id) {
        String sql = "DELETE FROM answers WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection()){
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            int result = statement.executeUpdate();
            return result > 0;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addNewAnswer(NewAnswerDTO answer) {
        String sql = "INSERT INTO answer (message) VALUES(?) RETURNING id";

        try (Connection conn = databaseConnection.getConnection()){
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, answer.message());

            ResultSet rs = statement.executeQuery();
            rs.next();
            return rs.getInt("id");

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
