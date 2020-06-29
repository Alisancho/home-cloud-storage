package ru.client.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.vavr.Function1;
import io.vavr.control.Option;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ru.client.core.Functions;
import ru.client.entity.OneFileFX;
import ru.client.service.WorkWithFilesServiceImpl;
import ru.client.service.netty.NettyClient;
import ru.home.api.entity.auth.UserCloud;
import ru.home.api.entity.data.OneTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static ru.home.api.entity.TaskType.*;

@Slf4j
public class MainController implements MainCiontrollerInt {
    public static NettyClient nettyClient;

    final private ObservableList<OneFileFX> filesListClient = FXCollections.observableArrayList();
    final private TreeItem<OneFileFX> rootClient = new RecursiveTreeItem<OneFileFX>(filesListClient, RecursiveTreeObject::getChildren);
    final private JFXTreeTableColumn<OneFileFX, String> typeColClient = new JFXTreeTableColumn<>(COL_1);
    final private JFXTreeTableColumn<OneFileFX, String> nameColClient = new JFXTreeTableColumn<>(COL_2);
    final private JFXTreeTableColumn<OneFileFX, String> sizeColClient = new JFXTreeTableColumn<>(COL_3);

    final private ObservableList<OneFileFX> filesListServer = FXCollections.observableArrayList();
    final private TreeItem<OneFileFX> rootServer = new RecursiveTreeItem<OneFileFX>(filesListServer, RecursiveTreeObject::getChildren);
    final private JFXTreeTableColumn<OneFileFX, String> typeColServer = new JFXTreeTableColumn<>(COL_1);
    final private JFXTreeTableColumn<OneFileFX, String> nameColServer = new JFXTreeTableColumn<>(COL_2);
    final private JFXTreeTableColumn<OneFileFX, String> sizeColServer = new JFXTreeTableColumn<>(COL_3);

    @FXML
    private JFXButton putToServerButton;

    @FXML
    private JFXButton getFromServerButton;

    @FXML
    private JFXButton deleteFromServerButton;

    @FXML
    private JFXButton connectButton;

    @FXML
    private JFXButton disconnectButton;

    @FXML
    private JFXButton upButton;

    @FXML
    private JFXButton goToCatalog;

    @FXML
    private JFXButton deleteFileClientButton;

    @FXML
    private JFXTextField localAddressTextField;

    @FXML
    private JFXTextField loginTextField;

    @FXML
    private JFXTextField hostTextField;

    @FXML
    private JFXTextField portTextField;

    @FXML
    private JFXPasswordField passTextField;

    @FXML
    private JFXTreeTableView<OneFileFX> tableHome;

    @FXML
    private JFXTreeTableView<OneFileFX> tableServer;

    @FXML
    private ResourceBundle resources;

    @FXML
    private VBox sbur;

    @FXML
    private HBox mainBox;

    @FXML
    void initialize() throws InterruptedException {
        Function1<Throwable, Class<Void>> funDis = Functions.functionDisconnection.apply(putToServerButton, getFromServerButton, deleteFromServerButton, mainBox, disconnectButton, filesListServer, sbur);
        Function1<Throwable, Class<Void>> funCon = Functions.functionConnection.apply(putToServerButton, getFromServerButton, deleteFromServerButton, mainBox, disconnectButton, filesListServer, sbur);

        localAddressTextField.setText(System.getProperty("user.dir"));

        typeColClient.setPrefWidth(100);
        nameColClient.setPrefWidth(400);
        sizeColClient.setPrefWidth(150);

        typeColServer.setPrefWidth(100);
        nameColServer.setPrefWidth(400);
        sizeColServer.setPrefWidth(150);

        typeColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileType;
            }
        });

        nameColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileName;
            }
        });

        sizeColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileSize;
            }
        });

        typeColServer.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileType;
            }
        });

        nameColServer.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileName;
            }
        });

        sizeColServer.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFileFX, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFileFX, String> param) {
                return param.getValue().getValue().fileSize;
            }
        });

        tableHome.getColumns().setAll(typeColClient, nameColClient, sizeColClient);
        tableHome.prefHeight(60);
        tableHome.setRoot(rootClient);
        tableHome.setShowRoot(false);

        tableServer.getColumns().setAll(typeColServer, nameColServer, sizeColServer);
        tableServer.prefHeight(60);
        tableServer.setRoot(rootServer);
        tableServer.setShowRoot(false);

        putToServerButton.setDisable(true);
        getFromServerButton.setDisable(true);
        deleteFromServerButton.setDisable(true);

        /**
         * Кнопка коннекта
         */
        connectButton.setOnAction(event -> {
            if (!"".equals(loginTextField.getText())) {
                var user = new UserCloud(loginTextField.getText(), passTextField.getText());
                CompletableFuture.runAsync(() -> {
                    try {
                        nettyClient = new NettyClient(
                                hostTextField.getText(),
                                Integer.parseInt(portTextField.getText()),
                                filesListServer,
                                filesListClient,
                                localAddressTextField,
                                funCon
                        );
                        nettyClient.run(user);
                    } catch (java.net.ConnectException e) {
                        log.error(e.getMessage());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        funDis.apply(null);
                    } finally {
                        nettyClient.stop();
                    }
                });
            } else {
                log.error("ededededededededededededed");
            }

        });

        disconnectButton.setOnAction(event -> {
            nettyClient.stop();
        });

        upButton.setOnAction(event -> {
            nettyClient.sendMess(new OneTask(OPTIONS, Option.none(), Option.none()));
        });

        goToCatalog.setOnAction(event -> {
            WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
        });


        putToServerButton.setOnAction(event -> {
            try {
                final var fileLocal = tableHome.getSelectionModel().getSelectedItem().getValue();
                if (fileLocal.fileType.get().equals("FILE")) {
                    final var arrB = Files.readAllBytes(Paths.get(localAddressTextField.getText() + "/" + fileLocal.fileName.get()));
                    nettyClient.sendMess(
                            new OneTask(PUT, Option.of(fileLocal.fileName.get()), Option.of(arrB))
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        getFromServerButton.setOnAction(event -> {
            try {
                final var fileLocal = tableServer.getSelectionModel().getSelectedItem().getValue();
                if (fileLocal.fileType.get().equals("FILE")) {
                    nettyClient.sendMess(
                            new OneTask(GET, Option.of(fileLocal.fileName.get()), Option.none())
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        deleteFromServerButton.setOnAction(event -> {
            nettyClient.sendMess(
                    new OneTask(DELETE, Option.of(tableServer.getSelectionModel().getSelectedItem().getValue().fileName.get()), Option.none())
            );
        });

        deleteFileClientButton.setOnAction(event -> {
            try {
                final var fileLocal = tableHome.getSelectionModel().getSelectedItem().getValue();
                Files.delete(Paths.get(localAddressTextField.getText() + "/" + fileLocal.fileName.get()));
                WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
    }
}
