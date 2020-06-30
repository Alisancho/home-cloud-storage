package ru.cloud.service;

import org.jetbrains.annotations.NotNull;
import ru.cloud.service.db.UserTable;
import ru.home.api.entity.auth.UserCloud;
import java.util.Optional;
import java.util.Set;

/**
 * Заглушка
 */
public class DataBaseServiceImpl {
    private static final Set<UserTable> usersTable = Set.of(
            new UserTable("admin", "qwerty1234", System.getProperty("user.dir") + "/server/src/main/resources/storage/admin"),
            new UserTable("user1", "user1", System.getProperty("user.dir") + "/server/src/main/resources/storage/user")
    );

    public static Optional<UserTable> userIsReal(@NotNull final UserCloud userCloud) {
       return usersTable.stream().filter(o -> o.login().equals(userCloud.login()) & o.pass().equals(userCloud.pass())).findFirst();
    }
}
