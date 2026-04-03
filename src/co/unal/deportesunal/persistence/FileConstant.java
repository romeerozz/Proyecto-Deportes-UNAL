package co.unal.deportesunal.persistence;

import java.io.File;

public class FileConstant {
    public static final String STUDENTS_FILE =
            System.getProperty("user.dir") + File.separator +
                    "data" + File.separator + "persistence" + File.separator + "students.txt";
}