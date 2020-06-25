package ru.client.service;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

public class NIOClient {
    private final SocketChannel socketChannet;
    private final ByteBuffer byteBuffer;

    public NIOClient(@NotNull final String HOST,
                     @NotNull final Integer PORT) throws IOException {
        this.socketChannet = SocketChannel.open();
        this.socketChannet.configureBlocking(false);
        this.socketChannet.connect(new InetSocketAddress(HOST, PORT));
        this.byteBuffer = ByteBuffer.allocate(1024);
    }

    public void downloadFileFromServer(@NotNull final String fileNameServer,
                                       @NotNull final Path localDirectory) throws Exception {
        final var fos = FileChannel.open(localDirectory);
        int r = socketChannet.read(byteBuffer);
        while (r != 1) {
            this.byteBuffer.flip();
            fos.write(this.byteBuffer);
            this.byteBuffer.clear();
            r = socketChannet.read(byteBuffer);
        }
        fos.close();
    }
}
