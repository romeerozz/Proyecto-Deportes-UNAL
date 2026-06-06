package co.unal.deportesunal.structure.graphadt;

import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.queue.ArrayQueue;

/** Simple undirected adjacency-list graph implementation. Vertices are generic values. */
public class AdjacencyListGraph<T> implements Graph<T> {

    private final LinkedList<T> vertices;
    private final LinkedList<LinkedList<T>> adjLists;

    public AdjacencyListGraph() {
        this.vertices = new LinkedList<>();
        this.adjLists = new LinkedList<>();
    }

    public void addVertex(T v) {
        if (containsVertex(v)) return;
        vertices.pushBack(v);
        adjLists.pushBack(new LinkedList<>());
    }

    private boolean containsVertex(T v) {
        return vertices.contains(v);
    }

    private int indexOf(T v) {
        final int[] idx = {-1};
        final int[] i = {0};
        vertices.traverse(new ListVisitor<T>() {
            @Override
            public void visit(T value) {
                if (idx[0] != -1) { i[0]++; return; }
                if (value == null ? v == null : value.equals(v)) {
                    idx[0] = i[0];
                }
                i[0]++;
            }
        });
        return idx[0];
    }

    private LinkedList<T> getAdjListAtIndex(int idx) {
        final LinkedList<T>[] out = new LinkedList[1];
        final int[] i = {0};
        adjLists.traverse(new ListVisitor<LinkedList<T>>() {
            @Override
            public void visit(LinkedList<T> value) {
                if (i[0] == idx) out[0] = value;
                i[0]++;
            }
        });
        return out[0];
    }

    public void addEdge(T a, T b) {
        if (a == null || b == null) return;
        addVertex(a);
        addVertex(b);
        int ia = indexOf(a);
        int ib = indexOf(b);
        LinkedList<T> la = getAdjListAtIndex(ia);
        LinkedList<T> lb = getAdjListAtIndex(ib);
        if (!la.contains(b)) la.pushBack(b);
        if (!lb.contains(a)) lb.pushBack(a);
    }

    public LinkedList<T> neighbors(T v) {
        int idx = indexOf(v);
        if (idx == -1) return new LinkedList<>();
        LinkedList<T> list = getAdjListAtIndex(idx);
        // return a shallow copy
        LinkedList<T> copy = new LinkedList<>();
        list.traverse(new ListVisitor<T>() {
            @Override
            public void visit(T value) { copy.pushBack(value); }
        });
        return copy;
    }

    /** BFS component from start vertex */
    public LinkedList<T> bfsComponent(T start) {
        LinkedList<T> component = new LinkedList<>();
        if (!containsVertex(start)) return component;

        // visited tracker using vertices.contains on component (O(n) checks ok for small n)
        ArrayQueue<T> queue = new ArrayQueue<>();
        queue.enqueue(start);
        component.pushBack(start);

        while (!queue.isEmpty()) {
            T cur = queue.dequeue();
            LinkedList<T> neigh = neighbors(cur);
            neigh.traverse(new ListVisitor<T>() {
                @Override
                public void visit(T v) {
                    if (!component.contains(v)) {
                        component.pushBack(v);
                        queue.enqueue(v);
                    }
                }
            });
        }

        return component;
    }

    /** All connected components */
    public LinkedList<LinkedList<T>> connectedComponents() {
        LinkedList<LinkedList<T>> comps = new LinkedList<>();
        vertices.traverse(new ListVisitor<T>() {
            @Override
            public void visit(T v) {
                // if v not in any existing comp, build component
                final boolean[] inAny = {false};
                comps.traverse(new ListVisitor<LinkedList<T>>() {
                    @Override
                    public void visit(LinkedList<T> comp) {
                        if (comp.contains(v)) inAny[0] = true;
                    }
                });
                if (!inAny[0]) {
                    comps.pushBack(bfsComponent(v));
                }
            }
        });
        return comps;
    }
}
