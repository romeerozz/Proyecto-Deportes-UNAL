package co.unal.deportesunal.persistence;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.SportEnum;
import co.unal.deportesunal.domain.exception.DataAccessException;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

import java.io.*;

public class TxtStudentRepository implements StudentRepository {

    private final File fileLocation = new File(FileConstant.STUDENTS_FILE);

    public TxtStudentRepository() throws DataAccessException {
        try {
            File parentDir = fileLocation.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new DataAccessException("There was an error creating the file directory");
                }
            }
            if (!fileLocation.exists()) {
                if (!fileLocation.createNewFile()) {
                    throw new DataAccessException("There was an error creating the file");
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("There was an error initializing students file", e);
        }
        System.out.println("user.dir = " + System.getProperty("user.dir"));
        System.out.println("students abs = " + fileLocation.getAbsolutePath());
        System.out.println("exists=" + fileLocation.exists() + ", length=" + fileLocation.length());
    }

    @Override
    public LinkedList<Student> load() throws DataAccessException {
        LinkedList<Student> students = new LinkedList<>();

        if (fileLocation.length() == 0) {
            return students;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                Student s = parseLine(line);
                students.pushBack(s);
            }
            return students;

        } catch (IOException e) {
            throw new DataAccessException("There was an error reading the file", e);
        }
    }

    @Override
    public void save(LinkedList<Student> students) throws DataAccessException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileLocation, false))) {
            bw.write("# id;name;PRACTICE(comma);INTEREST(comma)");
            bw.newLine();

            students.traverse(new ListVisitor<Student>() {
                @Override
                public void visit(Student s) {
                    try {
                        bw.write(serializeStudent(s));
                        bw.newLine();
                    } catch (IOException e) {
                        // No podemos lanzar checked exception desde aquí, así que envolvemos
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw new DataAccessException("There was an error writing the file", e.getCause());
            }
            throw e;
        } catch (IOException e) {
            throw new DataAccessException("There was an error writing the file", e);
        }
    }

    private Student parseLine(String line) throws DataAccessException {
        String[] parts = line.split(";", -1); // conserva vacíos
        if (parts.length < 4) {
            throw new DataAccessException("Invalid line (expected 4 fields): " + line);
        }

        int id;
        try {
            id = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new DataAccessException("Invalid ID in line: " + line, e);
        }

        String name = parts[1].trim();
        Student s = new Student(id, name);

        parseSports(parts[2], true, s);
        parseSports(parts[3], false, s);

        return s;
    }

    private void parseSports(String field, boolean practice, Student s) throws DataAccessException {
        String trimmed = field.trim();
        if (trimmed.isEmpty()) return;

        String[] tokens = trimmed.split(",");
        for (String raw : tokens) {
            String token = raw.trim();
            if (token.isEmpty()) continue;

            try {
                SportEnum sport = SportEnum.valueOf(token);

                if (practice) s.addPractice(sport);
                else s.addInterest(sport);

            } catch (IllegalArgumentException e) {
                throw new DataAccessException("Invalid sport '" + token + "' in file.", e);
            }
        }
    }

    private String serializeStudent(Student s) {
        return s.getId() + ";" +
                escapeName(s.getName()) + ";" +
                joinSports(s, true) + ";" +
                joinSports(s, false);
    }

    private String escapeName(String name) {
        return name == null ? "" : name.replace(";", " ");
    }

    private String joinSports(Student s, boolean practice) {
        final StringBuilder sb = new StringBuilder();

        LinkedList<SportEnum> list = practice ? s.getPractice() : s.getInterest();

        list.traverse(new ListVisitor<SportEnum>() {
            @Override
            public void visit(SportEnum sport) {
                if (sport == null) return;
                if (sb.length() > 0) sb.append(",");
                sb.append(sport.name());
            }
        });

        return sb.toString();
    }
}