package ru.home.api.entity.catalog;

import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;

import java.io.File;

public enum FileType implements NettyMess {
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
