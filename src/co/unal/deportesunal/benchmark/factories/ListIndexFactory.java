package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.index.ListStudentIndex;
import co.unal.deportesunal.structure.index.StudentIndex;

public class ListIndexFactory implements IndexFactory {
    @Override
    public String name() {
        return "LIST";
    }

    @Override
    public StudentIndex create() {
        return new ListStudentIndex();
    }
}
