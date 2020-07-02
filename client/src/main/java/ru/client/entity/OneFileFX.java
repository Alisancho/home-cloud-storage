package ru.client.entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.catalog.FileType;

public class OneFileFX extends RecursiveTreeObject<OneFileFX> {
    public final StringProperty fileType;
    public final StringProperty fileName;
    public final StringProperty fileSize;

    public OneFileFX(final @NotNull FileType fileType,
                     final @NotNull String fileName,
                     final @NotNull String fileSize) {
        this.fileType = new SimpleStringProperty(fileType.fileType());
        this.fileName = new SimpleStringProperty(fileName);
        this.fileSize = new SimpleStringProperty(fileSize);
    }
}