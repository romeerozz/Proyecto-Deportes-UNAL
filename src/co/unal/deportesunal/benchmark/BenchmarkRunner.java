package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.factories.IndexFactory;
import co.unal.deportesunal.benchmark.utils.CsvWriter;
import co.unal.deportesunal.benchmark.utils.MockDataGenerator;
import co.unal.deportesunal.benchmark.utils.SimpleCsvWriter;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.io.File;
import java.io.IOException;

public class BenchmarkRunner {

    private final MockDataGenerator generator;
    private final IndexBenchmark indexBenchmark;

    public BenchmarkRunner() {
        this.generator = new MockDataGenerator();
        this.indexBenchmark = new IndexBenchmark();
    }

    public BenchmarkRunner(MockDataGenerator generator, IndexBenchmark indexBenchmark) {
        if (generator == null) {
            throw new IllegalArgumentException("MockDataGenerator cannot be null.");
        }
        if (indexBenchmark == null) {
            throw new IllegalArgumentException("IndexBenchmark cannot be null.");
        }

        this.generator = generator;
        this.indexBenchmark = indexBenchmark;
    }

    public void runAll(BenchmarkConfig config, IndexFactory[] factories)
            throws IOException, DataAccessException {

        runOperations(
                config,
                factories,
                new BenchmarkOperation[]{
                        BenchmarkOperation.PUT,
                        BenchmarkOperation.GET,
                        BenchmarkOperation.REMOVE
                },
                false
        );
    }

    public void runOperations(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations
    ) throws IOException, DataAccessException {

        runOperations(config, factories, operations, false);
    }

    public void runOperations(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations,
            boolean append
    ) throws IOException, DataAccessException {

        validateConfig(config);
        validateFactories(factories);
        validateOperations(operations);

        runWarmup(config, factories, operations);

        String resultPath = FileConstant.INDEX_BENCHMARK_RESULTS;

        try (CsvWriter writer = new SimpleCsvWriter(resultPath, append)) {

            if (shouldWriteHeader(resultPath, append)) {
                writer.writeHeader(
                        "structure",
                        "operation",
                        "n",
                        "trial",
                        "seed",
                        "count",
                        "time_ns"
                );
            }

            for (int n : config.sizes) {
                System.out.println("\n=== Benchmark n=" + n + " ===");

                for (int trial = 1; trial <= config.trials; trial++) {
                    long trialSeed = config.seed + (trial - 1);

                    System.out.println("Generando datos: n=" + n + ", trial=" + trial + ", seed=" + trialSeed);

                    LinkedList<Student> students = generator.generateStudents(n, trialSeed);

                    int queryCount = config.getQueryCount(n);
                    int removeCount = config.getRemoveCount(n);

                    int[] queryIds = generator.generateRandomIds(
                            n,
                            queryCount,
                            trialSeed + 10_000
                    );

                    int[] removeIds = generator.generateUniqueRandomIds(
                            n,
                            removeCount,
                            trialSeed + 20_000
                    );

                    /*
                     * Persistimos solo el primer dataset por tamaño.
                     * Esto sirve como evidencia/reproducibilidad, pero NO hace parte
                     * del tiempo medido.
                     */
                    if (trial == 1) {
                        persistMockData(students, n, trialSeed);
                    }

                    for (IndexFactory factory : factories) {
                        runSelectedOperations(
                                writer,
                                factory,
                                students,
                                queryIds,
                                removeIds,
                                operations,
                                n,
                                trial,
                                trialSeed,
                                queryCount,
                                removeCount
                        );
                    }
                }
            }
        }

        System.out.println("\nBenchmarks finalizados.");
        System.out.println("Resultados CSV: " + resultPath);
    }

    private void runWarmup(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations
    ) {
        if (config.warmupTrials == 0) {
            return;
        }

        System.out.println("\n=== Warmup JVM ===");

        int n = config.warmupSize;

        for (int warmup = 1; warmup <= config.warmupTrials; warmup++) {
            long warmupSeed = config.seed + 999_000L + warmup;

            LinkedList<Student> students = generator.generateStudents(n, warmupSeed);

            int queryCount = config.getQueryCount(n);
            int removeCount = config.getRemoveCount(n);

            int[] queryIds = generator.generateRandomIds(
                    n,
                    queryCount,
                    warmupSeed + 10_000
            );

            int[] removeIds = generator.generateUniqueRandomIds(
                    n,
                    removeCount,
                    warmupSeed + 20_000
            );

            for (IndexFactory factory : factories) {
                runWarmupForFactory(
                        factory,
                        students,
                        queryIds,
                        removeIds,
                        operations
                );
            }
        }

        System.out.println("Warmup terminado.\n");
    }

