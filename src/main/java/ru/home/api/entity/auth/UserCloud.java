package ru.home.api.entity.auth;

import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;

public record UserCloud(@NotNull String login,
                        @NotNull String pass) implements NettyMess {}