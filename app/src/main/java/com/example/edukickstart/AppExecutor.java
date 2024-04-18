package com.example.edukickstart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static final int THREAD_COUNT = 3;
    private static AppExecutor instance;
    private final ExecutorService executor;

    private AppExecutor() {
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    public static synchronized AppExecutor getInstance() {
        if (instance == null) {
            instance = new AppExecutor();
        }
        return instance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}

