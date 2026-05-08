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

## Nota de alcance Entrega 2

Para esta entrega se implementaron manualmente las estructuras obligatorias:

- Arreglos dinámicos
- Listas enlazadas
- Colas
- Pilas
- Árboles BST
- Árboles AVL

No se usan grafos como estructura base funcional en esta entrega.  
El paquete `structure/graphadt` queda únicamente como referencia técnica y no entra en la evaluación principal de la Entrega 2.

Las comunidades deportivas se modelan mediante un grafo implícito: cada estudiante funciona como nodo y dos estudiantes están conectados si comparten al menos un deporte practicado. El recorrido de comunidades se realiza mediante BFS usando una cola implementada manualmente.

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
│               │   ├── graphadt/
│               │   │   ├── AdjacenceMatrixGraph.java
│               │   │   └── Graph.java
│               │   ├── index/
│               │   │   ├── AvlIndex.java
│               │   │   ├── BstIndex.java
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
│               │   └── StudentServiceSmokeTest.java
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
El proyecto incluye una suite de benchmarks para comparar diferentes implementaciones de StudentIndex.

Implementaciones evaluadas: 
- ListStudentIndex
- BstIndex
- AvlIndex

Operaciones evaluadas: 
- PUT: inserción de estudiantes. 
- FIND: búsqueda de estudiantes por ID.
- DELETE: eliminación de estudiantes por ID. 

Los benchmarks usan datos mock generados aleatoriamente y no modifican el archivo principal students.txt.

### Menú de benchmarks

Desde la aplicación: 
```Bash 
# id;name;PRACTICE(comma);INTEREST(comma)
--- Benchmarks ---
1) Ejecutar TODOS los benchmarks (configuración completa)
2) Ejecutar TODOS los benchmarks (configuración rápida)
3) Ejecutar solo PUT
4) Ejecutar solo GET
5) Ejecutar solo REMOVE
6) Ejecutar por estructura específica
0) Volver
```

### Configuración rápida: 
```Bash 
10^3
10^4
```
Esta configuración es útil para comparar rápidamente LIST, BST y AVL.

### Configuración completa 
La configuración completa se usa para resultados más representativos.

Suele incluir:
```Bash 
10^4
10^5
10^6
```
En la práctica, se recomienda correr la comparación completa principalmente entre BST y AVL, ya que ListStudentIndex puede volverse demasiado lento para tamaños grandes.

### Archivos de resultados
Los resultados se guardan en: 
```Bash 
data/results/
```

Ejemplo de los archivos: 
```Bash 
data/results/index_benchmark_quick.csv
data/results/index_benchmark_full.csv
data/results/index_benchmark_put.csv
data/results/index_benchmark_get.csv
data/results/index_benchmark_remove.csv
data/results/index_benchmark_avl_vs_bst_fullbenchmark.csv
```

Cada fila del CSV tiene el formato: 
```Bash 
structure,operation,n,trial,seed,count,time_ns
```

Donde: 
- **structure:** estructura evaluada (LIST, AVL, BST)
- **operation:** operación medida (PUT, GET, REMOVE)
- **n:** tamaño de entrada
- **trial:** número de  repetición
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
python3 src/co/unal/deportesunal/benchmark/scripts/plot_benchmarks.py data/results/index_benchmark_quick.csv
```

O para el benchmark completo AVL vs BST:
```Bash 
python3 src/co/unal/deportesunal/benchmark/scripts/plot_benchmarks.py data/results/index_benchmark_avl_vs_bst_fullbenchmark.csv
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
Para benchmarks grandes, se recomienda ejecutar desde terminal y aumentar la memoria de la JVM:
```Bash 
java -Xms2g -Xmx4g -cp out co.unal.deportesunal.AppMain
```

Si la prueba incluye ListStudentIndex con tamaños grandes, puede tardar mucho. Por eso se recomienda:
- Usar LIST solo en benchmark rápido.
- Usar BST y AVL para benchmark completo.
- Ejecutar operaciones específicas si la suite tarda demasiado. 

## Estructuras implementadas

### Listas
Usadas para: 
- Almacenar deportes, prácticas e interés. 
- Retornar colecciones de estudiantes.
- representar comunidade.
- Implementar ListIndex.

### DynamicArray
Usado como base para: 
- ArrayStack
- ArrayQueue

### Stack

Usado como apoyo en recorridos iterativos de árboles.

### Queue 

Usada en BFS para construir comunidades deportivas.

### BST 

Usdado como implementación de índice mediante BstIndex

### AVL

Usado como inmplementación de índice mediante AvlIndex. 

El sistema usa la interfaz StudentIndez, lo que permite cambiar entre distintas implementaciones sin modificar StudentService.

## Consideraciones importantes

- No se usaron librerías externas o de Java para las estructutas de datos. 
- Las estructuras de datos fueron implementadas manualmente. 
- El archivo students.txt corresponde al CRUD interactivo. 
- Los benchmark usan datos mock separados.
- La implementación de grafos queda fuera del alcance principal de esta entrega.

## Estado actual

El proyecto cuenta con: 
- CRUD funcional por consola.
- Persistencia en archivos de ttexto.
- Índices intercambiables mediante StudentIndex.
- Implementaciones con listas, BST, AVL. 
- Comunidades deportivas mediante BFS.
- Estadísticas de deportes.
- Benchmarks configurables.
- Exportación de resultados a CSV.
- Generación de grpaficas con Python. 



