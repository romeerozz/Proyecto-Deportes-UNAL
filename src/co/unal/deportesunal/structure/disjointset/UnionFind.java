package co.unal.deportesunal.structure.disjointset;

import java.util.HashMap;

public class UnionFind {

    private final HashMap<Integer, Integer> parent;
    private final HashMap<Integer, Integer> rank;

    public UnionFind() {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    public void makeSet(int x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            rank.put(x, 0);
        }
    }

    public int find(int x) {
        int p = parent.get(x);

        if (p != x) {
            parent.put(x, find(p));
        }

        return parent.get(x);
    }

    public void union(int a, int b) {

        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return;
        }

        int rankA = rank.get(rootA);
        int rankB = rank.get(rootB);

        if (rankA < rankB) {
            parent.put(rootA, rootB);
        } else if (rankA > rankB) {
            parent.put(rootB, rootA);
        } else {
            parent.put(rootB, rootA);
            rank.put(rootA, rankA + 1);
        }
    }
}