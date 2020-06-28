package ru.client.service.netty.handler;

import com.jfoenix.controls.JFXTextField;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.vavr.control.Option;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.client.entity.OneFileFX;
import ru.client.service.WorkWithFilesServiceImpl;
import ru.home.api.entity.ErrorType;
import ru.home.api.entity.NettyMess;
import ru.home.api.entity.catalog.ContentsDirectory;
import ru.home.api.entity.data.OneTask;

import java.io.IOException;
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

    public  void sendMessage(@NotNull final NettyMess msgToSend) {
        if (this.ctxMain != null) {
            log.info("LLLLLL");
            this.ctxMain.writeAndFlush(msgToSend);
            //ctxMain.flush();
//            if (!cf.isSuccess()) {
//                log.info("Send failed: " + cf.cause());
//            }
        } else {
           log.info("ctx not initialized yet. you were too fast. do something here");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctxL) throws Exception {
        ctxMain = ctxL;
//        RequestData msg = new RequestData();
//        msg.setIntValue(123);
//        msg.setStringValue("all work and no play makes jack a dull boy");
//        ChannelFuture future = ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctxMain = ctx;
        try {
            log.info("Новое сообщение на клиент");
               //final var newMess = nioClient.readObject();
            if (msg instanceof ContentsDirectory contentsDirectory) {
                log.info("Получен список файлов" + contentsDirectory.toString());
                WorkWithFilesServiceImpl.getFiles(filesListServer, contentsDirectory.files());
            }
            if (msg instanceof OneTask oneTask) {
                log.info("Получен OneTask");
                switch (oneTask.task()) {
                    case GET -> {
                        Files.write(Paths.get(localAddressTextField.getText() + "/" + oneTask.fileName().get()), oneTask.data().get(), StandardOpenOption.CREATE);
                               WorkWithFilesServiceImpl.getFiles(filesListClient, localAddressTextField.getText());
                    }
                    default -> {

                    }
                }
            }
            if (msg instanceof ErrorType errorType) {
                switch (errorType) {
                    case ERROR_NO -> {
                        ctx.writeAndFlush(new OneTask(OPTIONS, Option.none(), Option.none()));
                    }
                    case ERROR_MESS -> {
                        System.out.println(ERROR_MESS.errorType());
                    }
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            log.error("MAINERROR!!!!!!!!");
            e.printStackTrace();
        } finally {
            //   nioClient.close();
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}