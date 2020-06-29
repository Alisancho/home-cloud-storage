package ru.cloud.service.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import ru.cloud.service.DataBaseServiceImpl;
import ru.cloud.service.db.UserTable;
import ru.home.api.entity.auth.UserCloud;
import ru.home.api.entity.catalog.ContentsDirectory;
import ru.home.api.entity.catalog.FileType;
import ru.home.api.entity.catalog.OneServerFile;
import ru.home.api.entity.data.OneTask;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.home.api.entity.ErrorType.*;

@Slf4j
public class MessHandler extends ChannelInboundHandlerAdapter {
    //    final private String path = "/Users/aleksandrmutovkin/IdeaProjects/home-cloud-storage/server/src/main/resources/storage/user";
    private Optional<UserTable> userTable = Optional.empty();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object mess) throws Exception {

        if (userTable.isEmpty()) {
            if (mess instanceof UserCloud userCloud) {
                final var op = DataBaseServiceImpl.userIsReal(userCloud);
                if (op.isEmpty()) {
                    throw new RuntimeException("Не верный логин или пароль");
                } else {
                    log.info("Успешная авторизация " + userCloud.login());
                    this.userTable = op;
                    ctx.writeAndFlush(ERROR_NO);
                }
            } else {
                throw new RuntimeException("Авторизация не пройдена");
            }
        } else if (mess instanceof OneTask oneTask) {
            switch (oneTask.task()) {
                case GET -> {
                    try {
                        log.info(oneTask.task().taskType());
                        final var arrB = Files.readAllBytes(Paths.get(userTable.get().refRep() + "/" + oneTask.fileName().get()));
                        ctx.writeAndFlush(new OneTask(oneTask.task(), oneTask.fileName(), Option.of(arrB)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        ctx.writeAndFlush(ERROR_NO_FILE);
                    }
                }
                case PUT -> {
                    log.info(oneTask.task().taskType());
                    Files.write(Paths.get(userTable.get().refRep() + "/" + oneTask.fileName().get()), oneTask.data().get(), StandardOpenOption.CREATE);
                    ctx.writeAndFlush(ERROR_NO);
                }
                case DELETE -> {
                    log.info(oneTask.task().taskType());
                    try {
                        Files.delete(Paths.get(userTable.get().refRep() + "/" + oneTask.fileName().get()));
                        ctx.writeAndFlush(ERROR_NO);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        ctx.writeAndFlush(ERROR_NO_FILE);
                    }
                }
                case OPTIONS -> {
                    log.info(oneTask.task().taskType());
                    final var files = new File(userTable.get().refRep()).listFiles();
                    final var setFile = Arrays.stream(files)
                            .filter(k -> !k.isHidden())
                            .map(o -> new OneServerFile(FileType.getTypeFile(o), o.getName(), String.format("%.2f", (double) o.length() / 1024) + " kb"))
                            .collect(Collectors.toSet());
                    ctx.writeAndFlush(new ContentsDirectory(setFile));
                }
                default -> {
                    log.info(oneTask.task().taskType());
                    ctx.writeAndFlush(ERROR_MESS);
                }
            }
        } else {
            throw new RuntimeException("Ошибка запроса");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Ошибка в MessHandler - " + cause.getMessage());
        ctx.close();
    }
}