# Proyecto-Deportes-UNAL (Estructuras de Datos)

Proyecto final del curso **Estructuras de Datos**. El objetivo es modelar la conectividad entre estudiantes de la Universidad Nacional a partir de los deportes que practican, para identificar **comunidades deportivas** y verificar si un estudiante está conectado directa o indirectamente con alguien que practique alguno de sus deportes de interés.

## Integrantes
- Egovail Cardozo Juan Daniel
- Romero Villalba Jean Pierre
- Toro Moreno Kevin Andrés 

## Problema
Muchos estudiantes practican uno o varios deportes y pueden estar interesados en explorar nuevas disciplinas, pero no siempre saben a quién acudir. Además, las conexiones relevantes pueden ser indirectas: un estudiante puede estar conectado con alguien que practique un deporte específico a través de estudiantes que comparten deportes en común.

## Funcionalidades (MVP)
- Registrar estudiantes con: **ID**, **nombre**, **deportes practicados** y **deportes de interés**.
- Consultar estudiantes por **ID**.
- Actualizar deportes (agregar/remover) y eliminar estudiantes.
- Agrupar automáticamente estudiantes en **comunidades deportivas** (componentes conexas).
- Verificar si existe conexión directa o indirecta hacia deportes de interés.
- Generar **estadísticas por deporte**: cuántos estudiantes practican cada deporte y listarlos en orden.
- (Opcional) Exportar visualizaciones del grafo/comunidades.

## Modelo (alto nivel)
- **Nodo**: estudiante
- **Arista**: existe entre dos estudiantes si comparten al menos un deporte practicado
- **Comunidad**: componente conexa del grafo
- **Conexión a interés**: existe si desde un estudiante se alcanza a otro que practique un deporte de su interés

## Estructuras de datos (propuesta)
- Listas enlazadas para deportes por estudiante: `LinkedList<DeporteEnum>` (estructura propia).
- Índice por ID: inicia con lista y evoluciona a **AVL**.
- Grafo no dirigido representado con **matriz de adyacencia** (BFS/DFS para comunidades y conectividad).
- Conteo y ranking por deporte con estructura propia (lista de pares + ordenamiento).

## Tecnología y herramientas
- **Java 17**
- **IntelliJ IDEA**
- Estructuras de datos implementadas manualmente (sin frameworks ni gestores de dependencias)

## Estructura del proyecto
```text
Proyecto-Deportes-UNAL/
├─ README.md
├─ .gitignore
├─ src/
│  └─ co/
│     └─ unal/
│        └─ deportesunal/
│           ├─ AppMain.java
│           ├─ domain/
│           │  ├─ Student.java
│           │  └─ SportEnum.java
│           ├─ structures/
│           │  ├─ list/
│           │  │  ├─ Node.java
│           │  │  └─ LinkedList.java
│           │  ├─ tree/
│           │  │  └─ AvlIndex.java
│           │  ├─ graph/
│           │  │  ├─ AdjacencyMatrixGraph.java
│           │  │  └─ Queue.java 
│           ├─ services/
│           │  ├─ StudentService.java
│           │  ├─ CommunityService.java
│           │  ├─ ConnectionService.java
│           │  └─ StatsService.java
│           ├─ persistence/
│           │  ├─ StudentRepository.java
│           │  └─ TxtStudentRepository.java
│           ├─ ui/
│           │  ├─ AppController.java
│           │  └─ MainView.java
│           └─ export/
│              └─ GraphExporter.java
├─ data/
│  ├─ students.txt
│  └─ sports.txt
└─ docs/
   ├─ Entrega1.pdf
   └─ diagramas/
      ├─ diagrama_sistema.png
      └─ flujo_sistema.png
