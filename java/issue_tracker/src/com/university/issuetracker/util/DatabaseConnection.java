package com.university.issuetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/issue_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j.jar to the lib folder.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.close();
        }
    }
}
