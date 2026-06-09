package co.unal.deportesunal.benchmark.utils;

public class Timer {

    /**
     * Measures the execution time of a runnable block using System.nanoTime().
     *
     * @param task code block to measure
     * @return elapsed time in nanoseconds
     */
    public long measure(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }

        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }
}