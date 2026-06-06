package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.index.StudentIndex;

public interface IndexFactory {
    String name();
    StudentIndex create();
}
