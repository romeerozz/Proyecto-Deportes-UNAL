package co.unal.deportesunal.tools;

import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.benchmark.GraphBenchmarkRunner;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.persistence.FileConstant;

public class QuickGraphBenchmark {
    public static void main(String[] args) {
        GraphBenchmarkRunner gbr = new GraphBenchmarkRunner();
        try {
            System.out.println("Running quick graph benchmark...");
            gbr.runOperations(BenchmarkConfig.quickConfig(), new BenchmarkOperation[]{
                    BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE
            }, FileConstant.graphBenchmarkResult("opt_quick"), false);
            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
