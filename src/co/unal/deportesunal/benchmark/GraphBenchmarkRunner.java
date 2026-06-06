package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.utils.CsvWriter;
import co.unal.deportesunal.benchmark.utils.MockDataGenerator;
import co.unal.deportesunal.benchmark.utils.SimpleCsvWriter;
import co.unal.deportesunal.benchmark.utils.Timer;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.structure.graphadt.AdjacencyListGraph;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.io.File;
import java.io.IOException;

public class GraphBenchmarkRunner {

    private final MockDataGenerator generator;
    private final Timer timer;

    public GraphBenchmarkRunner() {
        this(new MockDataGenerator(), new Timer());
    }

    public GraphBenchmarkRunner(MockDataGenerator generator, Timer timer) {
        if (generator == null) {
            throw new IllegalArgumentException("MockDataGenerator cannot be null.");
        }
        if (timer == null) {
            throw new IllegalArgumentException("Timer cannot be null.");
        }
        this.generator = generator;
        this.timer = timer;
    }

    public void runAll(BenchmarkConfig config) throws IOException, DataAccessException {
        runOperations(
                config,
                new BenchmarkOperation[]{BenchmarkOperation.PUT, BenchmarkOperation.GET, BenchmarkOperation.REMOVE},
                FileConstant.GRAPH_BENCHMARK_FULL,
                false
        );
    }

    public void runOperations(
            BenchmarkConfig config,
            BenchmarkOperation[] operations,
            String outputPath,
            boolean append
    ) throws IOException, DataAccessException {

        validateConfig(config);
        validateOperations(operations);
        validateOutputPath(outputPath);

        try (CsvWriter writer = new SimpleCsvWriter(outputPath, append)) {
            if (shouldWriteHeader(outputPath, append)) {
                writer.writeHeader("structure", "operation", "n", "trial", "seed", "count", "time_ns");
            }

            for (int n : config.sizes) {
                System.out.println("\n=== Graph benchmark n=" + n + " ===");

                for (int trial = 1; trial <= config.trials; trial++) {
                    long trialSeed = config.seed + (trial - 1);
                    LinkedList<Student> students = generator.generateStudents(n, trialSeed);

                    int queryCount = config.getQueryCount(n);
                    int removeCount = config.getRemoveCount(n);

                    int[] queryIds = generator.generateRandomIds(n, queryCount, trialSeed + 10_000);
                    int[] removeIds = generator.generateUniqueRandomIds(n, removeCount, trialSeed + 20_000);

                    for (BenchmarkOperation operation : operations) {
                        switch (operation) {
                            case PUT -> {
                                AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
                                long putTime = benchPut(graph, students);
                                writer.writeRow("GRAPH", "PUT", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(n), String.valueOf(putTime));
                            }
                            case GET -> {
                                AdjacencyListGraph<Integer> graph = buildGraph(students);
                                long getTime = benchGet(graph, queryIds);
                                writer.writeRow("GRAPH", "GET", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(queryCount), String.valueOf(getTime));
                            }
                            case REMOVE -> {
                                AdjacencyListGraph<Integer> graph = buildGraph(students);
                                long removeTime = benchRemove(graph, removeIds);
                                writer.writeRow("GRAPH", "REMOVE", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(removeCount), String.valueOf(removeTime));
                            }
                        }
                    }
                }
            }
        }

        System.out.println("\nGraph benchmarks finalizados.");
        System.out.println("Resultados CSV: " + outputPath);
    }

    private long benchPut(AdjacencyListGraph<Integer> graph, LinkedList<Student> students) {
        return timer.measure(new Runnable() {
            @Override
            public void run() {
                populateGraph(graph, students);
            }
        });
    }

    private long benchGet(AdjacencyListGraph<Integer> graph, int[] queryIds) {
        return timer.measure(new Runnable() {
            @Override
            public void run() {
                for (int id : queryIds) {
                    graph.bfsComponent(id);
                }
            }
        });
    }

    private long benchRemove(AdjacencyListGraph<Integer> graph, int[] removeIds) {
        return timer.measure(new Runnable() {
            @Override
            public void run() {
                for (int id : removeIds) {
                    boolean removed = graph.removeVertex(id);
                    if (!removed) {
                        throw new RuntimeException("Vertex was not removed during REMOVE benchmark: " + id);
                    }
                }
            }
        });
    }

    private AdjacencyListGraph<Integer> buildGraph(LinkedList<Student> students) {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        populateGraph(graph, students);
        return graph;
    }

    private void populateGraph(AdjacencyListGraph<Integer> graph, LinkedList<Student> students) {
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student student) {
                if (student != null) {
                    graph.addVertex(student.getId());
                }
            }
        });

        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s1) {
                students.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s2) {
                        if (s1 != null && s2 != null && s1.getId() < s2.getId() && s1.sharesPracticeWith(s2)) {
                            graph.addEdge(s1.getId(), s2.getId());
                        }
                    }
                });
            }
        });
    }

    private void validateConfig(BenchmarkConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("BenchmarkConfig cannot be null.");
        }
    }

    private void validateOperations(BenchmarkOperation[] operations) {
        if (operations == null || operations.length == 0) {
            throw new IllegalArgumentException("At least one BenchmarkOperation is required.");
        }
        for (BenchmarkOperation operation : operations) {
            if (operation == null) {
                throw new IllegalArgumentException("BenchmarkOperation cannot be null.");
            }
        }
    }

    private void validateOutputPath(String outputPath) {
        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty.");
        }
    }

    private boolean shouldWriteHeader(String path, boolean append) {
        File file = new File(path);
        if (!append) {
            return true;
        }
        return !file.exists() || file.length() == 0;
    }
}
