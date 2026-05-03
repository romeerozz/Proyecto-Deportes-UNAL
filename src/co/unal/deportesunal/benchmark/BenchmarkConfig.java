package co.unal.deportesunal.benchmark;

public class BenchmarkConfig {
    public final int[] sizes;
    public final int trials;
    public final long seed;
    public final int getQueriesFactor;
    public final int removeFactor;

    public BenchmarkConfig(int[] sizes, int trials, long seed, int getQueriesFactor, int removeFactor) {
        validateSizes(sizes);

        if (trials <= 0) {
            throw new IllegalArgumentException("Trials must be greater than 0.");
        }

        if (getQueriesFactor <= 0) {
            throw new IllegalArgumentException("Get queries factor must be greater than 0.");
        }

        if (removeFactor <= 0) {
            throw new IllegalArgumentException("Remove factor must be greater than 0.");
        }

        this.sizes = copyArray(sizes);
        this.trials = trials;
        this.seed = seed;
        this.getQueriesFactor = getQueriesFactor;
        this.removeFactor = removeFactor;
    }

    public int getQueryCount(int n) {
        return n * getQueriesFactor;
    }

    public int getRemoveCount(int n) {
        int count = n / removeFactor;
        return Math.max(1, count);
    }

    private void validateSizes(int[] sizes) {
        if (sizes == null || sizes.length == 0) {
            throw new IllegalArgumentException("Sizes cannot be null or empty.");
        }

        for (int size : sizes) {
            if (size <= 0) {
                throw new IllegalArgumentException("All sizes must be greater than 0.");
            }
        }
    }

    private int[] copyArray(int[] source) {
        int[] copy = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    public static BenchmarkConfig defaultConfig() {
        return new BenchmarkConfig(
                new int[]{10_000, 100_000, 1_000_000},
                3,
                42L,
                1,
                10
        );
    }

    public static BenchmarkConfig quickConfig() {
        return new BenchmarkConfig(
                new int[]{1_000, 10_000},
                2,
                42L,
                1,
                10
        );
    }
}