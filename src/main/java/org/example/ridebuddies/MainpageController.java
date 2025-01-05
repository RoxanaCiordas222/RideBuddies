package org.example.ridebuddies;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainpageController {
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<String> listView;
    @FXML
    private Label nameFriendLabel;
    @FXML
    private Label usernameFriendLabel;
    @FXML
    private Label ageFriendLabel;
    @FXML
    private Button addFriendButton;
    @FXML
    private Label confirmeFriendLabel;

    private Stage stage;
    private int currentUserId = -1; // Ensure a valid default value

    @FXML
    public void switchToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/profile.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * used fo searching for friends by username
     * @param actionEvent
     */
    @FXML
    public void searchButtonOnAction(ActionEvent actionEvent) {
        String searchText = searchTextField.getText().trim();

        if (searchText.isEmpty()) {
            listView.getItems().clear();
            listView.getItems().add("No Search Results");
            return;
        }
        try {
            searchUsers(searchText);
        } catch (SQLException e) {
            e.printStackTrace();
            listView.getItems().add("Error while searching users.");
        }
    }

    private void searchUsers(String searchText) throws SQLException {
        listView.getItems().clear();
        DatabaseConnection databaseConnection = new DatabaseConnection();//connect tot he databse
        Connection connectDb = databaseConnection.getConnection();

        String sql = "SELECT username, firstname, lastname FROM person WHERE username LIKE ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean resultFound = false;

            while (resultSet.next()) {
                resultFound = true;
                String username = resultSet.getString("username");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                listView.getItems().add(username + ", " + firstname + " " + lastname);
            }
            if (!resultFound) {
                listView.getItems().add("No users found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listView.getItems().add("Error loading users. Please try again.");
        }
    }

    @FXML
    public void initialize() {
        nameFriendLabel.setVisible(false);
        usernameFriendLabel.setVisible(false);
        ageFriendLabel.setVisible(false);
        confirmeFriendLabel.setVisible(false);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("No users found")) {
                String selectedUsername = newValue.split(",")[0].trim();
                try {
                    loadFriendDetails(selectedUsername);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void loadFriendDetails(String username) throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDb = databaseConnection.getConnection();

        String sql = "SELECT firstname, lastname, username, age FROM person WHERE username = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(sql)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int age = resultSet.getInt("age");
                nameFriendLabel.setVisible(true);//set the labels as visible and put their text
                usernameFriendLabel.setVisible(true);
                ageFriendLabel.setVisible(true);
                nameFriendLabel.setText(firstname + " " + lastname);
                usernameFriendLabel.setText(username);
                ageFriendLabel.setText(String.valueOf(age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addFriendButtonOnAction(ActionEvent event) {
        if (currentUserId == -1) {
            confirmeFriendLabel.setText("Please log in first.");//if the currentid is not set which is set when logging in
            confirmeFriendLabel.setVisible(true);
            return;
        }

        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.equals("No users found")) {
            confirmeFriendLabel.setText("No valid friend selected.");
            confirmeFriendLabel.setVisible(true);
            return;
        }

        String selectedUsername = selectedItem.split(",")[0].trim();
        try {
            addFriendToDatabase(selectedUsername);
            confirmeFriendLabel.setText("Friend added successfully!");
            confirmeFriendLabel.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            confirmeFriendLabel.setText("Failed to add friend.");
            confirmeFriendLabel.setVisible(true);
        }
    }

    /**
     * add friendships in the friends table by their id
     * @param username
     * @throws SQLException
     */
    private void addFriendToDatabase(String username) throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDb = databaseConnection.getConnection();

        String getFriendIdQuery = "SELECT person_id FROM person WHERE username = ?";
        int friendId = 0;

        try (PreparedStatement preparedStatement = connectDb.prepareStatement(getFriendIdQuery)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                friendId = resultSet.getInt("person_id");
            } else {
                System.out.println("Friend not found in the database.");
                return;
            }
        }

        if (currentUserId == 0 || friendId == 0) {
            System.out.println("Invalid user or friend ID.");
            return;
        }

        String insertQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, currentUserId);
            preparedStatement.setInt(2, friendId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Friend successfully added!");
            } else {
                System.out.println("Failed to add friend.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error inserting into friends table.", e);
        }
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}
