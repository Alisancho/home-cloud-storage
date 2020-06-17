package ru.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppStart extends Application {

    @Override
    public void start(final Stage stage) {
        final var javaVersion = System.getProperty("java.version");
        final var javafxVersion = System.getProperty("javafx.version");
        final var l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        final var scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}