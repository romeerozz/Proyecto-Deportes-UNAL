package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.factories.IndexFactory;
import co.unal.deportesunal.benchmark.utils.CsvWriter;
import co.unal.deportesunal.benchmark.utils.MockDataGenerator;
import co.unal.deportesunal.benchmark.utils.SimpleCsvWriter;
import co.unal.deportesunal.benchmark.utils.Timer;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.structure.disjointset.UnionFind;
import co.unal.deportesunal.structure.index.StudentIndex;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.benchmark.GraphBenchmarkRunner;

import java.io.File;
import java.io.IOException;

/**
 * Ejecutor principal de benchmarks sobre estructuras de índice, grafos y Union-Find.
 * Orquesta la generación de datos mock, el calentamiento de la JVM y la ejecución
 * de operaciones PUT, GET y REMOVE, escribiendo los resultados en archivos CSV.
 */
public class BenchmarkRunner {

    private final MockDataGenerator generator;
    private final IndexBenchmark indexBenchmark;
    private final GraphBenchmarkRunner graphBenchmark;

    /**
     * Crea un BenchmarkRunner con componentes por defecto.
     */
    public BenchmarkRunner() {
        this.generator = new MockDataGenerator();
        this.indexBenchmark = new IndexBenchmark();
        this.graphBenchmark = new GraphBenchmarkRunner();
    }

    /**
     * Crea un BenchmarkRunner con generador de datos y benchmark de índices personalizados.
     *
     * @param generator     generador de datos mock
     * @param indexBenchman ejecutor de benchmarks sobre índices
     * @throws IllegalArgumentException si alguno de los parámetros es nulo
     */
    public BenchmarkRunner(MockDataGenerator generator, IndexBenchmark indexBenchmark) {
        if (generator == null) {
            throw new IllegalArgumentException("MockDataGenerator cannot be null.");
        }
        if (indexBenchmark == null) {
            throw new IllegalArgumentException("IndexBenchmark cannot be null.");
        }

        this.generator = generator;
        this.indexBenchmark = indexBenchmark;
        this.graphBenchmark = new GraphBenchmarkRunner();
    }

