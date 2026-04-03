package co.unal.deportesunal.ui;

import co.unal.deportesunal.controller.AppController;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.util.Scanner;

public class ConsoleUi {

    private final AppController controller;
    private final Scanner sc = new Scanner(System.in);

    public ConsoleUi(AppController controller) {
        this.controller = controller;
    }

    public void run() {
        System.out.println("=== Deportes UNAL - Prototipo (Consola) ===");

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
        System.out.println("3) Cargar desde archivo");
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
                    case 0 -> back = true;
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
        try {
            controller.load();
            System.out.println("Datos cargados.");
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
        // TODO: cuando tengas BenchmarkRunner listo:
        // controller.runBenchmarks();
        System.out.println("Benchmarks: pendiente de implementación (BenchmarkRunner).");
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