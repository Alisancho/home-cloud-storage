package ru.cloud;

import lombok.extern.slf4j.Slf4j;
import ru.cloud.service.netty.NettyServer;

@Slf4j
public class AppStart {
    public static void main(String[] args) throws InterruptedException {
        new NettyServer(9999);
    }
}
