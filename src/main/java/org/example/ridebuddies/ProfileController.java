package org.example.ridebuddies;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileController {

    @FXML
    private Button backToMain;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField username;
    @FXML
    private TextField firstname;
    @FXML
    private TextField lastname;
    @FXML
    private TextField email;
    @FXML
    private TextField age;
    @FXML
    private TextField description;
    @FXML
    private ListView<String> friendListView;

    private Stage stage;
    private boolean isEditing = false;
    private int currentUserId;
    @FXML
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    @FXML
    public void initialize() {
        loadUserData();
        toggleEditable(false);
        loadUserFriends();
    }

    private void loadUserData() {
        User currentUser = LoggedUser.getUser();
        if (currentUser != null) {
            username.setText(currentUser.getUsername());
            firstname.setText(currentUser.getFirstName());
            lastname.setText(currentUser.getLastName());
            email.setText(currentUser.getEmail());
            age.setText(String.valueOf(currentUser.getAge()));
            description.setText("");
        } else {
            System.err.println("No user is currently logged in!");
        }
    }

    private void loadUserFriends() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDb = databaseConnection.getConnection();

        String query = "SELECT person.username, person.firstname, person.age FROM friends " +
                "JOIN person ON friends.friend_id = person.person_id " +
                "WHERE friends.user_id = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(query)) {
            preparedStatement.setInt(1, currentUserId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String friendUsername = resultSet.getString("username");
                String friendFirstname = resultSet.getString("firstname");
                int friendAge = resultSet.getInt("age");
                String friendDetails = friendUsername + " - " + friendFirstname + ", Age: " + friendAge;
                friendListView.getItems().add(friendDetails);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * when the edit button is pressed, some textfields become editable;
     * @param editable
     */
    private void toggleEditable(boolean editable) {
        firstname.setEditable(editable);
        lastname.setEditable(editable);
        age.setEditable(editable);
        description.setEditable(editable);
        email.setEditable(false);
    }

    @FXML
    public void switchToMain(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/mainpage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void onEditButtonClick() {
        if (isEditing) {
            if (saveChangesToDatabase()) {
                User currentUser = LoggedUser.getUser();
                if (currentUser != null) {
                    currentUser.setFirstName(firstname.getText());
                    currentUser.setLastName(lastname.getText());
                    currentUser.setEmail(email.getText());
                    currentUser.setAge(Integer.parseInt(age.getText()));
                }
            }
        }
        isEditing = !isEditing;
        toggleEditable(isEditing);
        editButton.setText(isEditing ? "Save" : "Edit");
    }

    private boolean saveChangesToDatabase() {
        String query = "UPDATE person SET firstname = ?, lastname = ?, email = ?, age = ? WHERE username = ?";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, firstname.getText());
            statement.setString(2, lastname.getText());
            statement.setString(3, email.getText());
            statement.setInt(4, Integer.parseInt(age.getText()));
            statement.setString(5, username.getText());
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void onDeleteButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String query = "DELETE FROM person WHERE username = ?";
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username.getText());
                statement.executeUpdate();
                switchToMain(null);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
