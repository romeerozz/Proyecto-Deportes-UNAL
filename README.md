# Proyecto-Deportes-UNAL

Proyecto final de la asignatura **Estructuras de Datos**.

## Integrantes

- Jean Romero
- Daniel Egoavil
- Kevin Toro

## Descripción del proyecto

**Proyecto-Deportes-UNAL** es un prototipo funcional por consola para gestionar estudiantes de la Universidad Nacional y analizar sus relaciones deportivas.

El sistema permite registrar estudiantes con:

- ID
- Nombre
- Deportes que practica
- Deportes de interés

A partir de esta información, la aplicación permite:

- Registrar, consultar, actualizar y eliminar estudiantes.
- Listar estudiantes registrados.
- Agrupar estudiantes en comunidades deportivas.
- Verificar si un estudiante tiene conexión directa o indirecta con alguien que practica un deporte de su interés.
- Consultar practicantes dentro de una comunidad.
- Obtener estadísticas generales de deportes.
- Ejecutar benchmarks sobre diferentes estructuras de índice.

## Estructuras de datos

Todas las estructuras de datos están implementadas manualmente en el paquete `structure/`. No se usa `java.util` para las estructuras base.

### Estructuras implementadas

| Categoría | Estructura | Ubicación |
|---|---|---|
| Arreglos | DinamicArray | `structure/array/` |
| Listas | LinkedList (con Node, Position, ListVisitor) | `structure/listadt/` |
| Colas | ArrayQueue | `structure/queue/` |
| Pilas | ArrayStack | `structure/stackadt/` |
| Árbol BST | BstTree (BstIndex) | `structure/tree/`, `structure/index/` |
| Árbol AVL | AvlTree (AvlIndex) | `structure/tree/`, `structure/index/` |
| Hash | HashTable (HashStudentIndex) | `structure/hash/`, `structure/index/` |
| Grafos | AdjacencyListGraph, AdjacenceMatrixGraph | `structure/graphadt/` |
| Heap | MaxHeap, MaxHeapSportCount | `structure/heap/` |
| Union-Find | UnionFind (con HashTable interna) | `structure/disjointset/` |

### Índices de estudiantes

Todos los índices implementan la interfaz `StudentIndex` y son intercambiables:

- **ListIndex** — basado en `LinkedList`
- **BstIndex** — basado en `BstTree` (BST)
- **AvlIndex** — basado en `AvlTree` (AVL con rebalanceo)
- **HashStudentIndex** — basado en `HashTable`

### Grafos

Los grafos se usan exclusivamente para benchmarks. El grafo se construye con conexión en cadena (`O(k)`): los estudiantes se agrupan por deporte y cada grupo se conecta secuencialmente como una lista enlazada, evitando la conexión completa `O(k²)`. `GraphBenchmarkRunner` usa únicamente `LinkedList` y `HashTable` (sin `ArrayList` ni `EnumMap`).

### Union-Find

`UnionFind` usa `HashTable` (implementación propia) para `parent` y `rank`, en lugar de `java.util.HashMap`.

### Comunidades deportivas

Las comunidades se modelan mediante BFS sobre un grafo implícito: cada estudiante funciona como nodo y dos están conectados si comparten al menos un deporte practicado. El recorrido usa `ArrayQueue`.

## Lenguaje y entorno

- Lenguaje: Java
- Versión recomendada: Java 21
- Sistema de construcción: compilación directa con `javac` o ejecución desde IntelliJ IDEA
- Scripts de análisis: Python 3
- Librerías Python usadas:
    - `csv`
    - `os`
    - `sys`
    - `collections`
    - `matplotlib`

## Estructura actual del proyecto

