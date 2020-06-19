package ru.client.controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import ru.client.entity.OneFile;
import ru.client.service.WorkWithFilesServiceImpl;

import java.util.ResourceBundle;

public class MainController {
    private String pathLocal;
    final ObservableList<OneFile> filesListClient = FXCollections.observableArrayList();
    final TreeItem<OneFile> rootClient = new RecursiveTreeItem<OneFile>(filesListClient, RecursiveTreeObject::getChildren);
    final JFXTreeTableColumn<OneFile, String> typeColClient = new JFXTreeTableColumn<>("Type");
    final JFXTreeTableColumn<OneFile, String> nameColClient = new JFXTreeTableColumn<>("File name");
    final JFXTreeTableColumn<OneFile, String> sizeColClient = new JFXTreeTableColumn<>("Size");

    final ObservableList<OneFile> filesListServer = FXCollections.observableArrayList();
    final TreeItem<OneFile> rootServer = new RecursiveTreeItem<OneFile>(filesListClient, RecursiveTreeObject::getChildren);
    final JFXTreeTableColumn<OneFile, String> typeColServer = new JFXTreeTableColumn<>("Type");
    final JFXTreeTableColumn<OneFile, String> nameColServer = new JFXTreeTableColumn<>("File name");
    final JFXTreeTableColumn<OneFile, String> sizeColServer = new JFXTreeTableColumn<>("Size");

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
    private JFXTreeTableView<OneFile> tableHome;

    @FXML
    private JFXTreeTableView<OneFile> tableServer;

    @FXML
    private JFXButton upButton;

    @FXML
    private JFXTextField localAddressTextField;

    @FXML
    void initialize() throws InterruptedException {
        localAddressTextField.setText(System.getProperty("user.dir"));
        typeColClient.setPrefWidth(100);
        typeColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFile, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFile, String> param) {
                return param.getValue().getValue().fileType;
            }
        });

        nameColClient.setPrefWidth(400);
        nameColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFile, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFile, String> param) {
                return param.getValue().getValue().fileName;
            }
        });

        sizeColClient.setPrefWidth(150);
        sizeColClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<OneFile, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<OneFile, String> param) {
                return param.getValue().getValue().fileSize;
            }
        });

        tableHome.getColumns().setAll(typeColClient, nameColClient, sizeColClient);
        tableHome.prefHeight(60);
        tableHome.setRoot(rootClient);
        tableHome.setShowRoot(false);

        connectButton.setOnAction(event -> {
            mainBox.getChildren().remove(sbur);
            disconnectButton.setVisible(true);
            WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
        });

        disconnectButton.setOnAction(event -> {
            mainBox.getChildren().add(0, sbur);
            disconnectButton.setVisible(false);
        });

        upButton.setOnAction(event -> {
        });

    }
}
