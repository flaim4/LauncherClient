package net.subworld.exec;

import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.BiConsumer;

@NoArgsConstructor
public abstract class Task<T> implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(Task.class);
    public abstract T execute() throws Exception;
    private Optional<BiConsumer<T, Task<T>>> consumer = Optional.empty();
    @Override
    public void run(){
        try {
            T t = execute();
            consumer.ifPresent((c) -> {
                c.accept(t, this);
            });
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void setConsumer(BiConsumer<T, Task<T>> consumer) {
        this.consumer = Optional.of(consumer);
    }

    @NoArgsConstructor
    public abstract static class VoidClass extends Task<Void> {
        @Override
        public void run() {
            try {
                execute();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }
}
