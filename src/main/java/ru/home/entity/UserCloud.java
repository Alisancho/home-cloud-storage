package ru.home.entity;

import org.jetbrains.annotations.NotNull;

public record UserCloud(@NotNull String login, @NotNull String pass) {}