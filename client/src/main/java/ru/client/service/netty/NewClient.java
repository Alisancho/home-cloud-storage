package ru.client.service.netty;

import com.jfoenix.controls.JFXTextField;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import ru.client.entity.OneFileFX;
import ru.client.service.netty.handler.ClientHandler;
import ru.home.api.entity.NettyMess;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NewClient implements Runnable {

    private final String host;
    private final int port;
    private final ClientHandler clientHandler;
    private boolean isRunning = false;
    private ExecutorService executor = null;

    public NewClient(@NotNull final String HOST,
                     @NotNull final Integer PORT,
                     @NotNull final ObservableList<OneFileFX> filesListServer,
                     @NotNull final ObservableList<OneFileFX> filesListClient,
                     @NotNull final JFXTextField localAddressTextField) {
        this.clientHandler = new ClientHandler(filesListServer, filesListClient, localAddressTextField);
        this.host = HOST;
        this.port = PORT;
    }

    public void startClient() {
        if (!isRunning) {
            executor = Executors.newFixedThreadPool(50);
            executor.execute(this);
            isRunning = true;
        }
    }

    public  boolean stopClient() {
        boolean bReturn = true;
        if (isRunning) {
            if (executor != null) {
                executor.shutdown();
                try {
                    executor.shutdownNow();
                    if (executor.awaitTermination(calcTime(10, 0.66667), TimeUnit.SECONDS)) {
                        if (!executor.awaitTermination(calcTime(10, 0.33334), TimeUnit.SECONDS)) {
                            bReturn = false;
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    executor = null;
                }
            }
            isRunning = false;
        }
        return bReturn;
    }

    private long calcTime(int nTime, double dValue) {
        return (long) ((double) nTime * dValue);
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(clientHandler);
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();

        } catch (InterruptedException ex) {
            // do nothing
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void sendMess(NettyMess msg) {
        clientHandler.sendMessage(msg);
    }
}