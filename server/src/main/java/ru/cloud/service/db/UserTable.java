package ru.cloud.service.db;

import org.jetbrains.annotations.NotNull;

public record UserTable(@NotNull String login,
                        @NotNull String pass,
                        @NotNull String refRep) {
}
