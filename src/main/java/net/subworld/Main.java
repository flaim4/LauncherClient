package net.subworld;

import com.google.common.eventbus.EventBus;
import net.subworld.exec.BThreadFactory;
import net.subworld.minecraft.MinecraftDownloaderTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static final ExecutorService executor = new ThreadPoolExecutor(
            4,
            8,
            4L, TimeUnit.HOURS,
            new LinkedBlockingQueue<>(),
            new BThreadFactory()
    );

    public static final EventBus bus = new EventBus();

    public static void main(String[] args) {
        //MinecraftDownloaderTask task = new MinecraftDownloaderTask();
        //task.setConsumer((r, t) -> {
        //    System.out.println("test");
        //});
        //executor.execute(task);
    }
}