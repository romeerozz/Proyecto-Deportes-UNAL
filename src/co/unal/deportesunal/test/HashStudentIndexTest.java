package co.unal.deportesunal.test;

import co.unal.deportesunal.structure.index.HashStudentIndex;
import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;

public class HashStudentIndexTest {
    public static boolean run() {
        HashStudentIndex idx = new HashStudentIndex();
        boolean ok = true;
        try {
            Student s1 = new Student(100, "Alice");
            Student s2 = new Student(200, "Bob");
            idx.put(s1.getId(), s1);
            idx.put(s2.getId(), s2);
            ok &= idx.contains(100);
            ok &= idx.size() == 2;
            Student got = idx.get(200);
            ok &= got.getName().equals("Bob");
            ok &= idx.remove(100);
            ok &= !idx.contains(100);
            try {
                idx.get(100);
                ok = false; // should throw
            } catch (NotFoundException e) { /* expected */ }
            try {
                idx.put(s2.getId(), s2);
                ok = false; // duplicate should throw
            } catch (DuplicatedIdException e) { /* expected */ }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = run();
        System.out.println("HashStudentIndexTest: " + (ok ? "PASS" : "FAIL"));
        if (!ok) System.exit(1);
    }
}
