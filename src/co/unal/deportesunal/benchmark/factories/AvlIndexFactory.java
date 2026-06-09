package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.index.AvlIndex;
import co.unal.deportesunal.structure.index.StudentIndex;

public class AvlIndexFactory implements IndexFactory {
    @Override
    public String name() {
        return "AVL";
    }

    @Override
    public StudentIndex create() {
        return new AvlIndex();
    }
}
