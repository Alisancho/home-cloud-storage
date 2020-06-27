package ru.client.service;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import ru.home.entity.catalog.FileType;
import ru.client.entity.OneFile;

public class WorkWithFilesServiceImpl {
    public static void getFiles(final @NotNull ObservableList<OneFile> obList,
                                final @NotNull String filePath) {
        CompletableFuture.runAsync(() -> {
            try {
                obList.remove(0, obList.size());
                final var files = new File(filePath).listFiles();
                Arrays.stream(files).forEach(file ->
                        obList.add(new OneFile(FileType.getTypeFile(file), file.getName(), String.format("%.2f",(double) file.length() / 1024) + " kb"))
                );

//                final var file = new File("/Users/aleksandrmutovkin/IdeaProjects/home-cloud-storage/client/src/main/resources/fontstyle.css");
//                final var fis = new FileInputStream(file);
//                final var body = IOUtils.toString(fis, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
