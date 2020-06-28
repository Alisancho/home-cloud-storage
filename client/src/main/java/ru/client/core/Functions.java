package ru.client.core;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import io.vavr.Function4;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.client.entity.OneFileFX;
import ru.client.service.netty.NettyClientServiceImpl;

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

    public static void errorConnect(JFXTreeTableView<OneFileFX> tableHome, NettyClientServiceImpl nioClient) {
    }
}
