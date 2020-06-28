package ru.home.api.entity.catalog;

import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;

public record OneServerFile(@NotNull FileType fileType,
                            @NotNull String nameFile,
                            @NotNull String sizeFile) implements NettyMess {
}
