package co.unal.deportesunal.benchmark;

import co.unal.deportesunal.domain.Student;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.tree.StudentIndex;

public class IndexBenchmark {

    public long benchPut(StudentIndex index, LinkedList<Student> students) {
        //TODO
        return 0;
    }

    public long benchGet(StudentIndex index, int[] queryIds) {
        //TODO
        return 0;
    }

    public long benchRemove(StudentIndex index, int[] removeIds) {
        //TODO
        return 0;
    }
}