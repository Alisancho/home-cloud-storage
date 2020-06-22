package ru.home.entity.catalog;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

public record ContentsDirectory(@NotNull Set<String>files, @NotNull Set<String>directorys) implements Serializable {
}
