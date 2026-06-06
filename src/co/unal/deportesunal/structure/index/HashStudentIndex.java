package co.unal.deportesunal.structure.index;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.domain.exception.DuplicatedIdException;
import co.unal.deportesunal.domain.exception.NotFoundException;
import co.unal.deportesunal.structure.hash.HashTable;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

public class HashStudentIndex implements StudentIndex {

    private final HashTable<Integer, Student> table;

    public HashStudentIndex() {
        this.table = new HashTable<>();
    }

    @Override
    public void put(int id, Student student) throws DuplicatedIdException {
        if (table.containsKey(id)) throw new DuplicatedIdException("ID already exists: " + id);
        table.put(id, student);
    }

    @Override
    public Student get(int id) throws NotFoundException {
        Student s = table.get(id);
        if (s == null) throw new NotFoundException("Student not found: " + id);
        return s;
    }

    @Override
    public boolean remove(int id) {
        return table.remove(id);
    }

    @Override
    public LinkedList<Student> valuesInOrder() {
        LinkedList<Student> vals = table.values();
        int n = vals.size();
        if (n <= 1) return vals;

        // copy to array
        final Student[] arr = new Student[n];
        final int[] idx = {0};
        vals.traverse(new ListVisitor<Student>() {
            @Override
            public void visit(Student s) { arr[idx[0]++] = s; }
        });

        // selection sort by id ascending
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j].getId() < arr[min].getId()) min = j;
            }
            if (min != i) {
                Student t = arr[i]; arr[i] = arr[min]; arr[min] = t;
            }
        }

        LinkedList<Student> out = new LinkedList<>();
        for (Student s : arr) out.pushBack(s);
        return out;
    }

    @Override
    public boolean contains(int id) {
        return table.containsKey(id);
    }

    @Override
    public int size() {
        return table.size();
    }
}
