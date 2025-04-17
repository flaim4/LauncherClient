package net.subworld.exec;

import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.BiConsumer;

@NoArgsConstructor
public abstract class Task<T> implements Runnable {
    public abstract T execute();
    private Optional<BiConsumer<T, Task<T>>> consumer = Optional.empty();
    @Override
    public void run() {
        T t = execute();
        consumer.ifPresent((c) -> {
            c.accept(t, this);
        });
    }

    public void setConsumer(BiConsumer<T, Task<T>> consumer) {
        this.consumer = Optional.of(consumer);
    }

    @NoArgsConstructor
    public abstract static class VoidClass extends Task<Void> {
        @Override
        public void run() {
            execute();
        }
    }
}
