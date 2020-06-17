package ru.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class AppStart extends Application {

    @Override
    public void start(final Stage stage) throws IOException {
        final Parent panel = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        final Scene scene = new Scene(panel, 1200, 700);
        stage.setTitle("Client cloud");
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}