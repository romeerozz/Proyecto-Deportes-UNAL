package co.unal.deportesunal.test;

import co.unal.deportesunal.structure.graphadt.AdjacencyListGraph;
import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;

public class AdjacencyListGraphTest {
    public static boolean run() {
        AdjacencyListGraph<String> g = new AdjacencyListGraph<>();
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addVertex("D");

        LinkedList<LinkedList<String>> comps = g.connectedComponents();
        final int[] count = {0};
        final boolean[] has3 = {false};
        final boolean[] has1 = {false};
        comps.traverse(new ListVisitor<LinkedList<String>>() {
            @Override
            public void visit(LinkedList<String> comp) {
                count[0]++;
                if (comp.size() == 3) has3[0] = true;
                if (comp.size() == 1) has1[0] = true;
            }
        });
        boolean ok = (count[0] == 2) && has3[0] && has1[0];
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = run();
        System.out.println("AdjacencyListGraphTest: " + (ok ? "PASS" : "FAIL"));
        if (!ok) System.exit(1);
    }
}
