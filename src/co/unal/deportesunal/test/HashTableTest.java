package co.unal.deportesunal.test;

import co.unal.deportesunal.structure.hash.HashTable;

public class HashTableTest {
    public static boolean run() {
        HashTable<Integer, String> ht = new HashTable<>();
        boolean ok = true;
        ok &= ht.put(1, "a");
        ok &= ht.put(2, "b");
        ok &= "a".equals(ht.get(1));
        ok &= ht.containsKey(2);
        ok &= !ht.put(1, "aa"); // replace returns false
        ok &= "aa".equals(ht.get(1));
        ok &= ht.remove(2);
        ok &= ht.get(2) == null;
        ok &= ht.size() == 1;
        return ok;
    }

    public static void main(String[] args) {
        boolean ok = run();
        System.out.println("HashTableTest: " + (ok ? "PASS" : "FAIL"));
        if (!ok) System.exit(1);
    }
}
