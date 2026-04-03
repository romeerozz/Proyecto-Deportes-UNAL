package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.tree.StudentIndex;

public interface IndexFactory {
    String name();
    StudentIndex create();
}
