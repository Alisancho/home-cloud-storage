package ru.home.api.entity.data;

import akka.util.ByteString;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import ru.home.api.entity.NettyMess;
import ru.home.api.entity.TaskType;

public record OneTask(@NotNull TaskType task,
                      @NotNull Option<String>fileName,
                      @NotNull Option<ByteString> byteString,
                      @NotNull Option<String>path) implements NettyMess {

}
