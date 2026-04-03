# LinkedList y Node

## Que se implemento
Se implemento una lista doblemente enlazada generica con punteros a cabeza y cola.
Tambien se implemento un nodo explicito `Node<T>` que actua como `Position<T>`.

Archivos principales:
- `LinkedList.java`
- `Node.java`
- `listAdt.java`
- `Position.java`
- `ListVisitor.java`

## Idea de funcionamiento
La lista mantiene:
- `head`: primer nodo.
- `tail`: ultimo nodo.
- `size`: numero de elementos.

Cada nodo `Node<T>` guarda:
- `value`
- `next`
- `prev`

Con puntero a cola, las operaciones al final se hacen en tiempo constante.

Ademas, `LinkedList` expone un recorrido interno con `traverse(ListVisitor<T> visitor)` para procesar la lista nodo a nodo sin depender de indices ni de `java.util`.

## Operaciones principales
- Insercion: `pushFront`, `pushBack`, `addBefore`, `addAfter`.
- Eliminacion: `popFront`, `popBack`, `erase` y `remove(value)`.
- Consulta: `topFront`, `topBack`, `find`, `contains`, `size`, `isEmpty`.
- Recorrido: `traverse(visitor)` para visitas secuenciales internas.

## Complejidad esperada
- `pushFront`, `pushBack`, `popFront`, `popBack`, `topFront`, `topBack`: O(1).
- `find`, `contains`: O(n).
- `addBefore`, `addAfter`, `erase`: O(1) si ya se tiene la posicion; O(n) si primero se debe buscar.
- `remove(value)`: O(n), porque primero busca el valor con `find` y luego reutiliza `erase`.
- `traverse(visitor)`: O(n) para recorrer toda la lista.

## Observaciones
- Se manejan casos borde: lista vacia, un solo elemento, cabeza y cola.
- `remove(value)` retorna `true` si elimino el elemento y `false` si no existe.
- `traverse(visitor)` permite examinar valores internos sin usar librerias de colecciones.
