package co.unal.deportesunal.ui;

import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.benchmark.factories.AvlIndexFactory;
import co.unal.deportesunal.benchmark.factories.BstIndexFactory;
import co.unal.deportesunal.benchmark.factories.IndexFactory;
import co.unal.deportesunal.benchmark.factories.ListIndexFactory;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.util.Scanner;

public class ConsoleUi {

    private final AppController controller;
    private final BenchmarkRunner benchmarkRunner;
    private final Scanner sc = new Scanner(System.in);

    public ConsoleUi(AppController controller, BenchmarkRunner benchmarkRunner) {
        this.controller = controller;
        this.benchmarkRunner = benchmarkRunner;
    }

    private void autoLoadOnStart() {
        try {
            controller.load();
            System.out.println("Datos cargados desde archivo.");
            System.out.println("Total estudiantes: " + controller.totalStudents());
        } catch (Exception e) {
            System.out.println("No se pudo cargar (archivo vacío o error): " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("=== Deportes UNAL - Prototipo (Consola) ===");

        autoLoadOnStart();

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int op = readInt("Opción: ");

            switch (op) {
                case 1 -> interactiveMenuLoop();
                case 2 -> runBenchmarksFlow(); // placeholder
                case 3 -> loadFlow();
                case 4 -> saveFlow();
                case 0 -> exit = true;
                default -> System.out.println("Opción inválida.");
            }
        }

        System.out.println("Saliendo...");
        sc.close();
    }

    private void printMainMenu() {
        System.out.println("\n--- Menú principal ---");
        System.out.println("1) Modo interactivo (CRUD)");
        System.out.println("2) Ejecutar benchmarks");
        System.out.println("3) Recargar desde el archivo (sobreescribe el estado actual)");
        System.out.println("4) Guardar a archivo");
        System.out.println("0) Salir");
    }

    private void interactiveMenuLoop() {
        boolean back = false;
        while (!back) {
            printCrudMenu();
            int op = readInt("Opción: ");

            switch (op) {
                case 1 -> registerStudentFlow();
                case 2 -> findStudentFlow();
                case 3 -> updateSportsFlow();
                case 4 -> deleteStudentFlow();
                case 5 -> listStudentsFlow();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void printCrudMenu() {
        System.out.println("\n--- CRUD ---");
        System.out.println("1) Registrar estudiante");
        System.out.println("2) Consultar por ID");
        System.out.println("3) Actualizar deportes");
        System.out.println("4) Eliminar estudiante");
        System.out.println("5) Listar estudiantes");
        System.out.println("0) Volver");
    }

    private void registerStudentFlow() {
        try {
            int id = readInt("ID: ");
            String name = readLine("Nombre: ");
            controller.registerStudent(id, name);
            System.out.println("Estudiante registrado.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void findStudentFlow() {
        try {
            int id = readInt("ID: ");
            Student s = controller.findStudent(id);
            printStudent(s);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void updateSportsFlow() {
        int id = readInt("ID del estudiante: ");

        try {
            controller.findStudent(id); // valida existencia
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n--- Actualizar deportes (ID=" + id + ") ---");
            System.out.println("1) Agregar a práctica");
            System.out.println("2) Remover de práctica");
            System.out.println("3) Agregar a interés");
            System.out.println("4) Remover de interés");
            System.out.println("5) Ver catálogo");
            System.out.println("0) Volver");

            int op = readInt("Opción: ");
            try {
                switch (op) {
                    case 1 -> {
                        SportEnum sp = readSportEnum();
                        System.out.println(controller.addPracticeSport(id, sp) ? "Agregado." : "No agregado (duplicado).");
                    }
                    case 2 -> {
                        SportEnum sp = readSportEnum();
                        System.out.println(controller.removePracticeSport(id, sp) ? "Removido." : "No estaba.");
                    }
                    case 3 -> {
                        SportEnum sp = readSportEnum();
                        System.out.println(controller.addInterestSport(id, sp) ? "Agregado." : "No agregado (duplicado).");
                    }
                    case 4 -> {
                        SportEnum sp = readSportEnum();
                        System.out.println(controller.removeInterestSport(id, sp) ? "Removido." : "No estaba.");
                    }
                    case 5 -> printSportsCatalog();
                    case 0 -> {
                        System.out.print("¿Guardar antes de salir? (s/n): ");
                        String ans = sc.nextLine().trim().toLowerCase();
                        if (ans.equals("s")) saveFlow();
                        back = true;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void deleteStudentFlow() {
        try {
            int id = readInt("ID a eliminar: ");
            boolean removed = controller.deleteStudent(id);
            System.out.println(removed ? "Eliminado." : "No existía ese ID.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void listStudentsFlow() {
        try {
            LinkedList<Student> students = controller.listStudents();
            System.out.println("Total: " + controller.totalStudents());

            students.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s) {
                    if (s == null) return;
                    printStudent(s);
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void loadFlow() {
        System.out.println("⚠️  Esto recargará desde archivo y reemplazará los datos actuales en memoria.");
        String ans = readLine("¿Continuar? (s/n): ").trim().toLowerCase();
        if (!ans.equals("s")) {
            System.out.println("Recarga cancelada.");
            return;
        }

        try {
            controller.load();
            System.out.println("Datos cargados. Total estudiantes: " + controller.totalStudents());
        } catch (Exception e) {
            System.out.println("ERROR al cargar: " + e.getMessage());
        }
    }

    private void saveFlow() {
        try {
            controller.save();
            System.out.println("Datos guardados.");
        } catch (Exception e) {
            System.out.println("ERROR al guardar: " + e.getMessage());
        }
    }

    private void runBenchmarksFlow() {
        boolean back = false;

        while (!back) {
            printBenchmarkMenu();
            int op = readInt("Opción: ");

            try {
                switch (op) {
                    case 1 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            allOperations(),
                            false
                    );

                    case 2 -> runBenchmark(
                            BenchmarkConfig.quickConfig(),
                            allFactories(),
                            allOperations(),
                            false
                    );

                    case 3 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.PUT},
                            false
                    );

                    case 4 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.GET},
                            false
                    );

                    case 5 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.REMOVE},
                            false
                    );

                    case 6 -> runBenchmarkByStructure();

                    case 0 -> back = true;

                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR ejecutando benchmark: " + e.getMessage());
            }
        }
    }

    private void printBenchmarkMenu() {
        System.out.println("\n--- Benchmarks ---");
        System.out.println("1) Ejecutar TODOS los benchmarks (configuración completa)");
        System.out.println("2) Ejecutar TODOS los benchmarks (configuración rápida)");
        System.out.println("3) Ejecutar solo PUT");
        System.out.println("4) Ejecutar solo GET");
        System.out.println("5) Ejecutar solo REMOVE");
        System.out.println("6) Ejecutar por estructura específica");
        System.out.println("0) Volver");
    }

    private void runBenchmark(
            BenchmarkConfig config,
            IndexFactory[] factories,
            BenchmarkOperation[] operations,
            boolean append
    ) throws Exception {
        System.out.println("\nEjecutando benchmark...");
        System.out.println("Resultados: " + FileConstant.INDEX_BENCHMARK_RESULTS);

        benchmarkRunner.runOperations(config, factories, operations, append);

        System.out.println("Benchmark terminado.");
        System.out.println("CSV generado en: " + FileConstant.INDEX_BENCHMARK_RESULTS);
    }

    private void runBenchmarkByStructure() throws Exception {
        System.out.println("\n--- Seleccionar estructura ---");
        System.out.println("1) LIST");
        System.out.println("2) BST");
        System.out.println("3) AVL");
        System.out.println("4) BST + AVL");
        System.out.println("0) Cancelar");

        int structureOption = readInt("Opción: ");

        IndexFactory[] factories;

        switch (structureOption) {
            case 1 -> factories = new IndexFactory[]{new ListIndexFactory()};
            case 2 -> factories = new IndexFactory[]{new BstIndexFactory()};
            case 3 -> factories = new IndexFactory[]{new AvlIndexFactory()};
            case 4 -> factories = new IndexFactory[]{new BstIndexFactory(), new AvlIndexFactory()};
            case 0 -> {
                System.out.println("Benchmark cancelado.");
                return;
            }
            default -> {
                System.out.println("Opción inválida.");
                return;
            }
        }

        BenchmarkOperation[] operations = chooseOperations();
        if (operations == null) {
            System.out.println("Benchmark cancelado.");
            return;
        }

        BenchmarkConfig config = chooseBenchmarkConfig();

        boolean append = askAppendResults();

        runBenchmark(config, factories, operations, append);
    }

    private BenchmarkOperation[] chooseOperations() {
        System.out.println("\n--- Seleccionar operación ---");
        System.out.println("1) Todas");
        System.out.println("2) Solo PUT");
        System.out.println("3) Solo GET");
        System.out.println("4) Solo REMOVE");
        System.out.println("0) Cancelar");

        int op = readInt("Opción: ");

        return switch (op) {
            case 1 -> allOperations();
            case 2 -> new BenchmarkOperation[]{BenchmarkOperation.PUT};
            case 3 -> new BenchmarkOperation[]{BenchmarkOperation.GET};
            case 4 -> new BenchmarkOperation[]{BenchmarkOperation.REMOVE};
            case 0 -> null;
            default -> {
                System.out.println("Opción inválida.");
                yield null;
            }
        };
    }

    private BenchmarkConfig chooseBenchmarkConfig() {
        System.out.println("\n--- Configuración ---");
        System.out.println("1) Rápida");
        System.out.println("2) Completa");
        System.out.println("0) Rápida por defecto");

        int op = readInt("Opción: ");

        return switch (op) {
            case 2 -> BenchmarkConfig.defaultConfig();
            default -> BenchmarkConfig.quickConfig();
        };
    }

    private boolean askAppendResults() {
        String ans = readLine("¿Agregar al CSV existente? (s/n): ").trim().toLowerCase();
        return ans.equals("s");
    }

    private IndexFactory[] allFactories() {
        return new IndexFactory[]{
                new ListIndexFactory(),
                new BstIndexFactory(),
                new AvlIndexFactory()
        };
    }

    private BenchmarkOperation[] allOperations() {
        return new BenchmarkOperation[]{
                BenchmarkOperation.PUT,
                BenchmarkOperation.GET,
                BenchmarkOperation.REMOVE
        };
    }

    // -------- Helpers de input/output --------
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un entero válido.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private SportEnum readSportEnum() {
        printSportsCatalog();
        String raw = readLine("Código deporte (ej: FUTBOL, NATACIÓN): ").trim().toUpperCase();
        try {
            return SportEnum.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Deporte inválido: " + raw);
        }
    }

    private void printSportsCatalog() {
        System.out.println("\nCatálogo:");
        for (SportEnum sp : SportEnum.values()) {
            System.out.println("- " + sp.name() + " => " + sp.displayName());
        }
        System.out.println();
    }

    private void printStudent(Student s) {
        System.out.println("ID=" + s.getId() + " | Nombre=" + s.getName());
        System.out.println("  Practica: " + joinSports(s.getPractice()));
        System.out.println("  Interés : " + joinSports(s.getInterest()));
        System.out.println();
    }

    private String joinSports(co.unal.deportesunal.structure.listadt.LinkedList<SportEnum> list) {
        StringBuilder sb = new StringBuilder();
        list.traverse(new ListVisitor<SportEnum>() {
            @Override
            public void visit(SportEnum sp) {
                if (sp == null) return;
                if (!sb.isEmpty()) sb.append(", ");
                sb.append(sp.displayName());
            }
        });
        return sb.isEmpty() ? "(vacio)" : sb.toString();
    }
}