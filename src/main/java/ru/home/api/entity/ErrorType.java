package ru.home.api.entity;

import org.jetbrains.annotations.NotNull;

public enum ErrorType implements NettyMess {
    ERROR_NO("Успех"),
    AUTH_OK("Успешная авторизация"),
    ERROR_USER("Нет такого пользователя"),
    ERROR_PASS("Неверный пароль"),
    ERROR_CONNECT("Розрыв соединения"),
    ERROR_NO_FILE("Нет такого файла"),
    ERROR_MESS("Ошибка запроса");
    private final String errorType;

    ErrorType(final @NotNull String errorType) {
        this.errorType = errorType;
    }

    public String errorType() {
        return errorType;
    }
}
