package co.unal.deportesunal.ui;

import co.unal.deportesunal.benchmark.BenchmarkConfig;
import co.unal.deportesunal.benchmark.BenchmarkOperation;
import co.unal.deportesunal.benchmark.BenchmarkRunner;
import co.unal.deportesunal.benchmark.factories.AvlIndexFactory;
import co.unal.deportesunal.benchmark.factories.BstIndexFactory;
import co.unal.deportesunal.benchmark.factories.IndexFactory;
import co.unal.deportesunal.benchmark.factories.ListIndexFactory;
import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.domain.SportCount;
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
                case 2 -> communityAnalysisFlow();
                case 3 -> connectionAnalysisFlow();
                case 4 -> statsAnalysisFlow();
                case 5 -> runBenchmarksFlow();
                case 6 -> loadFlow();
                case 7 -> saveFlow();
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
        System.out.println("2) Análisis de comunidades deportivas");
        System.out.println("3) Análisis de conexiones");
        System.out.println("4) Análisis de estadísticas");
        System.out.println("5) Ejecutar benchmarks");
        System.out.println("6) Recargar desde el archivo (sobreescribe el estado actual)");
        System.out.println("7) Guardar a archivo");
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

    private void communityAnalysisFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- An\u00e1lisis de Comunidades ---");
            System.out.println("1) Listar todas las comunidades");
            System.out.println("2) Ver comunidad de un estudiante");
            System.out.println("0) Volver");

            int op = readInt("Opci\u00f3n: ");

            switch (op) {
                case 1 -> listAllCommunities();
                case 2 -> viewStudentCommunity();
                case 0 -> back = true;
                default -> System.out.println("Opci\u00f3n inv\u00e1lida.");
            }
        }
    }

    private void listAllCommunities() {
        try {
            LinkedList<LinkedList<Student>> communities = controller.getCommunities();
            int totalCommunities = controller.getTotalCommunities();
            System.out.println("\nTotal de comunidades: " + totalCommunities);

            final int[] communityNum = {1};
            communities.traverse(new ListVisitor<LinkedList<Student>>() {
                @Override
                public void visit(LinkedList<Student> community) {
                    if (community != null) {
                        System.out.println("\nComunidad " + communityNum[0] + " (tama\u00f1o: " + community.size() + "):");
                        community.traverse(new ListVisitor<Student>() {
                            @Override
                            public void visit(Student s) {
                                if (s != null) {
                                    System.out.println("  - " + s.getName() + " (ID=" + s.getId() + ") practica: " 
                                        + joinSports(s.getPractice()));
                                }
                            }
                        });
                        communityNum[0]++;
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewStudentCommunity() {
        try {
            int id = readInt("ID del estudiante: ");
            Student student = controller.findStudent(id);
            
            LinkedList<Student> community = controller.getStudentCommunity(id);
            int communitySize = community.size();
            
            System.out.println("\n" + student.getName() + " (ID=" + id + ") est\u00e1 en una comunidad de " + communitySize + " miembros:");
            
            community.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s) {
                    if (s != null) {
                        String marker = s.getId() == id ? " <-- (t\u00fa)" : "";
                        System.out.println("  - " + s.getName() + " (ID=" + s.getId() + ")" + marker);
                        System.out.println("    Practica: " + joinSports(s.getPractice()));
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void connectionAnalysisFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- An\u00e1lisis de Conexiones ---");
            System.out.println("1) Verificar conexi\u00f3n a un deporte");
            System.out.println("2) Ver deportistas de un deporte en mi comunidad");
            System.out.println("3) Ver todos los deportes en mi comunidad");
            System.out.println("0) Volver");

            int op = readInt("Opci\u00f3n: ");

            switch (op) {
                case 1 -> checkConnectionToSport();
                case 2 -> viewPractitionersOfSport();
                case 3 -> viewAllSportsInCommunity();
                case 0 -> back = true;
                default -> System.out.println("Opci\u00f3n inv\u00e1lida.");
            }
        }
    }

    private void checkConnectionToSport() {
        try {
            int id = readInt("Tu ID: ");
            Student student = controller.findStudent(id);
            SportEnum sport = readSportEnum();
            
            boolean hasConnection = controller.hasConnectionToSport(id, sport);
            
            if (hasConnection) {
                System.out.println("\n\u2713 S\u00cd, existe una conexi\u00f3n en tu comunidad a alguien que practica " 
                    + sport.displayName() + "!");
                LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);
                System.out.println("Practicantes en tu comunidad (" + practitioners.size() + "):");
                practitioners.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s) {
                        if (s != null) {
                            System.out.println("  - " + s.getName() + " (ID=" + s.getId() + ")");
                        }
                    }
                });
            } else {
                System.out.println("\n\u2717 No existe conexi\u00f3n en tu comunidad a alguien que practique " 
                    + sport.displayName());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewPractitionersOfSport() {
        try {
            int id = readInt("Tu ID: ");
            Student student = controller.findStudent(id);
            SportEnum sport = readSportEnum();
            
            LinkedList<Student> practitioners = controller.getPractitionersInCommunity(id, sport);
            int count = controller.countPractitionersInCommunity(id, sport);
            
            System.out.println("\nPracticantes de " + sport.displayName() + " en tu comunidad: " + count);
            if (count > 0) {
                practitioners.traverse(new ListVisitor<Student>() {
                    @Override
                    public void visit(Student s) {
                        if (s != null) {
                            System.out.println("  - " + s.getName() + " (ID=" + s.getId() + ")");
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewAllSportsInCommunity() {
        try {
            int id = readInt("Tu ID: ");
            Student student = controller.findStudent(id);
            
            LinkedList<SportEnum> sports = controller.getSportsInCommunity(id);
            System.out.println("\nDeportes practicados en tu comunidad (" + sports.size() + "):");
            sports.traverse(new ListVisitor<SportEnum>() {
                @Override
                public void visit(SportEnum sport) {
                    if (sport != null) {
                        int practitioners = controller.countPractitionersInCommunity(id, sport);
                        System.out.println("  - " + sport.displayName() + " (" + practitioners + " practicantes)");
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void statsAnalysisFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Análisis de Estadísticas ---");
            System.out.println("1) Ver ranking de deportes");
            System.out.println("2) Ver deporte más practicado");
            System.out.println("3) Ver deporte menos practicado");
            System.out.println("4) Contar practicantes de un deporte");
            System.out.println("5) Ver estadísticas generales");
            System.out.println("0) Volver");

            int op = readInt("Opción: ");

            switch (op) {
                case 1 -> viewRankingSports();
                case 2 -> viewMostPracticedSport();
                case 3 -> viewLeastPracticedSport();
                case 4 -> countPractitionersOfSport();
                case 5 -> viewGeneralStats();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void viewRankingSports() {
        try {
            LinkedList<SportCount> ranking = controller.getRankingSports();
            
            if (ranking.size() == 0) {
                System.out.println("\nNo hay deportes practicados aún.");
                return;
            }

            System.out.println("\n=== Ranking de Deportes ===");
            final int[] position = {1};
            ranking.traverse(new ListVisitor<SportCount>() {
                @Override
                public void visit(SportCount sc) {
                    if (sc != null) {
                        System.out.println(position[0] + ". " + sc.getSport().displayName() 
                            + " (" + sc.getCount() + " practicantes)");
                        position[0]++;
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewMostPracticedSport() {
        try {
            SportCount top = controller.getMostPracticedSport();
            if (top != null) {
                System.out.println("\n🏆 Deporte más practicado: " + top.getSport().displayName() 
                    + " (" + top.getCount() + " estudiantes)");
            } else {
                System.out.println("\nNo hay deportes practicados aún.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewLeastPracticedSport() {
        try {
            SportCount least = controller.getLeastPracticedSport();
            if (least != null) {
                System.out.println("\n📊 Deporte menos practicado: " + least.getSport().displayName() 
                    + " (" + least.getCount() + " estudiantes)");
            } else {
                System.out.println("\nNo hay deportes practicados aún.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void countPractitionersOfSport() {
        try {
            SportEnum sport = readSportEnum();
            int count = controller.getPractitionersCount(sport);
            System.out.println("\nPracticantes de " + sport.displayName() + ": " + count);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewGeneralStats() {
        try {
            String stats = controller.getGeneralStats();
            System.out.println("\n" + stats);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
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
                            FileConstant.INDEX_BENCHMARK_FULL,
                            false
                    );

                    case 2 -> runBenchmark(
                            BenchmarkConfig.quickConfig(),
                            allFactories(),
                            allOperations(),
                            FileConstant.indexBenchmarkResult("quick"),
                            false
                    );

                    case 3 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.PUT},
                            FileConstant.indexBenchmarkResult("put"),
                            false
                    );

                    case 4 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.GET},
                            FileConstant.indexBenchmarkResult("get"),
                            false
                    );

                    case 5 -> runBenchmark(
                            BenchmarkConfig.defaultConfig(),
                            allFactories(),
                            new BenchmarkOperation[]{BenchmarkOperation.REMOVE},
                            FileConstant.indexBenchmarkResult("remove"),
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
            String outputPath,
            boolean append
    ) throws Exception {
        System.out.println("\nEjecutando benchmark...");
        System.out.println("Resultados: " + outputPath);

        benchmarkRunner.runOperations(config, factories, operations, outputPath, append);

        System.out.println("Benchmark terminado.");
        System.out.println("CSV generado en: " + outputPath);
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

        String label = readLine("Nombre para el archivo CSV (ej: avl_put, bst_avl_get): ")
                .trim()
                .toLowerCase();

        if (label.isEmpty()) {
            label = "custom";
        }

        String outputPath = FileConstant.indexBenchmarkResult(label);

        runBenchmark(config, factories, operations, outputPath, append);
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
