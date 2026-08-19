package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewQuestionDTO;
import com.codecool.askmateoop.dao.model.Question;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionsDaoJdbc implements QuestionsDAO {
    private final DatabaseConnection dbConnection;

    public QuestionsDaoJdbc(DatabaseConnection connection) {
        this.dbConnection = connection;
    }

    @Override
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";

        try (Connection conn = dbConnection.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("submission_time").toLocalDateTime()
                );
                questions.add(question);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return questions;
    }

    @Override
    public Question getQuestionById(int id) {
       String sql = "SELECT * FROM questions where id = ?";

       try (Connection conn = dbConnection.getConnection()){
           PreparedStatement statement = conn.prepareStatement(sql);
           statement.setInt(1, id);
           ResultSet rs = statement.executeQuery();
           rs.next();
           return new Question(
                   rs.getInt("id"),
                   rs.getString("title"),
                   rs.getString("description"),
                   rs.getTimestamp("submission_time").toLocalDateTime()
           );

       }catch (SQLException e){
           throw new RuntimeException(e);
       }
    }

    @Override
    public int addNewQuestion(NewQuestionDTO question) {
        String sql = "INSERT INTO questions (title, description, user_id) VALUES(?, ?, ?) RETURNING id";

        try (Connection conn = dbConnection.getConnection()){
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, question.title());
            statement.setString(2, question.description());
            statement.setInt(3, question.userId());

            ResultSet rs = statement.executeQuery();
            rs.next();
            return rs.getInt("id");

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteQuestionById(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";

        try (Connection conn = dbConnection.getConnection()){
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            int result = statement.executeUpdate();
            return result > 0;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}