# Proyecto-Deportes-UNAL


Integrantes: Jean Romero - Daniel Egoavil - Kevin Moreno

## Estructura Actual del Proyecto

Nota de alcance Entrega 2:
- Estructuras obligatorias: arreglos, listas, colas, pilas, arboles (BST/AVL).
- No se usaran grafos como base funcional en esta entrega.
- El paquete `structure/graphadt` queda solo como referencia tecnica y no entra en evaluacion de Entrega 2.

```text
Proyecto-Deportes-UNAL/
├── data/
│   ├── deportes.txt
│   ├── estudiantes.txt
│   ├── sports.txt
│   └── stuents.txt
├── src/
│   └── co/
│       └── unal/
│           └── deportesunal/
│               ├── AppMain.java
│               ├── README.md
│               ├── benchmark/
│               │   ├── BenchmarkRunner.java
│               │   ├── MockDataGenerator.java
│               │   └── Timer.java
│               ├── controller/
│               │   └── AppController.java
│               ├── domain/
│               │   ├── SportsCount.java
│               │   ├── SportsEnum.java
│               │   ├── Student.java
│               │   └── exception/
│               │       ├── DuplicatedIdException.java
│               │       └── NotFoundException.java
│               ├── persistence/
│               │   ├── StudentRepository.java
│               │   └── TxtStudentRepository.java
│               ├── service/
│               │   ├── CommunityService.java
│               │   ├── ConnectionService.java
│               │   ├── StatsService.java
│               │   └── StudentService.java
│               ├── structure/
│               │   ├── array/
│               │   │   └── DinamicArray.java
│               │   ├── graphadt/
│               │   │   ├── AdjacenceMatrixGraph.java
│               │   │   └── Graph.java
│               │   ├── listadt/
│               │   │   ├── LinkedList.java
│               │   │   ├── Node.java
│               │   │   ├── listAdt.java
│               │   │   └── Position.java
│               │   ├── queue/
│               │   │   ├── ArrayQueue.java
│               │   │   └── Queue.java
│               │   ├── stackadt/
│               │   │   ├── ArrayStack.java
│               │   │   └── Stack.java
│               │   ├── tests/
│               │   │   └── StructureSanityTests.java
│               │   └── tree/
│               │       ├── AvlIndex.java
│               │       ├── BstIndex.java
│               │       └── StudentIndex.java
│               ├── ui/
│               │   ├── ConsoleUi.java
│               │   └── MainWindow.java
│               └── util/
│                   ├── GraphExporter.java
│                   ├── Helper.java
│                   └── Validator.java
└── README.md
```