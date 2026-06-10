package co.unal.deportesunal.structure.disjointset;

import co.unal.deportesunal.structure.hash.HashTable;

/**
 * Estructura de datos Union-Find (conjuntos disjuntos) con compresión de ruta
 * y unión por rango. Permite agrupar elementos en conjuntos disjuntos y
 * consultar si dos elementos pertenecen al mismo conjunto.
 */
public class UnionFind {

    private final HashTable<Integer, Integer> parent;
    private final HashTable<Integer, Integer> rank;

    /**
     * Crea una nueva instancia de Union-Find vacía.
     */
    public UnionFind() {
        parent = new HashTable<>();
        rank = new HashTable<>();
    }

    /**
     * Crea un nuevo conjunto que contiene únicamente el elemento {@code x}.
     * Si el elemento ya existe, no hace nada.
     *
     * @param x elemento a agregar como nuevo conjunto
     */
    public void makeSet(int x) {
        if (!parent.containsKey(x)) {
            parent.put(x, x);
            rank.put(x, 0);
        }
    }

    /**
     * Encuentra el representante (raíz) del conjunto al que pertenece
     * {@code x}. Aplica compresión de ruta para aplanar el árbol.
     *
     * @param x elemento a consultar
     * @return raíz del conjunto de {@code x}
     */
    public int find(int x) {
        int p = parent.get(x);

        if (p != x) {
            parent.put(x, find(p));
        }

        return parent.get(x);
    }

    /**
     * Une los conjuntos que contienen a {@code a} y {@code b}. Si ya
     * pertenecen al mismo conjunto, no hace nada.
     *
     * @param a primer elemento
     * @param b segundo elemento
     */
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