package co.unal.deportesunal.structure.hash;

import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.listadt.Position;

public class HashTable<K, V> {
    private LinkedList<Entry<K,V>>[] buckets;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        this.capacity = Math.max(4, initialCapacity);
        this.buckets = new LinkedList[this.capacity];
        this.size = 0;
    }

    public HashTable() { this(16); }

    private int indexFor(K key) {
        int h = (key == null) ? 0 : key.hashCode();
        return Math.abs(h) % capacity;
    }

    public boolean containsKey(K key) {
        int idx = indexFor(key);
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        if (bucket == null) return false;
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        return pos != null;
    }

    public V get(K key) {
        int idx = indexFor(key);
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        if (bucket == null) return null;
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        if (pos == null) return null;
        return pos.getValue().getValue();
    }

    public boolean put(K key, V value) {
        int idx = indexFor(key);
        if (buckets[idx] == null) buckets[idx] = new LinkedList<>();
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        if (pos != null) {
            pos.getValue().setValue(value);
            return false;
        }
        bucket.pushBack(new Entry<>(key, value));
        size++;
        if ((double)size / capacity > 0.75) resize();
        return true;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCap = capacity * 2;
        LinkedList<Entry<K,V>>[] newBuckets = new LinkedList[newCap];
        // rehash
        for (int i = 0; i < capacity; i++) {
            LinkedList<Entry<K,V>> bucket = buckets[i];
            if (bucket == null) continue;
            bucket.traverse(new ListVisitor<Entry<K,V>>() {
                @Override
                public void visit(Entry<K, V> e) {
                    int idx = Math.abs((e.getKey()==null?0:e.getKey().hashCode())) % newCap;
                    if (newBuckets[idx] == null) newBuckets[idx] = new LinkedList<>();
                    newBuckets[idx].pushBack(e);
                }
            });
        }
        this.buckets = newBuckets;
        this.capacity = newCap;
    }

    public boolean remove(K key) {
        int idx = indexFor(key);
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        if (bucket == null) return false;
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        if (pos == null) return false;
        bucket.erase(pos);
        size--;
        return true;
    }

    public int size() { return size; }

    public LinkedList<V> values() {
        LinkedList<V> out = new LinkedList<>();
        for (int i = 0; i < capacity; i++) {
            LinkedList<Entry<K,V>> bucket = buckets[i];
            if (bucket == null) continue;
            bucket.traverse(new ListVisitor<Entry<K,V>>() {
                @Override
                public void visit(Entry<K, V> e) {
                    out.pushBack(e.getValue());
                }
            });
        }
        return out;
    }

    // Entry class
    public static class Entry<K,V> {
        private final K key;
        private V value;
        public Entry(K key, V value) { this.key = key; this.value = value; }
        public K getKey() { return key; }
        public V getValue() { return value; }
        public void setValue(V v) { this.value = v; }
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Entry)) return false;
            Entry<?,?> e = (Entry<?,?>) o;
            return (key == null ? e.key==null : key.equals(e.key));
        }
    }
}
