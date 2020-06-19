package ru.home.entity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record UserCloud(@NotNull FileType fileType, @NotNull String login, @NotNull String pass) {}