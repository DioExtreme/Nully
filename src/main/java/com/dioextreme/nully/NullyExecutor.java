package com.dioextreme.nully;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NullyExecutor
{
    private static final ExecutorService executor;

    static
    {
        int numThreadsMax = 4 * Runtime.getRuntime().availableProcessors();

        executor = new ThreadPoolExecutor(0, numThreadsMax,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

    public static void execute(Runnable r)
    {
        executor.execute(r);
    }
}
