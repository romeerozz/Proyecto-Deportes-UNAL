package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.benchmark.utils.CsvWriter;
import co.unal.deportesunal.benchmark.utils.MockDataGenerator;
import co.unal.deportesunal.benchmark.utils.SimpleCsvWriter;
import co.unal.deportesunal.benchmark.utils.Timer;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.structure.graphadt.AdjacencyListGraph;
import co.unal.deportesunal.structure.hash.HashTable;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.io.IOException;

/**
 * Ejecutor de benchmarks sobre la estructura de grafo (AdjacencyListGraph).
 * Mide el rendimiento de las operaciones PUT (construcción con aristas), GET (BFS)
 * y REMOVE (eliminación de vértices) sobre datos mock de estudiantes.
 */
public class GraphBenchmarkRunner {

    private static final String STRUCTURE_NAME = "GRAPH";

    private final MockDataGenerator generator;
    private final Timer timer;

    /**
     * Crea un GraphBenchmarkRunner con componentes por defecto.
     */
    public GraphBenchmarkRunner() {
        this(new MockDataGenerator(), new Timer());
    }

    /**
     * Crea un GraphBenchmarkRunner con generador y timer personalizados.
     *
     * @param generator generador de datos mock
     * @param timer     cronómetro para medir tiempos
     * @throws IllegalArgumentException si algún parámetro es nulo
     */
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

    /**
     * Ejecuta benchmarks de grafo para todas las configuraciones y escribe los resultados en un CSV.
     *
     * @param config      configuración del benchmark
     * @param operations  operaciones a ejecutar
     * @param outputPath  ruta del archivo CSV de salida
     * @param append      si es true, añade al archivo existente
     * @throws IOException si hay error de escritura
     */
    public void runOperations(BenchmarkConfig config, BenchmarkOperation[] operations, String outputPath, boolean append) throws IOException {
        try (CsvWriter writer = new SimpleCsvWriter(outputPath, append)) {
            writer.writeHeader("structure", "operation", "n", "trial", "seed", "count", "time_ns");
            for (int n : config.sizes) {
                for (int trial = 1; trial <= config.trials; trial++) {
                    long trialSeed = config.seed + (trial - 1);
                    LinkedList<Student> students = generator.generateStudents(n, trialSeed);
                    int queryCount = config.getQueryCount(n);
                    int removeCount = config.getRemoveCount(n);
                    int[] queryIds = generator.generateRandomIds(n, queryCount, trialSeed + 10_000);
                    int[] removeIds = generator.generateUniqueRandomIds(n, removeCount, trialSeed + 20_000);
                    runBenchmarks(writer, config, operations, students, queryIds, removeIds, n, trial, trialSeed, queryCount, removeCount);
                }
            }
        }
    }

    /**
     * Ejecuta los benchmarks de grafo para las operaciones indicadas sobre un conjunto de datos dado.
     *
     * @param writer      escritor CSV (puede ser null durante calentamiento)
     * @param config      configuración del benchmark
     * @param operations  operaciones a ejecutar
     * @param students    lista de estudiantes
     * @param queryIds    IDs para consultas GET (BFS)
     * @param removeIds   IDs para eliminación de vértices
     * @param n           tamaño de los datos
     * @param trial       número de ensayo
     * @param trialSeed   semilla del ensayo
     * @param queryCount  cantidad de consultas
     * @param removeCount cantidad de eliminaciones
     */
    public void runBenchmarks(
            CsvWriter writer,
            BenchmarkConfig config,
            BenchmarkOperation[] operations,
            LinkedList<Student> students,
            int[] queryIds,
            int[] removeIds,
            int n,
            int trial,
            long trialSeed,
            int queryCount,
            int removeCount
    ) {
        for (BenchmarkOperation operation : operations) {
            switch (operation) {
                case PUT -> {
                    AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
                    long putTime = benchPut(graph, students);
                    if (writer != null) {
                        writer.writeRow(STRUCTURE_NAME, "PUT", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(n), String.valueOf(putTime));
                    }
                }
                case GET -> {
                    AdjacencyListGraph<Integer> graph = buildGraph(students);
                    long getTime = benchGet(graph, queryIds);
                    if (writer != null) {
                        writer.writeRow(STRUCTURE_NAME, "GET", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(queryCount), String.valueOf(getTime));
                    }
                }
                case REMOVE -> {
                    AdjacencyListGraph<Integer> graph = buildGraph(students);
                    long removeTime = benchRemove(graph, removeIds);
                    if (writer != null) {
                        writer.writeRow(STRUCTURE_NAME, "REMOVE", String.valueOf(n), String.valueOf(trial), String.valueOf(trialSeed), String.valueOf(removeCount), String.valueOf(removeTime));
                    }
                }
            }
        }
    }

    /**
     * Mide el tiempo de construcción del grafo (inserción de vértices y aristas).
     *
     * @param graph    grafo vacío
     * @param students estudiantes para poblar el grafo
     * @return tiempo en nanosegundos
     */
    private long benchPut(AdjacencyListGraph<Integer> graph, LinkedList<Student> students) {
        return timer.measure(new Runnable() {
            @Override
            public void run() {
                populateGraph(graph, students);
            }
        });
    }

    /**
     * Mide el tiempo de ejecución de BFS para cada ID de consulta.
     *
     * @param graph    grafo poblado
     * @param queryIds IDs para ejecutar BFS
     * @return tiempo en nanosegundos
     */
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

    /**
     * Mide el tiempo de eliminación de vértices del grafo.
     *
     * @param graph     grafo poblado
     * @param removeIds IDs de vértices a eliminar
     * @return tiempo en nanosegundos
     */
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

    /**
     * Construye un grafo poblado con los estudiantes (vértices y aristas por deporte).
     *
     * @param students lista de estudiantes
     * @return grafo poblado
     */
    private AdjacencyListGraph<Integer> buildGraph(LinkedList<Student> students) {
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();
        populateGraph(graph, students);
        return graph;
    }

    /**
     * Puebla un grafo con vértices (IDs de estudiantes) y aristas basadas en deportes compartidos.
     *
     * @param graph    grafo a poblar
     * @param students lista de estudiantes
     */
    private void populateGraph(AdjacencyListGraph<Integer> graph, LinkedList<Student> students) {
        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student student) {
                if (student != null) {
                    graph.addVertex(student.getId());
                }
            }
        });

        HashTable<SportEnum, LinkedList<Integer>> buckets = new HashTable<>();
        for (SportEnum sp : SportEnum.values()) {
            buckets.put(sp, new LinkedList<>());
        }

        students.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (s == null) return;
                s.getPractice().traverse(new ListVisitor<SportEnum>() {
                    @Override
                    public void visit(SportEnum sport) {
                        if (sport == null) return;
                        LinkedList<Integer> list = buckets.get(sport);
                        if (list != null) list.pushBack(s.getId());
                    }
                });
            }
        });

        for (SportEnum sp : SportEnum.values()) {
            LinkedList<Integer> ids = buckets.get(sp);
            if (ids == null || ids.size() < 2) continue;

            final LinkedList<Integer> idList = ids;
            final Integer[] prev = {null};
            idList.traverse(new ListVisitor<Integer>() {
                @Override
                public void visit(Integer id) {
                    if (id == null) return;
                    if (Thread.currentThread().isInterrupted()) return;
                    if (prev[0] != null) {
                        graph.addEdge(prev[0], id);
                    }
                    prev[0] = id;
                }
            });
        }
    }
}