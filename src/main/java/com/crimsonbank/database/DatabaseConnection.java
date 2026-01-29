package com.crimsonbank.database;

import com.crimsonbank.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/crimsonbank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private DatabaseConnection() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Failed to establish database connection: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() throws DatabaseException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Connection is closed: " + e.getMessage(), e);
        }
        return connection;
    }

    public void closeConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error closing connection: " + e.getMessage(), e);
        }
    }

}
