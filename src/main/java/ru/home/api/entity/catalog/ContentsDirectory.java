package ru.home.api.entity.catalog;

import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;

import java.util.Set;

public record ContentsDirectory(@NotNull Set<OneServerFile> files) implements NettyMess {
}
