package co.unal.deportesunal.tools;

import co.unal.deportesunal.benchmark.GraphBenchmarkRunner;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.persistence.FileConstant;

public class QuickGraphBenchmarkSmall {
    public static void main(String[] args) {
        GraphBenchmarkRunner gbr = new GraphBenchmarkRunner();
        try {
            BenchmarkConfig cfg = new BenchmarkConfig(new int[]{100, 500}, 1, 42L, 1, 10, 0, 100);
            System.out.println("Running small optimized graph benchmark...");
            gbr.runOperations(cfg, new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE}, FileConstant.graphBenchmarkResult("opt_small"), false);
            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