```text
Proyecto-Deportes-UNAL/
├── data/
│   ├── persistence/
│   │   └── students.txt
│   ├── mock/
│   │   └── ...
│   ├── results/
│   │   ├── benchmark_full.csv
│   │   ├── index_benchmark_full.csv
│   │   ├── index_benchmark_quick.csv
│   │   ├── graph_benchmark_full.csv
│   │   └── ...
│   └── graphics/
│       └── ...
├── src/
│   └── co/
│       └── unal/
│           └── deportesunal/
│               ├── AppMain.java
│               ├── benchmark/
│               │   ├── factories/
│               │   │   ├── AvlIndexFactory.java
│               │   │   ├── BstIndexFactory.java
│               │   │   ├── HashIndexFactory.java
│               │   │   ├── IndexFactory.java
│               │   │   └── ListIndexFactory.java
│               │   ├── scripts/
│               │   │   └── plot_benchmarks.py
│               │   ├── utils/
│               │   │   ├── CsvWriter.java
│               │   │   ├── MockDataGenerator.java
│               │   │   ├── SimpleCsvWriter.java
│               │   │   └── Timer.java
│               │   ├── BenchmarkConfig.java
│               │   ├── BenchmarkOperation.java
│               │   ├── BenchmarkRunner.java
│               │   ├── GraphBenchmarkRunner.java
│               │   └── IndexBenchmark.java
│               ├── controller/
│               │   └── AppController.java
│               ├── domain/
│               │   ├── SportCount.java
│               │   ├── SportEnum.java
│               │   ├── Student.java
│               │   └── exception/
│               │       ├── DataAccessException.java
│               │       ├── DuplicatedIdException.java
│               │       └── NotFoundException.java
│               ├── persistence/
│               │   ├── FileConstant.java
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
│               │   ├── disjointset/
│               │   │   └── UnionFind.java
│               │   ├── graphadt/
│               │   │   ├── AdjacenceMatrixGraph.java
│               │   │   ├── AdjacencyListGraph.java
│               │   │   └── Graph.java
│               │   ├── hash/
│               │   │   └── HashTable.java
│               │   ├── heap/
│               │   │   ├── Comparator.java
│               │   │   ├── MaxHeap.java
│               │   │   └── MaxHeapSportCount.java
│               │   ├── index/
│               │   │   ├── AvlIndex.java
│               │   │   ├── BstIndex.java
│               │   │   ├── HashStudentIndex.java
│               │   │   ├── ListIndex.java
│               │   │   └── StudentIndex.java
│               │   ├── listadt/
│               │   │   ├── LinkedList.java
│               │   │   ├── ListAdt.java
│               │   │   ├── ListVisitor.java
│               │   │   ├── Node.java
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
│               │       ├── AvlTree.java
│               │       ├── BstTree.java
│               │       └── Tree.java
│               ├── test/
│               │   ├── AdjacencyListGraphTest.java
│               │   ├── HashStudentIndexTest.java
│               │   ├── HashTableTest.java
│               │   ├── MaxHeapTest.java
│               │   ├── SmokeTestsRunner.java
│               │   └── StudentServiceSmokeTest.java
│               ├── tools/
│               │   ├── QuickGraphBenchmark.java
│               │   └── QuickGraphBenchmarkSmall.java
│               ├── ui/
│               │   ├── ConsoleUi.java
│               │   └── MainWindow.java
│               └── util/
│                   ├── GraphExporter.java
│                   ├── Helper.java
│                   └── Validator.java
└── README.md
```

## Clonar el repositorio

```Bash 
git clone https://github.com/romeerozz/Proyecto-Deportes-UNAL.git
cd Proyecto-Deportes-UNAL
```

### Ejecutar el proyecto desde IntelliJIDEA
1. Abrir IntelliJIDEA.
2. Sepeccionar Open .
3. Elejir la carpeta del proyecto Proyecto-Deportes-UNAL.
4. Esperar a que IntelliJ indexe el proyecto
5. Abrir la clase: 
    ```Bash 
    src/co/unal/deportesunal/AppMain.java
    ```
6. Ejecutar el método main. 

### Ejecutar el proyecto desde la terminal 

Desde la raíz del proyecto: 

```Bash 
javac -d out $(find src -name "*.java")
```
Luego ejecutar: 

```Bash 
java -cp out co.unal.deportesunal.AppMain
```
Si se desea asignar má memoria para pruebas grandes: 

```Bash 
java -Xms2g -Xmx4g -cp out co.unal.deportesunal.AppMain
```
## Uso general de la aplicación. 

### Menú principal

Al ejecutar el programa aparecerá el menú principal: 
```Bash 
--- Menú principal ---
1) Modo interactivo (CRUD)
2) Análisis de comunidades deportivas
3) Análisis de conexiones
4) Análisis de estadísticas
5) Ejecutar benchmarks
6) Recargar desde el archivo
7) Guardar a archivo
0) Salir
```

### Modo interactivo CRUD

Permite trabajar manualmente con estudiantes: 
```Bash 
1) Registrar estudiante
2) Consultar por ID
3) Actualizar deportes
4) Eliminar estudiante
5) Listar estudiantes
0) Volver
```

Desde este modo se pueden agregar estudiantes, consultar estudiantes existentes, actualizar deportes practicados o de interés, 
eliminar estudiantes y listar la información actual.

Los datos del modo interactivo se guardarán en: 
```Bash 
data/persistence/students.txt
```
Este archivo es independiente de los datos usados por los benchmark. 

### Persistencia
El sistema usa archivos de texto para guardar y cargar estudiantes.

Archivo principal del CRUD:
```Bash 
data/persistence/students.txt
```

Formato esperado para el archivo de texto:
```Bash 
# id;name;PRACTICE(comma);INTEREST(comma)
1;Ana;FUTBOL;NATACION
2;Luis;BALONCESTO;FUTBOL
```
El sistema carga automáticamente este archivo al iniciar. También se puede recargar o guardar manualmente desde el menú principal.

### Benchmarks
El proyecto incluye una suite de benchmarks para comparar diferentes implementaciones de índices y estructuras.

