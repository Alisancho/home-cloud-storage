package ru.client.service;

import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.catalog.FileType;
import ru.client.entity.OneFileFX;
import ru.home.api.entity.catalog.OneServerFile;

public class WorkWithFilesServiceImpl {
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
            obList.remove(0, obList.size());
            fileSet.forEach(l ->
                    obList.add(new OneFileFX(l.fileType(), l.nameFile(), l.sizeFile())));
        });
    }
}
