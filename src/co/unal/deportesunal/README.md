
## Integracion con Persona 2 (Arboles)

Esta integracion ya quedo iniciada para que Persona 2 solo complete la logica interna de AVL/BST.

### Contrato ya definido

Archivo:
- `structure/tree/StudentIndex.java`

Metodos del contrato:
- `put(int id, Student student)`
- `get(int id)`
- `remove(int id)`
- `valuesInOrder()`
- `contains(int id)`
- `size()`

Excepciones esperadas:
- `DuplicatedIdException` en `put` si el ID ya existe.
- `NotFoundException` en `get/remove` si el ID no existe.

### Punto de acople ya listo

Archivo:
- `service/StudentService.java`

`StudentService` ya depende de `StudentIndex`, por lo que no se debe cambiar su firma publica.
Persona 2 solo debe terminar la implementacion en:
- `structure/tree/AvlIndex.java` (obligatorio)
- `structure/tree/BstIndex.java` (opcional/recomendado)

### Flujo de integracion recomendado

1. Persona 2 implementa AVL con el contrato actual (sin cambiar interfaz).
2. Se inyecta `new AvlIndex()` en `StudentService` desde `AppMain` o controlador.
3. Se valida CRUD basico desde `StudentService`.
4. Se valida `valuesInOrder()` para listado ordenado por ID.
5. Se agregan pruebas de rotaciones y eliminacion en AVL.

### Regla importante de esta entrega

- No usar grafo para resolver consultas funcionales.
- Toda consulta por ID y ordenamiento por ID debe salir del indice de arbol (AVL/BST).
