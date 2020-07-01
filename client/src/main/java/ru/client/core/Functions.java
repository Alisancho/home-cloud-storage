package ru.client.core;

import com.jfoenix.controls.JFXButton;
import io.vavr.Function8;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import ru.client.entity.OneFileFX;
import ru.home.api.entity.catalog.FileType;
import ru.home.api.entity.catalog.OneServerFile;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    /**
     * Функция для заполнетни таблиц
     * М для промежуточний данныз
     */
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


    /**
     * Заполняем таблицу клиента
     */
    public static void getFiles(final @NotNull ObservableList<OneFileFX> obList,
                                final @NotNull String filePath) {
        CompletableFuture.runAsync(() -> {
            try {
                obList.remove(0, obList.size());
                final var files = new File(filePath).listFiles();
                Platform.runLater(
                        () -> {
                            Arrays.stream(files).forEach(file ->
                                    obList.add(new OneFileFX(FileType.getTypeFile(file), file.getName(), String.format("%.2f", (double) file.length() / 1024) + " kb"))
                            );
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Заполняем таблицу сервера
     *
     * @param obList
     * @param fileSet
     */
    public static void getFiles(final @NotNull ObservableList<OneFileFX> obList,
                                final @NotNull Set<OneServerFile> fileSet) {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(
                    () -> {
                        obList.remove(0, obList.size());
                        fileSet.forEach(l ->
                                obList.add(new OneFileFX(l.fileType(), l.nameFile(), l.sizeFile())));
                    }
            );
        });
    }

}
