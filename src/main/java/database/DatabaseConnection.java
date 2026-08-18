package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private final Properties props = new Properties();
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Could not load application.properties file.");
        }
    }

    public Connection getConnection() throws SQLException {
        url = props.getProperty("askmate.database.url", "");
        username = props.getProperty("askmate.database.username");
        password = props.getProperty("askmate.database.password");
        return DriverManager.getConnection(url, username, password);
    }
}
