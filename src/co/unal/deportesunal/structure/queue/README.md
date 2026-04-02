# Queue y ArrayQueue

## Que se implemento
Se implemento la interfaz `Queue<T>` y su implementacion `ArrayQueue<T>`.

Archivos principales:
- `Queue.java`
- `ArrayQueue.java`

## Idea de funcionamiento
La cola sigue politica FIFO (First In, First Out).
`ArrayQueue` usa un buffer circular con:
- `head`: posicion de salida.
- `tail`: posicion de insercion.
- `size`: elementos actuales.

Cuando se llena, el arreglo interno crece y reordena los elementos desde `head`.

## Operaciones principales
- `enqueue(value)`: encolar al final.
- `dequeue()`: desencolar al frente.
- `peek()`: consultar frente.
- `isEmpty()`, `size()`.

## Complejidad esperada
- `enqueue`: O(1) amortizado.
- `dequeue`: O(1).
- `peek`: O(1).
- `isEmpty`, `size`: O(1).

## Observaciones
- `dequeue` y `peek` lanzan excepcion si la cola esta vacia.
