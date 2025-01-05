package org.example.ridebuddies;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "RideBuddies_Database";
        String databaseUser = "postgres";
        String databasePassword = "andreea111";
        String url = "jdbc:postgresql://localhost:5432/" + databaseName;

        try {
            Class.forName("org.postgresql.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("Database connected successfully:)");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database:(");
        }
        return databaseLink;
    }
}
