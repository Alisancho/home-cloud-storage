package ru.cloud.service;

import ru.home.UserCloud;

import java.util.Map;

/**
 * Заглушка
 */
public class DataBaseServiceImpl {
    private final Map<String, String> mapUsers = Map.of(
            "admin", "qwerty1234",
            "user1", "user1"
    );

    public Boolean userIsReal(final UserCloud userCloud) {
        if (mapUsers.containsKey(userCloud.login())) {
            final var localPass = mapUsers.get(userCloud.login());
            return localPass.equals(userCloud.pass());
        }
        return false;
    }
}
