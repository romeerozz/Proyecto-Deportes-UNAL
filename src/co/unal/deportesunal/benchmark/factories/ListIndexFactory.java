package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.tree.ListStudentIndex;
import co.unal.deportesunal.structure.tree.StudentIndex;

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
