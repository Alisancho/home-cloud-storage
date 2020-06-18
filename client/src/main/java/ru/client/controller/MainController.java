package ru.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ResourceBundle;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private VBox sbur;

    @FXML
    private JFXButton connectButton;

    @FXML
    private JFXButton disconnectButton;

    @FXML
    private HBox mainBox;

    @FXML
    void initialize() throws InterruptedException {
        disconnectButton.setVisible(false);

        connectButton.setOnAction(event -> {
            mainBox.getChildren().remove(sbur);
            disconnectButton.setVisible(true);
        });

        disconnectButton.setOnAction(event -> {
            mainBox.getChildren().add(0,sbur);
            disconnectButton.setVisible(false);
        });

    }
}
