package co.unal.deportesunal.persistence;

import java.io.File;

public class FileConstant {
    public static final String DATA_DIR =
            System.getProperty("user.dir") + File.separator + "data";

    public static final String PERSISTENCE_DIR =
            DATA_DIR + File.separator + "persistence";

    public static final String MOCK_DIR =
            DATA_DIR + File.separator + "mock";

    public static final String RESULTS_DIR =
            DATA_DIR + File.separator + "results";

    public static final String STUDENTS_FILE =
            PERSISTENCE_DIR + File.separator + "students.txt";

    public static String mockStudentsFile(int n, long seed) {
        return MOCK_DIR + File.separator + "students_" + n + "_seed" + seed + ".txt";
    }

    public static final String INDEX_BENCHMARK_RESULTS =
            RESULTS_DIR + File.separator + "index_benchmark.csv";
}