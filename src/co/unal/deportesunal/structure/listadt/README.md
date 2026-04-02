# LinkedList y Node

## Que se implemento
Se implemento una lista doblemente enlazada generica con punteros a cabeza y cola.
Tambien se implemento un nodo explicito `Node<T>` que actua como `Position<T>`.

Archivos principales:
- `LinkedList.java`
- `Node.java`
- `listAdt.java`
- `Position.java`

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

## Operaciones principales
- Insercion: `pushFront`, `pushBack`, `addBefore`, `addAfter`.
- Eliminacion: `popFront`, `popBack`, `erase`.
- Consulta: `topFront`, `topBack`, `find`, `contains`, `size`, `isEmpty`.

## Complejidad esperada
- `pushFront`, `pushBack`, `popFront`, `popBack`, `topFront`, `topBack`: O(1).
- `find`, `contains`: O(n).
- `addBefore`, `addAfter`, `erase`: O(1) si ya se tiene la posicion; O(n) si primero se debe buscar.

## Observaciones
- Se manejan casos borde: lista vacia, un solo elemento, cabeza y cola.