    private void runWarmupForFactory(
            IndexFactory factory,
            LinkedList<Student> students,
            int[] queryIds,
            int[] removeIds,
            BenchmarkOperation[] operations
    ) {
        if (shouldRun(operations, BenchmarkOperation.PUT)) {
            StudentIndex putIndex = factory.create();
            indexBenchmark.benchPut(putIndex, students);
        }

        if (shouldRun(operations, BenchmarkOperation.GET)) {
            StudentIndex getIndex = factory.create();
            populateIndex(getIndex, students);
            indexBenchmark.benchGet(getIndex, queryIds);
        }

        if (shouldRun(operations, BenchmarkOperation.REMOVE)) {
            StudentIndex removeIndex = factory.create();
            populateIndex(removeIndex, students);
            indexBenchmark.benchRemove(removeIndex, removeIds);
        }
    }

    private void runSelectedOperations(
            CsvWriter writer,
            IndexFactory factory,
            LinkedList<Student> students,
            int[] queryIds,
            int[] removeIds,
            BenchmarkOperation[] operations,
            int n,
            int trial,
            long seed,
            int queryCount,
            int removeCount
    ) {
        String structureName = factory.name();

        System.out.println("Estructura: " + structureName + " | n=" + n + " | trial=" + trial);

        if (shouldRun(operations, BenchmarkOperation.PUT)) {
            StudentIndex putIndex = factory.create();

            long putTime = indexBenchmark.benchPut(putIndex, students);

            writer.writeRow(
                    structureName,
                    "PUT",
                    String.valueOf(n),
                    String.valueOf(trial),
                    String.valueOf(seed),
                    String.valueOf(n),
                    String.valueOf(putTime)
            );

            System.out.println("  PUT terminado.");
        }

        if (shouldRun(operations, BenchmarkOperation.GET)) {
            StudentIndex getIndex = factory.create();

            populateIndex(getIndex, students);

            long getTime = indexBenchmark.benchGet(getIndex, queryIds);

            writer.writeRow(
                    structureName,
                    "GET",
                    String.valueOf(n),
                    String.valueOf(trial),
                    String.valueOf(seed),
                    String.valueOf(queryCount),
                    String.valueOf(getTime)
            );

            System.out.println("  GET terminado.");
        }

        if (shouldRun(operations, BenchmarkOperation.REMOVE)) {
            StudentIndex removeIndex = factory.create();

            populateIndex(removeIndex, students);

            long removeTime = indexBenchmark.benchRemove(removeIndex, removeIds);

            writer.writeRow(
                    structureName,
                    "REMOVE",
                    String.valueOf(n),
                    String.valueOf(trial),
                    String.valueOf(seed),
                    String.valueOf(removeCount),
                    String.valueOf(removeTime)
            );

            System.out.println("  REMOVE terminado.");
        }
    }

    /*
     * Población del índice para GET y REMOVE.
     * No se mide este tiempo. Solo prepara el estado inicial.
     */
    private void populateIndex(StudentIndex index, LinkedList<Student> students) {
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student student) {
                if (student == null) return;

                try {
                    index.put(student.getId(), student);
                } catch (DuplicatedIdException e) {
                    throw new RuntimeException("Duplicated ID while populating index: " + student.getId(), e);
                }
            }
        });
    }

    private void persistMockData(LinkedList<Student> students, int n, long seed)
            throws DataAccessException {

        String path = FileConstant.mockStudentsFile(n, seed);

        TxtStudentRepository mockRepo = new TxtStudentRepository(path);
        mockRepo.save(students);

        System.out.println("Mock data persistida en: " + path);
    }

    private boolean shouldRun(BenchmarkOperation[] operations, BenchmarkOperation target) {
        for (BenchmarkOperation op : operations) {
            if (op == target) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldWriteHeader(String path, boolean append) {
        File file = new File(path);

        if (!append) {
            return true;
        }

        return !file.exists() || file.length() == 0;
    }

    private void validateConfig(BenchmarkConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("BenchmarkConfig cannot be null.");
        }
    }

    private void validateFactories(IndexFactory[] factories) {
        if (factories == null || factories.length == 0) {
            throw new IllegalArgumentException("At least one IndexFactory is required.");
        }

        for (IndexFactory factory : factories) {
            if (factory == null) {
                throw new IllegalArgumentException("IndexFactory cannot be null.");
            }
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
}