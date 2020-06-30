package ru.client.core;

import com.jfoenix.controls.JFXButton;
import io.vavr.Function8;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.client.entity.OneFileFX;

public class Functions {

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
