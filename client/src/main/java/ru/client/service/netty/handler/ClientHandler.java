package ru.client.service.netty.handler;

import com.jfoenix.controls.JFXTextField;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vavr.Function1;
import io.vavr.control.Option;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.client.core.Functions;
import ru.client.entity.OneFileFX;
import ru.home.api.entity.ErrorType;
import ru.home.api.entity.NettyMess;
import ru.home.api.entity.catalog.ContentsDirectory;
import ru.home.api.entity.data.OneTask;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;
import static ru.home.api.entity.ErrorType.ERROR_MESS;
import static ru.home.api.entity.TaskType.OPTIONS;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    final private ObservableList<OneFileFX> filesListServer;
    final private ObservableList<OneFileFX> filesListClient;
    final private JFXTextField localAddressTextField;
    final private Function1<Throwable, Class<Void>> funOn;
    private ChannelHandlerContext ctxMain;


    public ClientHandler(@NotNull final ObservableList<OneFileFX> filesListServer,
                         @NotNull final ObservableList<OneFileFX> filesListClient,
                         @NotNull final JFXTextField localAddressTextField,
                         @NotNull final Function1<Throwable, Class<Void>> funOn) {
        this.funOn = funOn;
        this.filesListServer = filesListServer;
        this.filesListClient = filesListClient;
        this.localAddressTextField = localAddressTextField;
    }

    public void sendMessage(@NotNull final NettyMess msgToSend) {
        if (this.ctxMain != null) {
            this.ctxMain.writeAndFlush(msgToSend);
        } else {
            log.info("ctx not initialized yet. you were too fast. do something here");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctxL) throws Exception {
        log.info("Client channelActive");
        ctxMain = ctxL;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctxMain = ctx;
        if (msg instanceof ContentsDirectory contentsDirectory) {
            log.info("Получен список файлов" + contentsDirectory.toString());
            Functions.getFiles(filesListServer, contentsDirectory.files());
        }
        if (msg instanceof OneTask oneTask) {
            log.info("Получен OneTask");
            switch (oneTask.task()) {
                case GET -> {
                    Files.write(Paths.get(oneTask.path().get() + "/" + oneTask.fileName().get()),
                            oneTask.byteString().get().toArray(),
                            CREATE, WRITE, APPEND);
                }
                case OPTIONS -> {
                    if (localAddressTextField.getText().equals(oneTask.path().get())) {
                        Functions.getFiles(filesListClient, oneTask.path().get());
                    }
                }
                default -> {
                    log.error("Ошибка в сообщении");
                }
            }
        }
        if (msg instanceof ErrorType errorType) {
            switch (errorType) {
                case ERROR_NO -> {
                    ctx.writeAndFlush(new OneTask(OPTIONS, Option.none(), Option.none(), Option.none()));
                }
                case ERROR_MESS -> {
                    log.error(ERROR_MESS.errorType());
                }
                case AUTH_OK -> {
                    funOn.apply(null);
                    ctx.writeAndFlush(new OneTask(OPTIONS, Option.none(), Option.none(), Option.none()));
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Client exceptionCaught " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}