    /**
     * Ejecuta todas las operaciones (PUT, GET, REMOVE) sobre todas las fábricas de índices
     * y escribe los resultados en el archivo CSV por defecto.
     *
     * @param config   configuración del benchmark
     * @param factories fábricas de índices a probar
     * @throws IOException         si hay error de escritura
     * @throws DataAccessException si hay error de acceso a datos
     */
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
                FileConstant.INDEX_BENCHMARK_FULL,
                false
        );
    }

    /**
     * Ejecuta un subconjunto de operaciones sobre las fábricas de índices
     * y escribe los resultados en un archivo CSV con nombre personalizado.
     *
     * @param config     configuración del benchmark
     * @param factories  fábricas de índices a probar
     * @param operations operaciones a ejecutar
     * @throws IOException         si hay error de escritura
     * @throws DataAccessException si hay error de acceso a datos
     */
    public void runOperations(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations
    ) throws IOException, DataAccessException {

        runOperations(
                config,
                factories,
                operations,
                FileConstant.indexBenchmarkResult("custom"),
                false
        );
    }

    /**
     * Ejecuta un subconjunto de operaciones sobre las fábricas de índices y escribe
     * los resultados en la ruta especificada, con opción de adjuntar al archivo existente.
     *
     * @param config      configuración del benchmark
     * @param factories   fábricas de índices a probar
     * @param operations  operaciones a ejecutar
     * @param outputPath  ruta del archivo CSV de salida
     * @param append      si es true, añade los resultados al archivo existente
     * @throws IOException         si hay error de escritura
     * @throws DataAccessException si hay error de acceso a datos
     */
    public void runOperations(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations,
            String outputPath,
            boolean append
    ) throws IOException, DataAccessException {

        validateConfig(config);
        validateFactories(factories);
        validateOperations(operations);
        validateOutputPath(outputPath);

        runWarmup(config, factories, operations);

        try (CsvWriter writer = new SimpleCsvWriter(outputPath, append)) {

            if (shouldWriteHeader(outputPath, append)) {
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
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Index benchmark cancelled.");
                        return;
                    }
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
                        if (Thread.currentThread().isInterrupted()) {
                            System.out.println("Index benchmark cancelled.");
                            return;
                        }
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

                    graphBenchmark.runBenchmarks(
                            writer,
                            config,
                            operations,
                            students,
                            queryIds,
                            removeIds,
                            n,
                            trial,
                            trialSeed,
                            queryCount,
                            removeCount
                    );

                    runUnionFindBenchmarks(
                            writer,
                            operations,
                            n,
                            trial,
                            trialSeed
                    );
                }
            }
        }

        System.out.println("\nBenchmarks finalizados.");
        System.out.println("Resultados CSV: " + outputPath);
    }

    /**
     * Valida que la ruta de salida no sea nula ni esté vacía.
     *
     * @param outputPath ruta a validar
     * @throws IllegalArgumentException si la ruta es inválida
     */
    private void validateOutputPath(String outputPath) {
        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty.");
        }
    }

    /**
     * Ejecuta ensayos de calentamiento para estabilizar la JVM antes de las mediciones reales.
     *
     * @param config     configuración con parámetros de calentamiento
     * @param factories  fábricas de índices a calentar
     * @param operations operaciones a ejecutar durante el calentamiento
     */
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

            graphBenchmark.runBenchmarks(
                    null,
                    config,
                    operations,
                    students,
                    queryIds,
                    removeIds,
                    n,
                    -1,
                    warmupSeed,
                    queryCount,
                    removeCount
            );

            runUnionFindBenchmarks(
                    null,
                    operations,
                    n,
                    -1,
                    warmupSeed
            );
        }

        System.out.println("Warmup terminado.\n");
    }

    /**
     * Ejecuta el calentamiento para una fábrica de índices específica.
     *
     * @param factory   fábrica que crea la estructura a calentar
     * @param students  lista de estudiantes para poblar
     * @param queryIds  IDs de consulta
     * @param removeIds IDs a eliminar
     * @param operations operaciones a ejecutar
     */
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

    /**
     * Ejecuta las operaciones seleccionadas para una fábrica de índices y escribe los resultados.
     *
     * @param writer       escritor CSV
     * @param factory      fábrica que crea la estructura
     * @param students     lista de estudiantes
     * @param queryIds     IDs para consultas GET
     * @param removeIds    IDs para eliminaciones REMOVE
     * @param operations   operaciones a ejecutar
     * @param n            tamaño de los datos
     * @param trial        número de ensayo actual
     * @param seed         semilla del ensayo
     * @param queryCount   cantidad de consultas GET
     * @param removeCount  cantidad de eliminaciones REMOVE
     */
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
    /**
     * Puebla un índice con estudiantes sin medir el tiempo. Prepara el estado inicial
     * para las operaciones GET y REMOVE.
     *
     * @param index    índice a poblar
     * @param students lista de estudiantes
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

    /**
     * Persiste los datos mock generados en un archivo de texto para reproducibilidad.
     * Solo se ejecuta en el primer ensayo de cada tamaño.
     *
     * @param students lista de estudiantes a persistir
     * @param n        tamaño de los datos
     * @param seed     semilla utilizada
     * @throws DataAccessException si hay error de escritura
     */
    private void persistMockData(LinkedList<Student> students, int n, long seed)
            throws DataAccessException {

        String path = FileConstant.mockStudentsFile(n, seed);

        TxtStudentRepository mockRepo = new TxtStudentRepository(path);
        mockRepo.save(students);

        System.out.println("Mock data persistida en: " + path);
    }

    /**
     * Determina si una operación específica debe ejecutarse según el arreglo de operaciones configurado.
     *
     * @param operations arreglo de operaciones seleccionadas
     * @param target     operación a verificar
     * @return true si la operación está incluida
     */
    private boolean shouldRun(BenchmarkOperation[] operations, BenchmarkOperation target) {
        for (BenchmarkOperation op : operations) {
            if (op == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determina si se debe escribir el encabezado del CSV según la ruta y el modo append.
     *
     * @param path   ruta del archivo
     * @param append modo de adjuntar
     * @return true si se debe escribir el encabezado
     */
    private boolean shouldWriteHeader(String path, boolean append) {
        File file = new File(path);

        if (!append) {
            return true;
        }

        return !file.exists() || file.length() == 0;
    }

    /**
     * Valida que la configuración del benchmark no sea nula.
     *
     * @param config configuración a validar
     * @throws IllegalArgumentException si es nula
     */
    private void validateConfig(BenchmarkConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("BenchmarkConfig cannot be null.");
        }
    }

    /**
     * Valida que el arreglo de fábricas no sea nulo, vacío ni contenga elementos nulos.
     *
     * @param factories fábricas a validar
     * @throws IllegalArgumentException si la validación falla
     */
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

    /**
     * Valida que el arreglo de operaciones no sea nulo, vacío ni contenga elementos nulos.
     *
     * @param operations operaciones a validar
     * @throws IllegalArgumentException si la validación falla
     */
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

    /**
     * Ejecuta benchmarks sobre la estructura Union-Find para las operaciones configuradas.
     *
     * @param writer     escritor CSV (puede ser null durante calentamiento)
     * @param operations operaciones a ejecutar
     * @param n          tamaño de los datos
     * @param trial      número de ensayo (-1 durante calentamiento)
     * @param seed       semilla del ensayo
     */
    private void runUnionFindBenchmarks(
            CsvWriter writer,
            BenchmarkOperation[] operations,
            int n,
            int trial,
            long seed
    ) {
        if (!shouldRun(operations, BenchmarkOperation.PUT)
                && !shouldRun(operations, BenchmarkOperation.GET)
                && !shouldRun(operations, BenchmarkOperation.REMOVE)) {
            return;
        }

        System.out.println("Estructura: UF | n=" + n + " | trial=" + trial);
        Timer timer = new Timer();

        if (shouldRun(operations, BenchmarkOperation.PUT)) {
            UnionFind uf = new UnionFind();
            long time = timer.measure(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= n; i++) {
                        uf.makeSet(i);
                    }
                }
            });
            if (writer != null) {
                writer.writeRow("UF", "PUT", String.valueOf(n), String.valueOf(trial), String.valueOf(seed), String.valueOf(n), String.valueOf(time));
            }
            System.out.println("  UF PUT terminado.");
        }

        if (shouldRun(operations, BenchmarkOperation.GET)) {
            // Pre-poblar sin medir
            UnionFind uf = new UnionFind();
            for (int i = 1; i <= n; i++) {
                uf.makeSet(i);
            }

            long time = timer.measure(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= n; i++) {
                        uf.find(i);
                    }
                }
            });
            if (writer != null) {
                writer.writeRow("UF", "GET", String.valueOf(n), String.valueOf(trial), String.valueOf(seed), String.valueOf(n), String.valueOf(time));
            }
            System.out.println("  UF GET terminado.");
        }

        if (shouldRun(operations, BenchmarkOperation.REMOVE)) {
            // Pre-poblar sin medir
            UnionFind uf = new UnionFind();
            for (int i = 1; i <= n; i++) {
                uf.makeSet(i);
            }

            int unionCount = Math.max(1, n / 10);
            long time = timer.measure(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < unionCount; i++) {
                        uf.union(1 + (i * 10) % n + 1, 1 + (i * 10 + 5) % n);
                    }
                }
            });
            if (writer != null) {
                writer.writeRow("UF", "REMOVE", String.valueOf(n), String.valueOf(trial), String.valueOf(seed), String.valueOf(unionCount), String.valueOf(time));
            }
            System.out.println("  UF REMOVE terminado.");
        }
    }
}