package ru.cloud;

import io.vavr.Function2;
import lombok.extern.slf4j.Slf4j;
import ru.cloud.service.DataBaseServiceImpl;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class AppStart {
    public static void main(String[] args) {
        final var dataBase = new DataBaseServiceImpl();
        new CompletableFuture().thenRunAsync(() -> {

        });

        final Function2<String, String, String> fun1 = (q, w) -> {
            return "";
        };
    }
}
