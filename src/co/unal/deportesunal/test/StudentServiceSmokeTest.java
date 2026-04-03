package co.unal.deportesunal.test;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.persistence.FileConstant;
import co.unal.deportesunal.persistence.TxtStudentRepository;
import co.unal.deportesunal.service.StudentService;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.tree.ListStudentIndex;
import co.unal.deportesunal.structure.tree.StudentIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StudentServiceSmokeTest {

    public static void main(String[] args) throws Exception {

        StudentIndex index = new ListStudentIndex();
        TxtStudentRepository repo = new TxtStudentRepository();
        StudentService service = new StudentService(index, repo);

        printHeader("INICIO - estado vacío");
        printServiceState(service);

        // 1) Seed de 10 estudiantes + deportes
        seedStudents(service);

        printHeader("Después de registrar 10 estudiantes + deportes");
        printServiceState(service);

        // 2) Duplicado
        try {
            service.registerStudent(new Student(1, "Otra"));
            fail("Se esperaba DuplicatedIdException");
        } catch (DuplicatedIdException ok) {
            System.out.println("OK: duplicado detectado");
        }

        // 3) Buscar y validar un caso
        Student s1 = service.findStudentById(1);
        assertEquals("Ana", s1.getName(), "Nombre estudiante 1");

        // 4) Cambios adicionales para verlos reflejados
        assertTrue(service.addPracticeSport(1, SportEnum.FUTBOL), "Agregar FUTBOL práctica (Ana)");
        assertFalse(service.addPracticeSport(1, SportEnum.FUTBOL), "No duplicar FUTBOL práctica (Ana)");
        assertTrue(service.addInterestSport(1, SportEnum.NATACION), "Agregar NATACION interés (Ana)");

        assertTrue(service.removePracticeSport(4, SportEnum.RUGBY), "Remover RUGBY práctica (ID=4)");
        assertTrue(service.addPracticeSport(4, SportEnum.VOLEIBOL), "Agregar VOLEIBOL práctica (ID=4)");

        printHeader("Después de cambios puntuales (IDs 1 y 4)");
        printServiceState(service);

        // 5) Guardar e imprimir archivo
        service.saveToRepository();
        System.out.println("OK: guardado");

        printHeader("Contenido de students.txt después de guardar");
        printStudentsTxt();

        // 6) Cargar en otro índice (round-trip)
        StudentIndex index2 = new ListStudentIndex();
        StudentService service2 = new StudentService(index2, repo);
        service2.loadFromRepository();

        printHeader("Estado después de cargar en un índice nuevo (round-trip)");
        printServiceState(service2);

        assertEquals(10, service2.totalStudents(), "Total tras cargar");

        // 7) Eliminar un estudiante y re-guardar para ver el cambio
        assertTrue(service2.removeStudentById(10), "Eliminar ID=10");
        assertFalse(service2.removeStudentById(999), "Eliminar inexistente");

        printHeader("Después de eliminar ID=10 (en service2)");
        printServiceState(service2);

        service2.saveToRepository();
        printHeader("Contenido de students.txt después de guardar tras eliminar");
        printStudentsTxt();

        System.out.println("\n✅ Smoke test terminado sin fallos.");
    }

    private static void seedStudents(StudentService service) throws Exception {
        // IDs 1..10 (nombres fijos)
        service.registerStudent(new Student(1, "Ana"));
        service.registerStudent(new Student(2, "Luis"));
        service.registerStudent(new Student(3, "Camila"));
        service.registerStudent(new Student(4, "Mateo"));
        service.registerStudent(new Student(5, "Sofia"));
        service.registerStudent(new Student(6, "Juan"));
        service.registerStudent(new Student(7, "Valentina"));
        service.registerStudent(new Student(8, "Carlos"));
        service.registerStudent(new Student(9, "Diana"));
        service.registerStudent(new Student(10, "Andres"));

        // Deportes (práctica / interés) variados para generar conexiones
        service.addPracticeSport(2, SportEnum.BALONCESTO);
        service.addInterestSport(2, SportEnum.FUTBOL);

        service.addPracticeSport(3, SportEnum.NATACION);
        service.addInterestSport(3, SportEnum.TAEKWONDO);

        service.addPracticeSport(4, SportEnum.RUGBY);
        service.addInterestSport(4, SportEnum.VOLEIBOL);

        service.addPracticeSport(5, SportEnum.VOLEIBOL);
        service.addInterestSport(5, SportEnum.RUGBY);

        service.addPracticeSport(6, SportEnum.TAEKWONDO);
        service.addInterestSport(6, SportEnum.NATACION);

        service.addPracticeSport(7, SportEnum.FUTBOL);
        service.addInterestSport(7, SportEnum.BALONCESTO);

        service.addPracticeSport(8, SportEnum.AJEDREZ);
        service.addInterestSport(8, SportEnum.FUTBOL);

        service.addPracticeSport(9, SportEnum.TENIS);
        service.addInterestSport(9, SportEnum.NATACION);

        service.addPracticeSport(10, SportEnum.ULTIMATE);
        service.addInterestSport(10, SportEnum.VOLEIBOL);
    }

    private static void printServiceState(StudentService service) {
        System.out.println("Total estudiantes: " + service.totalStudents());

        var list = service.listStudentsOrderedById();

        list.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) {
                if (s == null) return;

                System.out.println("- ID=" + s.getId() + " | Nombre=" + s.getName());
                System.out.println("  Practica : " + joinSports(s.getPractice()));
                System.out.println("  Interes  : " + joinSports(s.getInterest()));
            }
        });
    }

    private static String joinSports(co.unal.deportesunal.structure.listadt.LinkedList<SportEnum> list) {
        StringBuilder sb = new StringBuilder();
        list.traverse(new ListVisitor<SportEnum>() {
            @Override
            public void visit(SportEnum sport) {
                if (sport == null) return;
                if (sb.length() > 0) sb.append(", ");
                sb.append(sport.displayName()); // ✅ nombre para mostrar
            }
        });
        return sb.length() == 0 ? "(vacio)" : sb.toString();
    }

    private static void printStudentsTxt() {
        String path = FileConstant.STUDENTS_FILE;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + path);
            e.printStackTrace();
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n==============================");
        System.out.println(title);
        System.out.println("==============================");
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError("FAIL: " + msg);
        System.out.println("OK: " + msg);
    }

    private static void assertFalse(boolean cond, String msg) {
        assertTrue(!cond, msg);
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("FAIL: " + msg + " | expected=" + expected + " actual=" + actual);
        }
        System.out.println("OK: " + msg);
    }

    private static void fail(String msg) {
        throw new AssertionError("FAIL: " + msg);
    }
}