# División de Trabajo - Entrega #2
## Proyecto Deportes UNAL

---

## 📋 Resumen de Miembros

| Miembro | Rol | Responsabilidades |
|---------|-----|-------------------|
| **Miembro 1** | Estructuras & Persistencia | Listas, AVL, Colas y Almacenamiento |
| **Miembro 2** | Servicios & Lógica | Consultas, Comunidades y Estadísticas |
| **Miembro 3** | UI, Testing & Reporte | Interfaz, Testing y Documentación |

---

## 🔧 Miembro 1: Estructuras de Datos & Persistencia
**(Sin asignar)**

### Carpetas y Archivos Asignados:

```
structure/
├── list/
│   ├── LinkedList.java          ← Implementar lista enlazada genérica
│   └── Node.java                ← Nodo para la lista
├── index/
│   └── AvlStudentIndex.java     ← Índice AVL para búsqueda por ID
├── graph/
│   └── Queue.java               ← Cola para BFS (sin grafo aún)
└── SportCounter.java            ← Estructura para contar deportes

domain/
├── Student.java                 ← Modelo de estudiante
├── SportsEnum.java              ← Enum de deportes
└── SportsCount.java             ← Clase para pares (deporte, cantidad)

persistence/
└── StudentRepository.java       ← Cargar/guardar datos
```

### Funcionalidades a Implementar:

- ✅ **LinkedList<T>**: insertar, eliminar, buscar, recorrer
- ✅ **AvlStudentIndex**: árbol AVL balanceado para indexar estudiantes por ID
- ✅ **Queue**: cola para BFS en búsqueda de conexiones
- ✅ **Student**: atributos (ID, nombre, deportes practicados, intereses)
- ✅ **SportsEnum**: enum con todos los deportes disponibles
- ✅ **StudentRepository**: persistencia en archivos (datos/students.txt, datos/sports.txt)

### Commits Esperados (mín. 3):
1. Estructuras base: LinkedList + Node
2. Índices: AvlStudentIndex + Queue
3. Persistencia y modelos: Student, SportsEnum, Repository

---

## 🎯 Miembro 2: Servicios & Lógica de Negocio
**(Sin asignar)**

### Carpetas y Archivos Asignados:

```
services/
├── StudentService.java          ← CRUD de estudiantes
├── CommunityService.java        ← Agrupar por deportes comunes
├── StatsService.java            ← Estadísticas y ranking
└── ConnectionService.java       ← Conectividad directa/indirecta

util/
├── Helper.java                  ← Métodos auxiliares
└── Validator.java               ← Validaciones
```

### Funcionalidades a Implementar:

- ✅ **StudentService**:
  - Registrar estudiante
  - Buscar por ID (usando AVL)
  - Actualizar deportes (agregar/remover)
  - Eliminar estudiante
  - Listar todos

- ✅ **CommunityService**:
  - Agrupar estudiantes que comparten al menos 1 deporte
  - Retornar comunidades como LinkedList de LinkedList

- ✅ **StatsService**:
  - Contar cuántos estudiantes practican cada deporte
  - Ordenar deportes por cantidad (ascendente/descendente)
  - Listar estudiantes por deporte

- ✅ **ConnectionService**:
  - Conectividad directa: ¿comparten deporte?
  - Conectividad indirecta: ¿existe camino usando Cola/BFS?
  - Verificar si estudiante X alcanza a alguien que practique deporte Y

### Commits Esperados (mín. 3):
1. StudentService + CommunityService
2. StatsService + ConnectionService
3. Validaciones y helpers

---

## 🖥️ Miembro 3: UI, Testing & Documentación
**(Sin asignar)**

### Carpetas y Archivos Asignados:

```
ui/
├── AppController.java           ← Controlador de lógica
└── MainWindow.java              ← Interfaz consola (obligatorio)

util/
└── GraphExporter.java           ← Exportar resultados (opcional)

data/
├── students.txt                 ← Datos de prueba
└── sports.txt                   ← Catálogo de deportes

/raíz del proyecto/
├── Proyecto-Entrega2-Reporte-[N].pdf    ← Reporte técnico
└── video_demo.mp4               ← Video demostrativo
```

### Funcionalidades a Implementar:

- ✅ **AppController**: orquestar flujo de la aplicación

- ✅ **MainWindow**: menú interactivo en consola
  - Opción: Registrar estudiante
  - Opción: Buscar estudiante
  - Opción: Actualizar deportes
  - Opción: Ver comunidades
  - Opción: Estadísticas por deporte
  - Opción: Verificar conexión
  - Opción: Eliminar estudiante
  - Opción: Salir

- ✅ **Mock Data**: generar 10^4, 10^5, 10^6 estudiantes aleatorios

- ✅ **Testing de Rendimiento**:
  - Medir tiempos de: búsqueda (AVL), comunidades, conectividad, estadísticas
  - Con datasets de diferentes tamaños
  - Crear tablas y gráficas de resultados

- ✅ **Reporte Técnico** (4-8 páginas):
  - Problema y objetivo
  - Estructuras elegidas y justificación (sin grafos en esta entrega)
  - Análisis de complejidad teórica
  - Resultados empíricos (tablas, gráficas)
  - Conclusiones

- ✅ **Video** (≤ 8 minutos):
  - Demostración de funcionalidades
  - Análisis de rendimiento
  - Explicar por qué estas estructuras son óptimas

### Commits Esperados (mín. 3):
1. UI base + AppController
2. Mock data + Testing
3. Integración final

---

## 📊 Integración General

| Etapa | Responsables | Actividad |
|-------|--------------|-----------|
| **Setup Inicial** | Todos | Crear estructura de carpetas, `.gitignore`, configurar proyecto |
| **Desarrollo** | Cada uno su módulo | Implementar en paralelo |
| **Integración** | Miembro 3 + apoyo de otros | Conectar servicios en AppMain |
| **Testing** | Miembro 3 (Miembro 1 y 2 apoyan) | Pruebas unitarias y rendimiento |
| **Documentación** | Miembro 3 | Reporte y video |

---

## ✅ Criterios de Calidad por Rol

### Miembro 1 (Estructuras):
- Código limpio y bien documentado
- Manejo correcto de memoria
- Métodos eficientes O(n) o mejor donde sea posible

### Miembro 2 (Servicios):
- Lógica correcta y optimizada
- Uso eficiente de estructuras del Miembro 1
- Manejo de casos edge (estudiantes duplicados, deportes inexistentes, etc.)

### Miembro 3 (UI & Reporte):
- Interfaz intuitiva y sin errores
- Análisis de rendimiento riguroso
- Reporte profesional con conclusiones

---

## 🚀 Entrega #3 (Preview)

Se permitirá usar **Grafos** para una solución alternativa más óptima. Por eso, documenten dónde usan Cola/Pila para que sea fácil migrar.

---
