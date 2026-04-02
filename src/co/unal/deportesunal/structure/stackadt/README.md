# Stack y ArrayStack

## Que se implemento
Se implemento la interfaz `Stack<T>` y su implementacion `ArrayStack<T>`.

Archivos principales:
- `Stack.java`
- `ArrayStack.java`

## Idea de funcionamiento
La pila sigue politica LIFO (Last In, First Out).
`ArrayStack` se apoya en `DinamicArray<T>` como almacenamiento interno.

## Operaciones principales
- `push(value)`: inserta en el tope.
- `pop()`: elimina y retorna el tope.
- `peek()`: consulta el tope sin eliminar.
- `isEmpty()`, `size()`.

## Complejidad esperada
- `push`: O(1) amortizado.
- `pop`: O(1).
- `peek`: O(1).
- `isEmpty`, `size`: O(1).

## Observaciones
- `pop` y `peek` lanzan excepcion si la pila esta vacia.
