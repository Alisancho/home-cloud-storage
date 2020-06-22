package ru.home.entity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public record UserCloud(@NotNull String login, @NotNull String pass) implements Serializable {}