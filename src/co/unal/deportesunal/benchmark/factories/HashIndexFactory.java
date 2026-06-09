package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.index.HashStudentIndex;
import co.unal.deportesunal.structure.index.StudentIndex;

public class HashIndexFactory implements IndexFactory {
    @Override
    public String name() {
        return "HASH";
    }

    @Override
    public StudentIndex create() {
        return new HashStudentIndex();
    }
}
