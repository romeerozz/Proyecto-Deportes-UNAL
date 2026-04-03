package co.unal.deportesunal.benchmark.utils;

public class Timer {
    public long measure(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        return System.nanoTime() - t0;
    }
}

