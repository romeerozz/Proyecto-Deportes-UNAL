package co.unal.deportesunal.benchmark.utils;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.structure.listadt.LinkedList;

import java.util.Random;

/**
 * Generador de datos mock para las pruebas de rendimiento.
 * Crea listas de estudiantes con IDs únicos, nombres generados y deportes
 * aleatorios (práctica e interés), así como arreglos de IDs para consultas y eliminaciones.
 */
public class MockDataGenerator {

    private static final int MIN_SPORTS_PER_LIST = 1;
    private static final int MAX_SPORTS_PER_LIST = 3;

    /**
     * Genera una lista de estudiantes con IDs barajados y deportes aleatorios.
     *
     * @param n    cantidad de estudiantes a generar
     * @param seed semilla para la generación aleatoria
     * @return lista enlazada de estudiantes
     * @throws IllegalArgumentException si n es negativo
     */
    public LinkedList<Student> generateStudents(int n, long seed) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be greater than or equal to 0.");
        }

        Random random = new Random(seed);
        LinkedList<Student> students = new LinkedList<>();

        int[] ids = generateSequentialIds(n);
        shuffle(ids, random);

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];

            Student student = new Student(id, buildName(id));

            int practiceCount = randomSportCount(random);
            int interestCount = randomSportCount(random);

            addRandomSports(student, practiceCount, true, random);
            addRandomSports(student, interestCount, false, random);

            students.pushBack(student);
        }

        return students;
    }

    /**
     * Genera IDs aleatorios entre 1 y n.
     * Puede repetir IDs.
     * Útil para operaciones get(), donde consultar varias veces el mismo ID es válido.
     */
    public int[] generateRandomIds(int n, int count, long seed) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0.");
        }
        if (count < 0) {
            throw new IllegalArgumentException("count must be greater than or equal to 0.");
        }

        Random random = new Random(seed);
        int[] ids = new int[count];

        for (int i = 0; i < count; i++) {
            ids[i] = 1 + random.nextInt(n);
        }

        return ids;
    }

    /**
     * Genera IDs únicos entre 1 y n.
     * Útil para remove(), porque evita intentar eliminar el mismo ID varias veces.
     */
    public int[] generateUniqueRandomIds(int n, int count, long seed) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0.");
        }
        if (count < 0 || count > n) {
            throw new IllegalArgumentException("count must be between 0 and n.");
        }

        int[] ids = generateSequentialIds(n);
        Random random = new Random(seed);
        shuffle(ids, random);

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = ids[i];
        }

        return result;
    }

    /**
     * Genera un arreglo de IDs secuenciales de 1 a n.
     *
     * @param n cantidad de IDs
     * @return arreglo de IDs secuenciales
     */
    private int[] generateSequentialIds(int n) {
        int[] ids = new int[n];

        for (int i = 0; i < n; i++) {
            ids[i] = i + 1;
        }

        return ids;
    }

    /**
     * Agrega una cantidad determinada de deportes aleatorios a un estudiante.
     *
     * @param student  estudiante al que agregar deportes
     * @param count    cantidad de deportes a agregar
     * @param practice true para deportes de práctica, false para interés
     * @param random   generador de números aleatorios
     */
    private void addRandomSports(Student student, int count, boolean practice, Random random) {
        SportEnum[] sports = SportEnum.values();
        int added = 0;

        while (added < count) {
            SportEnum sport = sports[random.nextInt(sports.length)];

            boolean success;
            if (practice) {
                success = student.addPractice(sport);
            } else {
                success = student.addInterest(sport);
            }

            if (success) {
                added++;
            }
        }
    }

    /**
     * Genera una cantidad aleatoria de deportes dentro del rango configurado.
     *
     * @param random generador de números aleatorios
     * @return cantidad aleatoria entre MIN_SPORTS_PER_LIST y MAX_SPORTS_PER_LIST
     */
    private int randomSportCount(Random random) {
        return MIN_SPORTS_PER_LIST + random.nextInt(MAX_SPORTS_PER_LIST);
    }

    /**
     * Construye un nombre de estudiante con formato "Student_XXXXXX".
     *
     * @param id identificador del estudiante
     * @return nombre formateado
     */
    private String buildName(int id) {
        return "Student_" + padLeft(id, 6);
    }

    /**
     * Rellena un número con ceros a la izquierda hasta alcanzar el ancho dado.
     *
     * @param value valor numérico
     * @param width ancho total deseado
     * @return cadena con ceros a la izquierda
     */
    private String padLeft(int value, int width) {
        String text = String.valueOf(value);
        StringBuilder sb = new StringBuilder();

        for (int i = text.length(); i < width; i++) {
            sb.append('0');
        }

        sb.append(text);
        return sb.toString();
    }

    /**
     * Baraja un arreglo de enteros usando el algoritmo de Fisher-Yates.
     *
     * @param array  arreglo a barajar
     * @param random generador de números aleatorios
     */
    private void shuffle(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}