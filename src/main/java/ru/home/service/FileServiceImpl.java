package ru.home.service;

import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FileServiceImpl {
    public void get(final @NotNull Path path) throws IOException {
        final var asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
    }

    public static CompletionStage<IOResult> saveFile(final @NotNull Path path,
                                                     final @NotNull ByteBuffer byteBuffer,
                                                     final @NotNull Materializer materializer) {
        return Source.single(byteBuffer)
                .map(ByteString::fromByteBuffer)
                .runWith(FileIO.toPath(path), materializer);
    }

    public static CompletionStage<Void> deleteFile(final @NotNull Path path) throws Exception {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

//    public static void readFile(final @NotNull Path path,
//                                final @NotNull ByteBuffer byteBuffer,
//                                final @NotNull Materializer materializer) {
//        final var printlnSink = Sink.<ByteString>foreach(chunk ->  chunk.copyToBuffer(byteBuffer));
//        FileIO.fromPath(path)
//                .to(byteBuffer)
//                .run(materializer);
//    }
}
