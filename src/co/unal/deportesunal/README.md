# Entrega 2 - Division de Trabajo

## Alcance Academico (Oficial)

Estructuras obligatorias para esta entrega:
- Arreglos
- Listas
- Colas
- Pilas
- Arboles (BST, AVL)

Restriccion:
- No se usan grafos como base funcional en Entrega 2.
- No se permite usar librerias con estructuras ya implementadas.
- Las estructuras deben implementarse manualmente.

## Persona 1 - EDD base

Implementa:
- structure.array.DinamicArray
- structure.listadt.LinkedList y structure.listadt.Node
- structure.stackadt.Stack y structure.stackadt.ArrayStack
- structure.queue.Queue y structure.queue.ArrayQueue

## Persona 2 - Arboles (BST/AVL)

Implementa:
- structure.tree.StudentIndex
- structure.tree.AvlIndex (obligatorio)
- structure.tree.BstIndex (recomendado)

Checklist obligatorio:
1. Contratos de error en StudentIndex:
- get(id) lanza NotFoundException si no existe.
- put(id, student) lanza DuplicatedIdException si el ID ya existe.
2. AvlIndex completo:
- put/get/remove con rebalanceo LL/RR/LR/RL.
- valuesInOrder() ordenado por ID.
- size() consistente.
3. BstIndex recomendado:
- mismo contrato de StudentIndex.
4. Pruebas minimas:
- rotaciones LL/RR/LR/RL en AVL.
- remove de hoja, 1 hijo y 2 hijos.
- verificacion in-order.

## Kevin - Dominio + Servicios + Controlador + Persistencia + Benchmark

Implementa:
- domain/* (Student, SportsEnum, SportsCount, excepciones)
- persistence/* (StudentRepository, TxtStudentRepository)
- service/* (StudentService, CommunityService, ConnectionService, StatsService)
- controller/AppController
- ui/ConsoleUi
- benchmark/* (MockDataGenerator, BenchmarkRunner, Timer)
- AppMain (inyeccion de dependencias y ejecucion)

## Checklist de Integracion

1. Compila y ejecuta AppMain desde repositorio clonado.
2. CRUD completo de estudiantes.
3. Consultas de comunidad sin grafo (segun reglas del enunciado).
4. Verificacion de conexiones sin usar estructura de grafo.
5. Estadisticas y ranking por deporte.
6. Benchmark con salida CSV/TXT en docs/results/.
