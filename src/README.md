# Proyecto-Deportes-UNAL — Código fuente

## Estructuras implementadas (todas manuales, sin java.util)

### `structure/`

| Paquete | Clases |
|---|---|
| `array/` | `DinamicArray` |
| `disjointset/` | `UnionFind` (usa `HashTable` propia) |
| `graphadt/` | `Graph`, `AdjacencyListGraph`, `AdjacenceMatrixGraph` |
| `hash/` | `HashTable` |
| `heap/` | `MaxHeap`, `MaxHeapSportCount`, `Comparator` |
| `index/` | `StudentIndex` (interfaz), `ListIndex`, `BstIndex`, `AvlIndex`, `HashStudentIndex` |
| `listadt/` | `ListAdt`, `LinkedList`, `Node`, `Position`, `ListVisitor` |
| `queue/` | `Queue`, `ArrayQueue` |
| `stackadt/` | `Stack`, `ArrayStack` |
| `tree/` | `Tree`, `BstTree`, `AvlTree` |
| `tests/` | `StructureSanityTests` |

### `benchmark/`

- `BenchmarkConfig` — configuraciones: `defaultConfig()` (100K, 1M, 10M, 100M) y `quickConfig()` (10, 100, 1K, 10K)
- `BenchmarkRunner` — orquesta índices + grafos + UF en un solo CSV
- `GraphBenchmarkRunner` — benchmarks de grafos con `LinkedList` y `HashTable`
- `IndexBenchmark` — PUT/GET/REMOVE sobre `StudentIndex`
- `factories/` — `IndexFactory`, `ListIndexFactory`, `BstIndexFactory`, `AvlIndexFactory`, `HashIndexFactory`
- `scripts/plot_benchmarks.py` — genera gráficas desde CSV
- `utils/` — `Timer`, `MockDataGenerator`, `CsvWriter`, `SimpleCsvWriter`

### `ui/`

- `ConsoleUi` — interfaz por terminal
- `MainWindow` — interfaz gráfica Swing con Nimbus Look and Feel

### `test/`

- `StudentServiceSmokeTest`
- `SmokeTestsRunner`
- `HashTableTest`
- `HashStudentIndexTest`
- `AdjacencyListGraphTest`
- `MaxHeapTest`

### `tools/`

- `QuickGraphBenchmark` — ejecución rápida de benchmarks de grafos
- `QuickGraphBenchmarkSmall` — versión con tamaños pequeños

## Protocolo de benchmarks

- **Warmup**: 1 iteración de calentamiento (10 000 elementos, datos descartados)
- **Trials**: 3 repeticiones, cada una con semilla diferente (`seed`, `seed+1`, `seed+2`)
- **Semillas**: cada trial usa semillas independientes para generar datos, IDs de consulta y IDs de eliminación
- **CSV unificado**: todas las estructuras (LIST, BST, AVL, HASH, GRAPH, UF) se escriben en el mismo archivo

## Compilación

```bash
javac -d out $(find src -name "*.java")
java -cp out co.unal.deportesunal.AppMain
java -cp out co.unal.deportesunal.AppMain cli    # modo consola
```