Estructuras evaluadas:
- **LIST** — ListIndex (basado en LinkedList)
- **BST** — BstIndex (basado en BstTree)
- **AVL** — AvlIndex (basado en AvlTree)
- **HASH** — HashStudentIndex (basado en HashTable)
- **GRAPH** — AdjacencyListGraph (benchmark de grafos con conexión en cadena)
- **UF** — UnionFind (basado en HashTable)

Operaciones evaluadas:
- **PUT**: inserción de datos
- **GET**: búsqueda/consulta de datos por ID
- **REMOVE**: eliminación de datos por ID

Todas las estructuras se evalúan en una misma ejecución y los resultados se consolidan en un único archivo CSV.

### Configuración rápida

```text
10, 100, 1000, 10000
```

Ideal para verificar comportamiento y comparar rápidamente todas las estructuras.

### Configuración completa

```text
100000, 1000000, 10000000, 100000000
```

Usada para resultados representativos. Se recomienda aumentar la memoria JVM para tamaños grandes.

### Protocolo de medición

1. **Warmup**: 1 iteración de calentamiento JVM con 10 000 elementos (datos descartados).
2. **Trials**: 3 repeticiones por cada tamaño con semillas diferentes por trial (`seed`, `seed+1`, `seed+2`).
3. **Semillas**: cada trial usa semillas distintas para generación de datos, IDs de consulta y IDs de eliminación.

### Archivos de resultados
Los resultados se guardan en:

```text
data/results/
```

Archivo unificado (contiene LIST, BST, AVL, HASH, GRAPH, UF):

```text
data/results/benchmark_full.csv
```

Archivos adicionales para ejecuciones específicas:

```text
data/results/index_benchmark_quick.csv
data/results/index_benchmark_full.csv
data/results/graph_benchmark_full.csv
```

Cada fila del CSV tiene el formato:

```text
structure,operation,n,trial,seed,count,time_ns
```

Donde:
- **structure:** estructura evaluada (LIST, BST, AVL, HASH, GRAPH, UF)
- **operation:** operación medida (PUT, GET, REMOVE)
- **n:** tamaño de entrada
- **trial:** número de repetición
- **seed:** semilla usada
- **count:** cantidad de operaciones ejecutadas
- **time_ns:** tiempo total en nanosegundos

### Generar gráficas con python

Primero ubicarse en la raíz:
```Bash 
cd Proyecto-Deportes-UNAL
```

Luego ejecutar: 
```Bash 
python3 src/co/unal/deportesunal/benchmark/scripts/plot_benchmarks.py data/results/benchmark_full.csv
```

O para un archivo específico:
```Bash 
python3 src/co/unal/deportesunal/benchmark/scripts/plot_benchmarks.py data/results/index_benchmark_quick.csv
```

### Carpeta de salida de gráficas

Las imágenes generadas se guardan automáticamente en: 
```Bash 
data/graphics/
```

Ejemplos: 
```Bash 
data/graphics/put_benchmark.png
data/graphics/get_benchmark.png
data/graphics/remove_benchmark.png
```

El script calcula la métrica: 
```Bash 
ns/op = time_ns / count
```

Esto permite comparar mejor los resultados porque no todas las operaciones tienen la misma cantidad de llamadas. Por ejemplo, GET se ejecuta n veces, mientras que REMOVE se ejecuta n/10 veces.

### Datos mock
Los benchmarks pueden generar y persistir datos mock en:
```Bash 
data/mock/
```

Estos archivos sirven para reproducibilidad y pruebas, pero no hacen parte del CRUD interactivo.

El CRUD interactivo usa:
```Bash 
data/persistence/students.txt
```
Los benchmarks usan datos generados automáticamente y archivos separados.

### Recomendaciones de la ejecución

Para probar la aplicación normalmente:
```Bash 
java -cp out co.unal.deportesunal.AppMain
```

Para iniciar en modo consola (sin GUI):
```Bash
java -cp out co.unal.deportesunal.AppMain cli
```

Para benchmarks grandes, se recomienda ejecutar desde terminal y aumentar la memoria de la JVM:
```Bash 
java -Xms2g -Xmx4g -cp out co.unal.deportesunal.AppMain
```

Si la prueba incluye LIST con tamaños grandes, puede tardar mucho. Por eso se recomienda:
- Usar LIST solo en benchmark rápido.
- Usar BST, AVL, HASH para benchmark completo.
- Ejecutar operaciones específicas si la suite tarda demasiado. 

## Estado actual

El proyecto cuenta con: 
- CRUD funcional por consola e interfaz gráfica (Swing con Nimbus L&F).
- Persistencia en archivos de texto.
- Índices intercambiables mediante StudentIndex (LIST, BST, AVL, HASH).
- Grafos con conexión en cadena para benchmarks.
- Union-Find con HashTable interna para benchmarks.
- Comunidades deportivas mediante BFS.
- Estadísticas de deportes.
- Benchmarks configurables con warmup, trials y semillas diferenciadas.
- Exportación de resultados a CSV unificado.
- Generación de gráficas con Python. 



