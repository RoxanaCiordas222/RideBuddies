package org.example.ridebuddies;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private Button closeButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label registerLabel;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField confirmpasswordTextField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField ageTextField;
    @FXML
    private Label ageLabel;

    private Stage stage;

    /**
     * all the switch to are the same as in the login and welcome controllers
     * @param event
     * @throws IOException
     */
    @FXML
    public void switchToWelcome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/hello-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * set the neccesary labels as non-visible
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirmPasswordLabel.setVisible(false);
        registerLabel.setText("");
        ageLabel.setVisible(false);
        loginButton.setVisible(false);
        emailLabel.setVisible(false);
    }

    @FXML
    public void registerButtonOnAction(ActionEvent event) {
        confirmPasswordLabel.setVisible(false);
        ageLabel.setVisible(false);

        if (!passwordTextField.getText().equals(confirmpasswordTextField.getText())) {//check the confirm password field
            confirmPasswordLabel.setText("Passwords do not match");
            confirmPasswordLabel.setVisible(true);
            return;
        }
        //check the username,firstname and last name fields
        if (firstnameTextField.getText().isEmpty() || lastnameTextField.getText().isEmpty() || usernameTextField.getText().isEmpty()) {
            registerLabel.setText("Please fill in all required fields.");
            registerLabel.setVisible(true);
            return;
        }
        try {
            saveUserToDatabase();
        } catch (NumberFormatException e) {
            registerLabel.setText("Please enter valid data.");
            registerLabel.setVisible(true);
        }
    }

    private void saveUserToDatabase() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDb = databaseConnection.getConnection();

        try {
            int age = Integer.parseInt(ageTextField.getText().trim());
            User user = new User(
                    firstnameTextField.getText().trim(),
                    lastnameTextField.getText().trim(),
                    usernameTextField.getText().trim(),
                    passwordTextField.getText(),
                    emailTextField.getText().trim(),
                    age
            );
            //validate the age and the email
            if (!user.isValidEmail()) {
                emailLabel.setVisible(true);
                emailLabel.setText("Please enter a valid email address.");
                return;
            }else{
                emailLabel.setVisible(false);
            }
            if (!user.isAgeValid()) {
                ageLabel.setVisible(true);
                ageLabel.setText("You must be at least 18 years old.");
                return;
            } else {
                ageLabel.setVisible(false);
            }

            String insertQuery = "INSERT INTO person (firstname, lastname, username, email, age, password) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connectDb.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getUsername());
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.setInt(5, user.getAge());
                preparedStatement.setString(6, user.getPassword());

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    registerLabel.setVisible(true);
                    loginButton.setVisible(true);
                    registerLabel.setText("Successfully registered.");
                } else {
                    registerLabel.setVisible(true);
                    registerLabel.setText("Something went wrong.");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void handleSQLException(SQLException e) {
        if ("23505".equals(e.getSQLState()) || "23000".equals(e.getSQLState())) {
            if (e.getMessage().contains("unique_username")) {
                registerLabel.setText("Username already exists");
                registerLabel.setVisible(true);
            } else if (e.getMessage().contains("unique_email")) {
                registerLabel.setText("Email already exists");
                registerLabel.setVisible(true);
            } else {
                registerLabel.setText("Registration failed due to a database error.");
                registerLabel.setVisible(true);
            }
        } else {
            e.printStackTrace();
            registerLabel.setText("An error occurred. Please try again.");
            registerLabel.setVisible(true);
        }
    }
}
