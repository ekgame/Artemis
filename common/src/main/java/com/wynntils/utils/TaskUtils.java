/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class TaskUtils {
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("wynntils-utilities-%d").build());

    public static Future<?> runAsync(Runnable r) {
        return executorService.submit(r);
    }
}
