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

@Slf4j
public class NettyClient {
    private final ClientHandler clientHandler;
    private final String host;
    private final Integer port;


    public NettyClient(@NotNull final String host,
                       @NotNull final Integer port,
                       @NotNull final ObservableList<OneFileFX> filesListServer,
                       @NotNull final ObservableList<OneFileFX> filesListClient,
                       @NotNull final JFXTextField localAddressTextField) throws Exception {
        this.clientHandler = new ClientHandler(filesListServer, filesListClient, localAddressTextField);
        this.host = host;
        this.port = port;

    }

    public void run(@NotNull final Function1<Throwable,Class<Void>> f) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
         var bootstrap =   new Bootstrap().group(group)
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
        f.apply(null);
        bootstrap.sync()
                    .channel()
                    .closeFuture()
                    .sync();

    }

    public void sendMess(NettyMess msg) {
        clientHandler.sendMessage(msg);
    }

    public void stop() {
        clientHandler.stop();
    }
}