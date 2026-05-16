package com.example.simulation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {

    private final ScheduledExecutorService executor;
    private final UserActivityScheduler scheduler;
    private final int tickRateMs;

    public SimulationEngine(
            int threads,
            int tickRateMs,
            UserActivityScheduler scheduler
    ) {
        this.executor = Executors.newScheduledThreadPool(threads);
        this.scheduler = scheduler;
        this.tickRateMs = tickRateMs;
    }

    public void start() {

        executor.scheduleAtFixedRate(
                scheduler::tick,
                0,
                tickRateMs,
                TimeUnit.MILLISECONDS
        );
    }
}
