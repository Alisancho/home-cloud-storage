package ru.client.service.netty;

import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;

import java.io.IOException;
import java.net.Socket;

public class NettyClientServiceImpl {
    
    private final Socket socket;
    private final ObjectDecoderInputStream objectDecoderInputStream;
    private final ObjectEncoderOutputStream objectEncoderOutputStream;

    public NettyClientServiceImpl(@NotNull final String HOST,
                                  @NotNull final Integer PORT) throws Exception {
        this.socket = new Socket(HOST, PORT);
        this.objectEncoderOutputStream = new ObjectEncoderOutputStream(socket.getOutputStream());
        this.objectDecoderInputStream = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public void close() {
        try {
            objectEncoderOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectDecoderInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NettyMess readObject() throws ClassNotFoundException, IOException {
        Object obj = objectDecoderInputStream.readObject();
        return (NettyMess) obj;
    }

    public boolean sendMsg(NettyMess msg) {
        try {
            objectEncoderOutputStream.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}