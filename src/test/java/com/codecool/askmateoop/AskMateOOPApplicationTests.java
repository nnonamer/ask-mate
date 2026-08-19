
package com.codecool.askmateoop;

import com.codecool.askmateoop.controller.AnswerController;
import com.codecool.askmateoop.controller.QuestionController;
import com.codecool.askmateoop.controller.dto.AnswerDTO;
import com.codecool.askmateoop.controller.dto.NewAnswerDTO;
import com.codecool.askmateoop.controller.dto.NewQuestionDTO;
import com.codecool.askmateoop.controller.dto.QuestionDTO;
import com.codecool.askmateoop.dao.AnswersDAO;
import com.codecool.askmateoop.dao.AnswersDaoJdbc;
import com.codecool.askmateoop.dao.QuestionsDAO;
import com.codecool.askmateoop.dao.QuestionsDaoJdbc;
import com.codecool.askmateoop.dao.model.Answer;
import com.codecool.askmateoop.dao.model.Question;
import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.dao.UsersDAO;
import com.codecool.askmateoop.dao.model.User;
import com.codecool.askmateoop.service.AnswerService;
import com.codecool.askmateoop.service.QuestionService;
import com.codecool.askmateoop.service.UserService;
import database.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    // =========================================================================
    // AnswersDaoJdbc Tests
    // =========================================================================
    @Nested
    @DisplayName("AnswersDaoJdbc Tests")
    class AnswersDaoJdbcTests {

        static class AnswerRowData {
            final int id;
            final int questionId;
            final int userId;
            final String message;
            final Timestamp submissionTime;

            AnswerRowData(int id, int questionId, int userId, String message, Timestamp submissionTime) {
                this.id = id;
                this.questionId = questionId;
                this.userId = userId;
                this.message = message;
                this.submissionTime = submissionTime;
            }
        }

        private final List<AnswerRowData> mockRows = new ArrayList<>();
        private int currentRowIndex;
        private int updateCountResult;
        private int generatedIdResult;
        private SQLException exceptionToThrow;

        private int lastSetIntIndex;
        private int lastSetIntValue;
        private int lastSetStringIndex;
        private String lastSetStringValue;
        private boolean executeQueryCalled;
        private boolean executeUpdateCalled;

        private ResultSet resultSetProxy;
        private PreparedStatement statementProxy;
        private Connection connectionProxy;
        private DatabaseConnection databaseConnection;
        private AnswersDaoJdbc answersDaoJdbc;

        @BeforeEach
        void setUp() {
            mockRows.clear();
            currentRowIndex = -1;
            updateCountResult = 0;
            generatedIdResult = 0;
            exceptionToThrow = null;

            lastSetIntIndex = -1;
            lastSetIntValue = -1;
            lastSetStringIndex = -1;
            lastSetStringValue = null;
            executeQueryCalled = false;
            executeUpdateCalled = false;

            InvocationHandler resultSetHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("next".equals(name)) {
                    currentRowIndex++;
                    return currentRowIndex < mockRows.size() || (generatedIdResult > 0 && currentRowIndex == 0);
                }
                if ("getInt".equals(name)) {
                    String col = (String) args[0];
                    if ("id".equals(col)) {
                        if (generatedIdResult > 0) return generatedIdResult;
                        if (currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                            return mockRows.get(currentRowIndex).id;
                        }
                    }
                    if ("question_id".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                        return mockRows.get(currentRowIndex).questionId;
                    }
                    if ("user_id".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                        return mockRows.get(currentRowIndex).userId;
                    }
                    return 0;
                }
                if ("getString".equals(name)) {
                    String col = (String) args[0];
                    if ("message".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                        return mockRows.get(currentRowIndex).message;
                    }
                    return null;
                }
                if ("getTimestamp".equals(name)) {
                    String col = (String) args[0];
                    if ("submission_time".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.
                            size()) {
                        return mockRows.get(currentRowIndex).submissionTime;
                    }
                    return null;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockResultSet";
                return defaultReturnValue(method.getReturnType());
            };

            resultSetProxy = (ResultSet) Proxy.newProxyInstance(
                    ResultSet.class.getClassLoader(),
                    new Class<?>[]{ResultSet.class},
                    resultSetHandler
            );

            InvocationHandler statementHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setInt".equals(name)) {
                    lastSetIntIndex = (int) args[0];
                    lastSetIntValue = (int) args[1];
                    return null;
                }
                if ("setString".equals(name)) {
                    lastSetStringIndex = (int) args[0];
                    lastSetStringValue = (String) args[1];
                    return null;
                }
                if ("executeQuery".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    executeQueryCalled = true;
                    return resultSetProxy;
                }
                if ("executeUpdate".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    executeUpdateCalled = true;
                    return updateCountResult;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockPreparedStatement";
                return defaultReturnValue(method.getReturnType());
            };

            statementProxy = (PreparedStatement) Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class<?>[]{PreparedStatement.class},
                    statementHandler
            );

            InvocationHandler connectionHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("prepareStatement".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    return statementProxy;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockConnection";
                return defaultReturnValue(method.getReturnType());
            };

            connectionProxy = (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    connectionHandler
            );

            databaseConnection = new DatabaseConnection("", "", "") {
                @Override
                public Connection getConnection() throws SQLException {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    return connectionProxy;
                }
            };

            answersDaoJdbc = new AnswersDaoJdbc(databaseConnection);
        }

        private Object defaultReturnValue(Class<?> type) {
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            return null;
        }

        @Test
        @DisplayName("getAllAnswersForQuestion returns list of answers when query succeeds")
        void getAllAnswersForQuestion_Success() {
            int questionId = 10;
            LocalDateTime now = LocalDateTime.now().withNano(0);
            Timestamp timestamp = Timestamp.valueOf(now);

            mockRows.add(new AnswerRowData(1, questionId, 100, "First answer", timestamp));
            mockRows.add(new AnswerRowData(2, questionId, 101, "Second answer", timestamp));

            List<Answer> answers = answersDaoJdbc.getAllAnswersForQuestion(questionId);

            assertNotNull(answers);
            assertEquals(2, answers.size());

            Answer first = answers.get(0);
            assertEquals(1, first.id());
            assertEquals(questionId, first.questionId());
            assertEquals(100, first.userId());
            assertEquals("First answer", first.message());
            assertEquals(now, first.submissionTime());

            assertEquals(1, lastSetIntIndex);
            assertEquals(questionId, lastSetIntValue);
            assertTrue(executeQueryCalled);
        }

        @Test
        @DisplayName("getAllAnswersForQuestion returns empty list when no rows are found")
        void getAllAnswersForQuestion_EmptyList() {
            int questionId = 99;

            List<Answer> answers = answersDaoJdbc.getAllAnswersForQuestion(questionId);

            assertNotNull(answers);
            assertTrue(answers.isEmpty());
            assertEquals(1, lastSetIntIndex);
            assertEquals(questionId, lastSetIntValue);
        }

        @Test
        @DisplayName("getAllAnswersForQuestion throws RuntimeException when SQLException occurs")
        void getAllAnswersForQuestion_ThrowsExceptionOnSQLException() {
            int questionId = 1;
            exceptionToThrow = new SQLException("Query failure");

            RuntimeException ex = assertThrows(RuntimeException.class, () -> answersDaoJdbc.
                    getAllAnswersForQuestion(questionId));
            assertTrue(ex.getMessage().contains("Failed to get answers for question with id: " + questionId));
        }

        @Test
        @DisplayName("deleteAnswerById returns true when row is deleted")
        void deleteAnswerById_Success() {
            int answerId = 5;
            updateCountResult = 1;

            boolean result = answersDaoJdbc.deleteAnswerById(answerId);

            assertTrue(result);
            assertEquals(1, lastSetIntIndex);
            assertEquals(answerId, lastSetIntValue);
            assertTrue(executeUpdateCalled);
        }

        @Test
        @DisplayName("deleteAnswerById returns false when no row is found")
        void deleteAnswerById_NotFound() {
            int answerId = 999;
            updateCountResult = 0;

            boolean result = answersDaoJdbc.deleteAnswerById(answerId);

            assertFalse(result);
            assertEquals(1, lastSetIntIndex);
            assertEquals(answerId, lastSetIntValue);
        }

        @Test
        @DisplayName("deleteAnswerById throws RuntimeException when SQLException occurs")
        void deleteAnswerById_ThrowsExceptionOnSQLException() {
            int answerId = 5;
            exceptionToThrow = new SQLException("Delete failure");

            assertThrows(RuntimeException.class, () -> answersDaoJdbc.deleteAnswerById(answerId));
        }
    }

    // =========================================================================
    // AnswerService Tests
    // =========================================================================
    @Nested
    @DisplayName("AnswerService Tests")
    class AnswerServiceTests {

        static class FakeAnswersDAO implements AnswersDAO {
            List<Answer> returnAnswers = new ArrayList<>();
            int returnGeneratedId = 0;
            boolean returnDeleteStatus = false;

            int lastQuestionIdQuery = -1;
            int lastDeleteId = -1;
            NewAnswerDTO lastNewAnswer = null;

            @Override
            public List<Answer> getAllAnswersForQuestion(int id) {
                this.lastQuestionIdQuery = id;
                return returnAnswers;
            }

            @Override
            public boolean deleteAnswerById(int id) {
                this.lastDeleteId = id;
                return returnDeleteStatus;
            }

            @Override
            public int addNewAnswer(NewAnswerDTO answer) {
                this.lastNewAnswer = answer;
                return returnGeneratedId;
            }
        }

        private FakeAnswersDAO fakeAnswersDAO;
        private AnswerService answerService;

        @BeforeEach
        void setUp() {
            fakeAnswersDAO = new FakeAnswersDAO();
            answerService = new AnswerService(fakeAnswersDAO);
        }

        @Test
        @DisplayName("getAllAnswersForQuestion maps Answer models to AnswerDTOs")
        void getAllAnswersForQuestion_MapsToDTOs() {
            int questionId = 1;
            LocalDateTime now = LocalDateTime.now();
            fakeAnswersDAO.returnAnswers = List.of(
                    new Answer(1, questionId, 10, "Answer 1", now),
                    new Answer(2, questionId, 20, "Answer 2", now.plusHours(1))
            );

            List<AnswerDTO> dtos = answerService.getAllAnswersForQuestion(questionId);

            assertNotNull(dtos);
            assertEquals(2, dtos.size());

            assertEquals(1, dtos.get(0).id());
            assertEquals(questionId, dtos.get(0).questionId());
            assertEquals(10, dtos.get(0).userId());
            assertEquals("Answer 1", dtos.get(0).message());
            assertEquals(now, dtos.get(0).submissionTime());

            assertEquals(2, dtos.get(1).id());
            assertEquals("Answer 2", dtos.get(1).message());

            assertEquals(questionId, fakeAnswersDAO.lastQuestionIdQuery);
        }

        @Test
        @DisplayName("deleteAnswerById delegates to DAO")
        void deleteAnswerById_DelegatesToDao() {
            int id = 7;
            fakeAnswersDAO.returnDeleteStatus = true;

            boolean result = answerService.deleteAnswerById(id);

            assertTrue(result);
            assertEquals(id, fakeAnswersDAO.lastDeleteId);
        }
    }

    // =========================================================================
    // AnswerController Tests
    // =========================================================================
    @Nested
    @DisplayName("AnswerController Tests")
    class AnswerControllerTests {

        static class FakeAnswerService extends AnswerService {
            List<AnswerDTO> returnAnswers = new ArrayList<>();
            int returnNewAnswerId = 0;
            boolean returnDeleteStatus = false;

            int lastQuestionIdPassed = -1;
            NewAnswerDTO lastNewAnswerPassed = null;
            int lastDeleteIdPassed = -1;

            public FakeAnswerService() {
                super(null);
            }

            @Override
            public List<AnswerDTO> getAllAnswersForQuestion(int questionId) {
                this.lastQuestionIdPassed = questionId;
                return returnAnswers;
            }

            @Override
            public int addNewAnswer(NewAnswerDTO answer) {
                this.lastNewAnswerPassed = answer;
                return returnNewAnswerId;
            }

            @Override
            public boolean deleteAnswerById(int id) {
                this.lastDeleteIdPassed = id;
                return returnDeleteStatus;
            }
        }

        private FakeAnswerService fakeAnswerService;
        private AnswerController answerController;

        @BeforeEach
        void setUp() {
            fakeAnswerService = new FakeAnswerService();
            answerController = new AnswerController(fakeAnswerService);
        }

        @Test
        @DisplayName("getAllQuestions returns list of AnswerDTOs from service")
        void getAllQuestions_ReturnsAnswerDTOList() {
            int questionId = 1;
            List<AnswerDTO> expected = List.of(
                    new AnswerDTO(1, questionId, 10, "First Answer", LocalDateTime.now())
            );
            fakeAnswerService.returnAnswers = expected;

            List<AnswerDTO> actual = answerController.getAllQuestions(questionId);

            assertEquals(expected, actual);
            assertEquals(questionId, fakeAnswerService.lastQuestionIdPassed);
        }

        @Test
        @DisplayName("deleteAnswerById delegates to service and returns status")
        void deleteAnswerById_ReturnsStatus() {
            int answerId = 15;
            fakeAnswerService.returnDeleteStatus = true;

            boolean result = answerController.deleteAnswerById(answerId);

            assertTrue(result);
            assertEquals(answerId, fakeAnswerService.lastDeleteIdPassed);
        }
    }

    // =========================================================================
    // QuestionsDaoJdbc Tests
    // =========================================================================
    @Nested
    @DisplayName("QuestionsDaoJdbc Tests")
    class QuestionsDaoJdbcTests {

        static class QuestionRowData {
            final int id;
            final String title;
            final String description;
            final Timestamp submissionTime;

            QuestionRowData(int id, String title, String description, Timestamp submissionTime) {
                this.id = id;
                this.title = title;
                this.description = description;
                this.submissionTime = submissionTime;
            }
        }

        private final List<QuestionRowData> mockRows = new ArrayList<>();
        private int currentRowIndex;
        private int updateCountResult;
        private int generatedIdResult;
        private SQLException exceptionToThrow;

        private int lastSetIntIndex;
        private int lastSetIntValue;
        private int lastSetStringIndex;
        private String lastSetStringValue;
        private boolean executeQueryCalled;
        private boolean executeUpdateCalled;

        private ResultSet resultSetProxy;
        private PreparedStatement statementProxy;
        private Connection connectionProxy;
        private DatabaseConnection databaseConnection;
        private QuestionsDaoJdbc questionsDaoJdbc;

        @BeforeEach
        void setUp() {
            mockRows.clear();
            currentRowIndex = -1;
            updateCountResult = 0;
            generatedIdResult = 0;
            exceptionToThrow = null;

            lastSetIntIndex = -1;
            lastSetIntValue = -1;
            lastSetStringIndex = -1;
            lastSetStringValue = null;
            executeQueryCalled = false;
            executeUpdateCalled = false;

            InvocationHandler resultSetHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("next".equals(name)) {
                    currentRowIndex++;
                    return currentRowIndex < mockRows.size() || (generatedIdResult > 0 && currentRowIndex == 0);
                }
                if ("getInt".equals(name)) {
                    String col = (String) args[0];
                    if ("id".equals(col)) {
                        if (generatedIdResult > 0) return generatedIdResult;
                        if (currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                            return mockRows.get(currentRowIndex).id;
                        }
                    }
                    return 0;
                }
                if ("getString".equals(name)) {
                    String col = (String) args[0];
                    if ("title".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                        return mockRows.get(currentRowIndex).title;
                    }
                    if ("description".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.size()) {
                        return mockRows.get(currentRowIndex).description;
                    }
                    return null;
                }
                if ("getTimestamp".equals(name)) {
                    String col = (String) args[0];
                    if ("submission_time".equals(col) && currentRowIndex >= 0 && currentRowIndex < mockRows.
                            size()) {
                        return mockRows.get(currentRowIndex).submissionTime;
                    }
                    return null;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockResultSet";
                return defaultReturnValue(method.getReturnType());
            };

            resultSetProxy = (ResultSet) Proxy.newProxyInstance(
                    ResultSet.class.getClassLoader(),
                    new Class<?>[]{ResultSet.class},
                    resultSetHandler
            );

            InvocationHandler statementHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("setInt".equals(name)) {
                    lastSetIntIndex = (int) args[0];
                    lastSetIntValue = (int) args[1];
                    return null;
                }
                if ("setString".equals(name)) {
                    lastSetStringIndex = (int) args[0];
                    lastSetStringValue = (String) args[1];
                    return null;
                }
                if ("executeQuery".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    executeQueryCalled = true;
                    return resultSetProxy;
                }
                if ("executeUpdate".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    executeUpdateCalled = true;
                    return updateCountResult;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockPreparedStatement";
                return defaultReturnValue(method.getReturnType());
            };

            statementProxy = (PreparedStatement) Proxy.newProxyInstance(
                    PreparedStatement.class.getClassLoader(),
                    new Class<?>[]{PreparedStatement.class, Statement.class},
                    statementHandler
            );

            InvocationHandler connectionHandler = (proxy, method, args) -> {
                String name = method.getName();
                if ("createStatement".equals(name) || "prepareStatement".equals(name)) {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    return statementProxy;
                }
                if ("close".equals(name)) return null;
                if ("toString".equals(name)) return "MockConnection";
                return defaultReturnValue(method.getReturnType());
            };

            connectionProxy = (Connection) Proxy.newProxyInstance(
                    Connection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    connectionHandler
            );

            databaseConnection = new DatabaseConnection("", "", "") {
                @Override
                public Connection getConnection() throws SQLException {
                    if (exceptionToThrow != null) throw exceptionToThrow;
                    return connectionProxy;
                }
            };

            questionsDaoJdbc = new QuestionsDaoJdbc(databaseConnection);
        }

        private Object defaultReturnValue(Class<?> type) {
            if (type == boolean.class) return false;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            return null;
        }

        @Test
        @DisplayName("getAllQuestions returns list of questions when query succeeds")
        void getAllQuestions_Success() {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            mockRows.add(new QuestionRowData(1, "Java Title", "Java Desc", Timestamp.valueOf(now)));
            mockRows.add(new QuestionRowData(2, "Spring Title", "Spring Desc", Timestamp.valueOf(now)));

            List<Question> questions = questionsDaoJdbc.getAllQuestions();

            assertNotNull(questions);
            assertEquals(2, questions.size());

            Question first = questions.get(0);
            assertEquals(1, first.id());
            assertEquals("Java Title", first.title());
            assertEquals("Java Desc", first.description());
            assertEquals(now, first.created());

            assertTrue(executeQueryCalled);
        }

        @Test
        @DisplayName("getAllQuestions returns empty list when no rows are found")
        void getAllQuestions_EmptyList() {
            List<Question> questions = questionsDaoJdbc.getAllQuestions();

            assertNotNull(questions);
            assertTrue(questions.isEmpty());
        }

        @Test
        @DisplayName("getAllQuestions throws RuntimeException when SQLException occurs")
        void getAllQuestions_ThrowsExceptionOnSQLException() {
            exceptionToThrow = new SQLException("DB query error");

            assertThrows(RuntimeException.class, () -> questionsDaoJdbc.getAllQuestions());
        }

        @Test
        @DisplayName("getQuestionById returns question when found")
        void getQuestionById_Success() {
            int id = 5;
            LocalDateTime now = LocalDateTime.now().withNano(0);
            mockRows.add(new QuestionRowData(id, "Q Title", "Q Desc", Timestamp.valueOf(now)));

            Question question = questionsDaoJdbc.getQuestionById(id);

            assertNotNull(question);
            assertEquals(id, question.id());
            assertEquals("Q Title", question.title());
            assertEquals("Q Desc", question.description());
            assertEquals(now, question.created());
            assertEquals(1, lastSetIntIndex);
            assertEquals(id, lastSetIntValue);
            assertTrue(executeQueryCalled);
        }

        @Test
        @DisplayName("getQuestionById throws RuntimeException when SQLException occurs")
        void getQuestionById_ThrowsExceptionOnSQLException() {
            exceptionToThrow = new SQLException("Query failure");

            assertThrows(RuntimeException.class, () -> questionsDaoJdbc.getQuestionById(5));
        }

        @Test
        @DisplayName("addNewQuestion returns generated ID on success")
        void addNewQuestion_Success() {
            NewQuestionDTO newQuestion = new NewQuestionDTO("New Title", "New Desc", 3);
            generatedIdResult = 77;

            int actualId = questionsDaoJdbc.addNewQuestion(newQuestion);

            assertEquals(77, actualId);
            assertTrue(executeQueryCalled);
        }

        @Test
        @DisplayName("addNewQuestion throws RuntimeException when SQLException occurs")
        void addNewQuestion_ThrowsExceptionOnSQLException() {
            NewQuestionDTO newQuestion = new NewQuestionDTO("New Title", "New Desc", 3);
            exceptionToThrow = new SQLException("Insert failure");

            assertThrows(RuntimeException.class, () -> questionsDaoJdbc.addNewQuestion(newQuestion));
        }

        @Test
        @DisplayName("deleteQuestionById returns true when deleted")
        void deleteQuestionById_Success() {
            int id = 10;
            updateCountResult = 1;

            boolean result = questionsDaoJdbc.deleteQuestionById(id);

            assertTrue(result);
            assertEquals(1, lastSetIntIndex);
            assertEquals(id, lastSetIntValue);
            assertTrue(executeUpdateCalled);
        }

        @Test
        @DisplayName("deleteQuestionById returns false when not found")
        void deleteQuestionById_NotFound() {
            int id = 999;
            updateCountResult = 0;

            boolean result = questionsDaoJdbc.deleteQuestionById(id);

            assertFalse(result);
            assertEquals(1, lastSetIntIndex);
            assertEquals(id, lastSetIntValue);
        }

        @Test
        @DisplayName("deleteQuestionById throws RuntimeException when SQLException occurs")
        void deleteQuestionById_ThrowsExceptionOnSQLException() {
            exceptionToThrow = new SQLException("Delete failure");

            assertThrows(RuntimeException.class, () -> questionsDaoJdbc.deleteQuestionById(10));
        }
    }

    // =========================================================================
    // QuestionService Tests
    // =========================================================================
    @Nested
    @DisplayName("QuestionService Tests")
    class QuestionServiceTests {

        static class FakeQuestionsDAO implements QuestionsDAO {
            List<Question> returnQuestions = new ArrayList<>();
            Question returnQuestion = null;
            int returnGeneratedId = 0;
            boolean returnDeleteStatus = false;

            int lastGetId = -1;
            int lastDeleteId = -1;
            NewQuestionDTO lastNewQuestion = null;

            @Override
            public List<Question> getAllQuestions() {
                return returnQuestions;
            }

            @Override
            public Question getQuestionById(int id) {
                this.lastGetId = id;
                return returnQuestion;
            }

            @Override
            public int addNewQuestion(NewQuestionDTO question) {
                this.lastNewQuestion = question;
                return returnGeneratedId;
            }

            @Override
            public boolean deleteQuestionById(int id) {
                this.lastDeleteId = id;
                return returnDeleteStatus;
            }

            @Override
            public List<Question> searchQuestions(String searchTerm) {
                return List.of(new Question(1, "test title", "test description", LocalDateTime.now()));
            }
        }

        private FakeQuestionsDAO fakeQuestionsDAO;
        private QuestionService questionService;

        @BeforeEach
        void setUp() {
            fakeQuestionsDAO = new FakeQuestionsDAO();
            questionService = new QuestionService(fakeQuestionsDAO);
        }

        @Test
        @DisplayName("getAllQuestions maps Question models to QuestionDTOs")
        void getAllQuestions_MapsToDTOs() {
            LocalDateTime now = LocalDateTime.now();
            fakeQuestionsDAO.returnQuestions = List.of(
                    new Question(1, "Q1", "Desc1", now),
                    new Question(2, "Q2", "Desc2", now.plusHours(1))
            );

            List<QuestionDTO> dtos = questionService.getAllQuestions();

            assertNotNull(dtos);
            assertEquals(2, dtos.size());
            assertEquals(1, dtos.get(0).id());
            assertEquals("Q1", dtos.get(0).title());
            assertEquals("Desc1", dtos.get(0).description());
            assertEquals(now, dtos.get(0).created());
        }

        @Test
        @DisplayName("getQuestionById maps Question model to QuestionDTO")
        void getQuestionById_MapsToDTO() {
            int id = 5;
            LocalDateTime now = LocalDateTime.now();
            fakeQuestionsDAO.returnQuestion = new Question(id, "Title 5", "Desc 5", now);

            QuestionDTO dto = questionService.getQuestionById(id);

            assertNotNull(dto);
            assertEquals(id, dto.id());
            assertEquals("Title 5", dto.title());
            assertEquals("Desc 5", dto.description());
            assertEquals(now, dto.created());
            assertEquals(id, fakeQuestionsDAO.lastGetId);
        }

        @Test
        @DisplayName("deleteQuestionById delegates to DAO")
        void deleteQuestionById_DelegatesToDao() {
            int id = 8;
            fakeQuestionsDAO.returnDeleteStatus = true;

            boolean result = questionService.deleteQuestionById(id);

            assertTrue(result);
            assertEquals(id, fakeQuestionsDAO.lastDeleteId);
        }

        @Test
        @DisplayName("addNewQuestion delegates to DAO")
        void addNewQuestion_DelegatesToDao() {
            NewQuestionDTO dto = new NewQuestionDTO("Title", "Description", 2);
            fakeQuestionsDAO.returnGeneratedId = 55;

            int resultId = questionService.addNewQuestion(dto);

            assertEquals(55, resultId);
            assertEquals(dto, fakeQuestionsDAO.lastNewQuestion);
        }
    }

    // =========================================================================
    // QuestionController Tests
    // =========================================================================
    @Nested
    @DisplayName("QuestionController Tests")
    class QuestionControllerTests {

        static class FakeQuestionService extends QuestionService {
            List<QuestionDTO> returnQuestions = new ArrayList<>();
            QuestionDTO returnQuestion = null;
            int returnNewQuestionId = 0;
            boolean returnDeleteStatus = false;

            int lastGetIdPassed = -1;
            NewQuestionDTO lastNewQuestionPassed = null;
            int lastDeleteIdPassed = -1;

            public FakeQuestionService() {
                super(null);
            }

            @Override
            public List<QuestionDTO> getAllQuestions() {
                return returnQuestions;
            }

            @Override
            public QuestionDTO getQuestionById(int id) {
                this.lastGetIdPassed = id;
                return returnQuestion;
            }

            @Override
            public int addNewQuestion(NewQuestionDTO question) {
                this.lastNewQuestionPassed = question;
                return returnNewQuestionId;
            }

            @Override
            public boolean deleteQuestionById(int id) {
                this.lastDeleteIdPassed = id;
                return returnDeleteStatus;
            }
        }

        private FakeQuestionService fakeQuestionService;
        private QuestionController questionController;

        @BeforeEach
        void setUp() {
            fakeQuestionService = new FakeQuestionService();
            questionController = new QuestionController(fakeQuestionService);
        }

        @Test
        @DisplayName("getAllQuestions returns list of QuestionDTOs from service")
        void getAllQuestions_ReturnsQuestionDTOList() {
            List<QuestionDTO> expected = List.of(
                    new QuestionDTO(1, "Title 1", "Desc 1", LocalDateTime.now())
            );
            fakeQuestionService.returnQuestions = expected;

            List<QuestionDTO> actual = questionController.getAllQuestions();

            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("getQuestionById returns QuestionDTO from service")
        void getQuestionById_ReturnsQuestionDTO() {
            int id = 3;
            QuestionDTO expected = new QuestionDTO(id, "Title 3", "Desc 3", LocalDateTime.now());
            fakeQuestionService.returnQuestion = expected;

            QuestionDTO actual = questionController.getQuestionById(id);

            assertEquals(expected, actual);
            assertEquals(id, fakeQuestionService.lastGetIdPassed);
        }

        @Test
        @DisplayName("addNewQuestion delegates to service and returns created id")
        void addNewQuestion_ReturnsCreatedId() {
            NewQuestionDTO newQuestion = new NewQuestionDTO("New Q", "New Desc", 1);
            fakeQuestionService.returnNewQuestionId = 99;

            int actualId = questionController.addNewQuestion(newQuestion);

            assertEquals(99, actualId);
            assertEquals(newQuestion, fakeQuestionService.lastNewQuestionPassed);
        }

        @Test
        @DisplayName("deleteQuestionById delegates to service and returns status")
        void deleteQuestionById_ReturnsStatus() {
            int questionId = 20;
            fakeQuestionService.returnDeleteStatus = true;

            boolean result = questionController.deleteQuestionById(questionId);

            assertTrue(result);
            assertEquals(questionId, fakeQuestionService.lastDeleteIdPassed);
        }
    }

    // =========================================================================
    // UserService Tests
    // =========================================================================
    @Nested
    @DisplayName("UserService Tests")
    class UserServiceTests {

        static class FakeUsersDAO implements UsersDAO {
            int returnPoints = 0;
            int lastPointsIdQuery = -1;
            User returnUser = null;
            String lastUsernameQuery = null;
            int returnAddNewUserResult = 0;
            NewUserDTO lastNewUserPassed = null;

            @Override
            public int addNewUser(NewUserDTO user) {
                this.lastNewUserPassed = user;
                return returnAddNewUserResult;
            }

            @Override
            public User getUserByUsername(String username) {
                this.lastUsernameQuery = username;
                return returnUser;
            }

            @Override
            public int getUserByPoints(int id) {
                this.lastPointsIdQuery = id;
                return returnPoints;
            }
        }

        private FakeUsersDAO fakeUsersDAO;
        private UserService userService;

        @BeforeEach
        void setUp() {
            fakeUsersDAO = new FakeUsersDAO();
            userService = new UserService(fakeUsersDAO);
        }

        @Test
        @DisplayName("getPointsById returns user points from DAO")
        void getPointsById_ReturnsPointsFromDao() {
            int userId = 42;
            int expectedPoints = 150;
            fakeUsersDAO.returnPoints = expectedPoints;

            int actualPoints = userService.getPointsById(userId);

            assertEquals(expectedPoints, actualPoints);
            assertEquals(userId, fakeUsersDAO.lastPointsIdQuery);
        }
    }
}

