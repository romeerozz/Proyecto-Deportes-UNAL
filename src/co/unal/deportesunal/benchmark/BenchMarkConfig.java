package co.unal.deportesunal.benchmark;

public class BenchMarkConfig {
    public final int[] sizes;          // e.g. {10_000, 100_000, 1_000_000}
    public final int trials;           // e.g. 3
    public final long seed;            // e.g. 42
    public final int getQueriesFactor; // e.g. 1 => m = n (o 2 => 2n)
    public final int removeFactor;     // e.g. 10 => k = n/10

    public BenchMarkConfig(int[] sizes, int trials, long seed, int getQueriesFactor, int removeFactor) {
        this.sizes = sizes;
        this.trials = trials;
        this.seed = seed;
        this.getQueriesFactor = getQueriesFactor;
        this.removeFactor = removeFactor;
    }
}
