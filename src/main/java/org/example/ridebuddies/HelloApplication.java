package org.example.ridebuddies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/ridebuddies/hello-view.fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IllegalStateException("FXML file not found!");
        }
        Scene scene = new Scene(fxmlLoader.load(), 720, 540);
        stage.setScene(scene);
        stage.setTitle("Hello!");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}