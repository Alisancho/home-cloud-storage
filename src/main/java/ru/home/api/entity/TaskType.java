package ru.home.api.entity;

import org.jetbrains.annotations.NotNull;

public enum TaskType implements NettyMess {
    GET("Скачать файл"),
    PUT("Загрузить файл"),
    DELETE("Удалить файл"),
    OPTIONS("Получить список файлов");
    private final String taskType;

    TaskType(final @NotNull String taskType) {
        this.taskType = taskType;
    }

    public String taskType() {
        return taskType;
    }
}
