package ru.home.entity;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public enum FileType {
    FILE("FILE"),
    DIRECTORY("DIRECTORY");

    private final String fileType;

    FileType(final @NotNull String fileType) {
        this.fileType = fileType;
    }

    public String fileType() {
        return fileType;
    }

    public static FileType getTypeFile(final @NotNull File file) {
        if (file.isFile()) {
            return FileType.FILE;
        } else {
            return FileType.DIRECTORY;
        }
    }
}
