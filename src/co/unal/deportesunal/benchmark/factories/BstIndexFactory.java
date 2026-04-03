package co.unal.deportesunal.benchmark.factories;

import co.unal.deportesunal.structure.tree.BstIndex;
import co.unal.deportesunal.structure.tree.StudentIndex;

public class BstIndexFactory implements IndexFactory {
    @Override
    public String name() {
        return "BST";
    }

    @Override
    public StudentIndex create() {
        return new BstIndex();
    }
}
