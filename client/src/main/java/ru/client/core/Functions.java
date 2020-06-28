package ru.client.core;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function7;
import io.vavr.Function8;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.client.entity.OneFileFX;

public class Functions {
    /**
     * Функция для дисконнекта
     */
    public static Function4<HBox, JFXButton, ObservableList<OneFileFX>, VBox, Class<Void>> errorConnect = (mainBox, disconnectButton, filesListServer, sbur) -> {
        mainBox.getChildren().add(0, sbur);
        disconnectButton.setVisible(false);
        filesListServer.remove(0, filesListServer.size());
        return Void.TYPE;
    };


    public static Function8<JFXButton, JFXButton, JFXButton, HBox, JFXButton, ObservableList<OneFileFX>, VBox, Throwable, Class<Void>> functionConnection = (putToServerButton, getFromServerButton, deleteFromServerButton, mainBox, disconnectButton, filesListServer, sbur, error) -> {
        Platform.runLater(
                () -> {
                    mainBox.getChildren().remove(sbur);
                    putToServerButton.setDisable(false);
                    getFromServerButton.setDisable(false);
                    deleteFromServerButton.setDisable(false);
                    disconnectButton.setVisible(true);
                }
        );
        return Void.TYPE;
    };

    public static Function8<JFXButton, JFXButton, JFXButton, HBox, JFXButton, ObservableList<OneFileFX>, VBox, Throwable, Class<Void>> functionDisconnection = (putToServerButton, getFromServerButton, deleteFromServerButton, mainBox, disconnectButton, filesListServer, sbur, error) -> {
        Platform.runLater(
                () -> {
                    putToServerButton.setDisable(true);
                    getFromServerButton.setDisable(true);
                    deleteFromServerButton.setDisable(true);
                    mainBox.getChildren().add(0, sbur);
                    disconnectButton.setVisible(false);
                    filesListServer.remove(0, filesListServer.size());
                }
        );
        return Void.TYPE;
    };

}
