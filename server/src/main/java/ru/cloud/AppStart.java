package ru.cloud;

import akka.actor.ActorSystem;
import lombok.extern.slf4j.Slf4j;
import ru.cloud.service.netty.NettyServer;

@Slf4j
public class AppStart {
    public static final ActorSystem actorSystem = ActorSystem.create("myapp-actorsys");

    public static void main(String[] args) throws InterruptedException {

        new NettyServer(9999);
    }
}
