package org.example.ridebuddies;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Label invalidLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        invalidLabel.setVisible(false);
    }

    public void setCancelButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction(ActionEvent actionEvent) {
        invalidLabel.setVisible(false);
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin(actionEvent);
        } else {
            invalidLabel.setText("Please enter your username and password.");
            invalidLabel.setVisible(true);
        }
    }

    public void validateLogin(ActionEvent actionEvent) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDb = databaseConnection.getConnection();

        String query = "SELECT * FROM person WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(query)) {
            preparedStatement.setString(1, usernameField.getText());
            preparedStatement.setString(2, passwordField.getText());
            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next()) {
                int personId = queryResult.getInt("person_id");

                User loggedInUser = new User(
                        queryResult.getString("firstname"),
                        queryResult.getString("lastname"),
                        queryResult.getString("username"),
                        queryResult.getString("password"),
                        queryResult.getString("email"),
                        queryResult.getInt("age")
                );

                LoggedUser.setUser(loggedInUser);

                invalidLabel.setText("Login successful!");
                invalidLabel.setVisible(true);
                switchToMainpage(actionEvent, personId);//switch to the main page
            } else {
                invalidLabel.setText("Invalid username or password.");
                invalidLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            invalidLabel.setText("An error occurred. Please try again.");
            invalidLabel.setVisible(true);
        }
    }

    /**
     * switches to the mainpage scene
     * @param event
     * @param personId
     * @throws IOException
     */
    public void switchToMainpage(ActionEvent event, int personId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ridebuddies/mainpage.fxml"));
        Parent root = loader.load();

        MainpageController mainpageController = loader.getController();
        mainpageController.setCurrentUserId(personId);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * switches to thw welcome scene
     * @param event
     * @throws IOException
     */
    public void switchToWelcome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/hello-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * switches to the register scene
     * @param event
     * @throws IOException
     */
    public void switchToRegister(ActionEvent event) throws IOException {
        Parent root =FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/register.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
