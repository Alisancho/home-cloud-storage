package ru.client.service.netty.handler;

import com.jfoenix.controls.JFXTextField;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.vavr.control.Option;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.client.entity.OneFileFX;
import ru.client.service.WorkWithFilesServiceImpl;
import ru.home.api.entity.ErrorType;
import ru.home.api.entity.NettyMess;
import ru.home.api.entity.TaskType;
import ru.home.api.entity.catalog.ContentsDirectory;
import ru.home.api.entity.data.OneTask;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static ru.home.api.entity.ErrorType.ERROR_MESS;
import static ru.home.api.entity.TaskType.OPTIONS;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    final private ObservableList<OneFileFX> filesListServer;
    final private ObservableList<OneFileFX> filesListClient;
    final private JFXTextField localAddressTextField;
    private ChannelHandlerContext ctxMain;

    public ClientHandler(@NotNull final ObservableList<OneFileFX> filesListServer,
                         @NotNull final ObservableList<OneFileFX> filesListClient,
                         @NotNull final JFXTextField localAddressTextField) {
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
        log.info("Получин ответ от сервера");
        ctxMain = ctx;
        log.info("Новое сообщение на клиент");
        if (msg instanceof ContentsDirectory contentsDirectory) {
            log.info("Получен список файлов" + contentsDirectory.toString());
            WorkWithFilesServiceImpl.getFiles(filesListServer, contentsDirectory.files());
        }
        if (msg instanceof OneTask oneTask) {
            log.info("Получен OneTask");
            if (oneTask.task() == TaskType.GET) {
                Files.write(Paths.get(localAddressTextField.getText() + "/" + oneTask.fileName().get()), oneTask.data().get(), StandardOpenOption.CREATE);
                WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
            } else {
                log.error("Ошибка в сообщении");
            }
        }
        if (msg instanceof ErrorType errorType) {
            switch (errorType) {
                case ERROR_NO -> {
                    ctx.writeAndFlush(new OneTask(OPTIONS, Option.none(), Option.none()));
                }
                case ERROR_MESS -> {
                    log.error(ERROR_MESS.errorType());
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Client exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}