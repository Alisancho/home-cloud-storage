package ru.client.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import ru.client.entity.OneFileFX;
import ru.client.service.WorkWithFilesServiceImpl;
import ru.client.service.netty.NettyClientServiceImpl;
import ru.home.api.entity.ErrorType;
import ru.home.api.entity.auth.UserCloud;
import ru.home.api.entity.catalog.ContentsDirectory;
import ru.home.api.entity.data.OneTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static ru.home.api.entity.ErrorType.ERROR_MESS;
import static ru.home.api.entity.TaskType.*;

public class MainController implements MainCiontrollerInt {
    public static NettyClientServiceImpl nioClient;
//    public static NettyClient nettyClient;

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

        /**
         * Кнопка коннекта
         */
        connectButton.setOnAction(event -> {
            try {
                 nioClient = new NettyClientServiceImpl(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
                final var userCloud = new UserCloud(loginTextField.getText(), passTextField.getText());
                mainBox.getChildren().remove(sbur);
                disconnectButton.setVisible(true);

                WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
                this.nioClient.sendMsg(new OneTask(OPTIONS, Option.none(), Option.none()));

                CompletableFuture.runAsync(() -> {
                    try {
                        while (true) {
                            final var newMess = nioClient.readObject();
                            if (newMess instanceof ContentsDirectory contentsDirectory) {
                                System.out.println("Получен список файлов" + contentsDirectory.toString());
                                WorkWithFilesServiceImpl.getFiles(filesListServer, contentsDirectory.files());

                            }
                            if (newMess instanceof OneTask oneTask) {
                                switch (oneTask.task()) {
                                    case GET -> {
                                        Files.write(Paths.get(localAddressTextField.getText() + "/" + oneTask.fileName().get()), oneTask.data().get(), StandardOpenOption.CREATE);
                                        WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
                                    }
                                    default -> {

                                    }
                                }
                            }
                            if (newMess instanceof ErrorType errorType) {
                                switch (errorType) {
                                    case ERROR_NO -> {
                                        this.nioClient.sendMsg(new OneTask(OPTIONS, Option.none(), Option.none()));
                                    }
                                    case ERROR_MESS -> {
                                        System.out.println(ERROR_MESS.errorType());
                                    }
                                    default -> {
                                    }
                                }
                            }
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        nioClient.close();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        disconnectButton.setOnAction(event -> {
            mainBox.getChildren().add(0, sbur);
            disconnectButton.setVisible(false);
            filesListServer.remove(0, filesListServer.size());
        });

        upButton.setOnAction(event -> {
            this.nioClient.sendMsg(new OneTask(OPTIONS, Option.none(), Option.none()));
        });

        goToCatalog.setOnAction(event -> {
            WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
        });


        putToServerButton.setOnAction(event -> {
            try {
                final var fileLocal = tableHome.getSelectionModel().getSelectedItem().getValue();
                if (fileLocal.fileType.get().equals("FILE")) {
                    final var arrB = Files.readAllBytes(Paths.get(localAddressTextField.getText() + "/" + fileLocal.fileName.get()));
                    this.nioClient.sendMsg(
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
                    this.nioClient.sendMsg(
                            new OneTask(GET, Option.of(fileLocal.fileName.get()), Option.none())
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        deleteFromServerButton.setOnAction(event -> {
            this.nioClient.sendMsg(
                    new OneTask(DELETE, Option.of(tableServer.getSelectionModel().getSelectedItem().getValue().fileName.get()), Option.none())
            );
        });
    }
}
