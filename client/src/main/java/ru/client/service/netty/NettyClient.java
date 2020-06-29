package ru.client.service.netty;

import com.jfoenix.controls.JFXTextField;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.vavr.Function1;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.client.entity.OneFileFX;
import ru.client.service.netty.handler.ClientHandler;
import ru.home.api.entity.NettyMess;
import ru.home.api.entity.auth.UserCloud;

@Slf4j
public class NettyClient {
    private final ClientHandler clientHandler;
    private final String host;
    private final Integer port;
    private final NioEventLoopGroup nioEventLoopGroup;


    public NettyClient(@NotNull final String host,
                       @NotNull final Integer port,
                       @NotNull final ObservableList<OneFileFX> filesListServer,
                       @NotNull final ObservableList<OneFileFX> filesListClient,
                       @NotNull final JFXTextField localAddressTextField) {
        this.nioEventLoopGroup = new NioEventLoopGroup();
        this.clientHandler = new ClientHandler(filesListServer, filesListClient, localAddressTextField);
        this.host = host;
        this.port = port;

    }

    public void run(@NotNull final Function1<Throwable, Class<Void>> f, @NotNull final UserCloud userCloud) throws Exception {

        final var bootstrap = new Bootstrap().group(this.nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(clientHandler);
                    }
                })
                .connect(host, port);

        var localVar = bootstrap.sync()
                .channel()
                .closeFuture();
        sendMess(userCloud);
        f.apply(null);
        localVar.sync();

        throw new RuntimeException("Разрыв соединения");
    }

    public void sendMess(NettyMess msg) {
        clientHandler.sendMessage(msg);
    }

    public void stop() {
        nioEventLoopGroup.shutdownGracefully();
    }
}