# DinamicArray

## Que se implemento
Se implemento un arreglo dinamico generico `DinamicArray<T>` sin usar librerias de estructuras prehechas.

Archivo principal:
- `DinamicArray.java`

## Idea de funcionamiento
La estructura mantiene un arreglo interno de `Object[]` y un contador `size`.
Cuando no hay capacidad suficiente, el arreglo se redimensiona duplicando su tamano.

Campos clave:
- `elements`: arreglo interno de almacenamiento.
- `size`: cantidad actual de elementos.

## Operaciones principales
- `add(value)`: inserta al final.
- `add(index, value)`: inserta en posicion desplazando elementos.
- `get(index)`: retorna elemento en posicion.
- `set(index, value)`: reemplaza valor y retorna el anterior.
- `remove(index)`: elimina por indice y compacta el arreglo.
- `contains(value)`, `indexOf(value)`, `size()`, `isEmpty()`, `clear()`.

## Complejidad esperada
- Acceso por indice (`get`, `set`): O(1).
- Insercion al final (amortizado): O(1).
- Insercion o eliminacion en posicion intermedia: O(n).
- Busqueda (`contains`, `indexOf`): O(n).

## Observaciones
- Se validan indices para evitar accesos invalidos.
