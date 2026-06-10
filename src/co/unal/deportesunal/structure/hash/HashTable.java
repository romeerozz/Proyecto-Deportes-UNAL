package co.unal.deportesunal.structure.hash;

import co.unal.deportesunal.structure.listadt.LinkedList;
import co.unal.deportesunal.structure.listadt.ListVisitor;
import co.unal.deportesunal.structure.listadt.Position;

/**
 * Tabla hash genérica con manejo de colisiones mediante encadenamiento
 * separado (listas enlazadas). Soporta redimensionamiento automático
 * cuando el factor de carga supera 0.75.
 *
 * @param <K> tipo de las llaves
 * @param <V> tipo de los valores
 */
public class HashTable<K, V> {
    private LinkedList<Entry<K,V>>[] buckets;
    private int capacity;
    private int size;

    /**
     * Crea una tabla hash con la capacidad inicial especificada (mínimo 4).
     *
     * @param initialCapacity capacidad inicial deseada
     */
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        this.capacity = Math.max(4, initialCapacity);
        this.buckets = new LinkedList[this.capacity];
        this.size = 0;
    }

    /**
     * Crea una tabla hash con capacidad inicial por defecto de 16.
     */
    public HashTable() { this(16); }

    /**
     * Calcula el índice del cubo (bucket) para una llave dada.
     *
     * @param key llave a indexar
     * @return índice en el arreglo de cubos
     */
    private int indexFor(K key) {
        int h = (key == null) ? 0 : key.hashCode();
        return Math.abs(h) % capacity;
    }

    /**
     * Verifica si la tabla contiene una llave determinada.
     *
     * @param key llave a buscar
     * @return {@code true} si la llave existe
     */
    public boolean containsKey(K key) {
        int idx = indexFor(key);
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        if (bucket == null) return false;
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        return pos != null;
    }

    /**
     * Obtiene el valor asociado a una llave.
     *
     * @param key llave a consultar
     * @return valor asociado, o {@code null} si la llave no existe
     */
    public V get(K key) {
        int idx = indexFor(key);
        LinkedList<Entry<K,V>> bucket = buckets[idx];
        if (bucket == null) return null;
        Position<Entry<K,V>> pos = bucket.find(new Entry<>(key, null));
        if (pos == null) return null;
        return pos.getValue().getValue();
    }

    /**
     * Inserta o actualiza una entrada en la tabla. Si la llave ya existe,
     * sobrescribe el valor. Si es nueva, la agrega y redimensiona si es
     * necesario.
     *
     * @param key   llave a insertar
     * @param value valor a asociar
     * @return {@code true} si se insertó una nueva entrada, {@code false}
     *         si se actualizó una existente
     */
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

    /**
     * Redimensiona la tabla al doble de su capacidad actual y reubica
     * todas las entradas.
     */
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

    /**
     * Elimina la entrada asociada a una llave de la tabla.
     *
     * @param key llave a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false}
     *         si la llave no existe
     */
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

    /**
     * Retorna la cantidad de entradas en la tabla.
     *
     * @return número de entradas
     */
    public int size() { return size; }

    /**
     * Retorna una lista enlazada con todos los valores almacenados en la
     * tabla, sin un orden definido.
     *
     * @return lista con los valores
     */
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

    /**
     * Par clave-valor interno utilizado por la tabla hash.
     *
     * @param <K> tipo de la llave
     * @param <V> tipo del valor
     */
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
