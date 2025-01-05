package org.example.ridebuddies.connection;
import java.sql.*;

public class ConnectionFactory {

    public static final String DBNAME = "test-person";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String DBURL = "jdbc:postgresql://localhost:5432/test-person";
    private static final String USER = "test_oop";
    private static final String PASS = "test_oop";

    private static final ConnectionFactory singleInstance = new ConnectionFactory();

    private ConnectionFactory() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(DBURL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() {
        return singleInstance.createConnection();
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
