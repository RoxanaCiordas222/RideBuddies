package org.example.ridebuddies;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import java.io.IOException;

public class WelcomeController {
    private Stage stage;
    private Parent root;

    /**
     * switch to register and login pages
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
    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root =FXMLLoader.load(getClass().getResource("/com/example/ridebuddies/login.fxml"));
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    }

