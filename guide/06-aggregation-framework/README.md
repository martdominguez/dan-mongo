# Modulo 06: Aggregation Framework

## Objetivo

Entender que es una agregacion en MongoDB, cuando conviene usar un `pipeline` y como resolver consultas de resumen, transformacion y agrupacion que aparecen seguido en backends reales.

## Que vas a aprender

- Que diferencia hay entre una consulta simple con `find` y una agregacion con `aggregate`.
- Por que un `pipeline` es util cuando no alcanza con filtrar documentos y devolverlos tal como estan.
- Como pensar una agregacion como una secuencia de `stages`.
- Que hace cada stage basico: `$match`, `$project`, `$group`, `$sort`, `$limit` y `$skip`.
- Para que sirven stages muy utiles en la practica como `$unwind`, `$count` y `$addFields`.
- Como construir pipelines que respondan preguntas frecuentes de backend:
  - totales por categoria
  - conteos por estado
  - resumenes mensuales
  - trabajo con arrays
- Que buenas practicas ayudan a escribir pipelines mas legibles y eficientes en una primera etapa.

## Enfoque del modulo

Este modulo presenta Aggregation Framework como una herramienta de trabajo de backend, no como una caracteristica aislada de MongoDB.

La idea central es esta:

- `find` sirve muy bien para buscar documentos
- `aggregate` sirve cuando necesitas transformar, resumir o reorganizar esos datos antes de devolverlos

Vamos a trabajar con escenarios cercanos al desarrollo de APIs:

- paneles administrativos con conteos por estado
- reportes simples de ordenes por mes
- resumenes de ventas por categoria
- analisis de tickets de soporte
- transformaciones de arrays para exponer respuestas mas utiles

No vamos a entrar todavia en operadores muy avanzados ni en pipelines demasiado largos. La prioridad es construir una base clara, util y reutilizable para modulos futuros, donde mas adelante veremos como llevar estas agregaciones a Spring Boot con `MongoTemplate`.

## Archivos del modulo

- `theory.md`: conceptos, stages, criterios y buenas practicas.
- `examples.md`: pipelines realistas explicados paso a paso.
- `exercises.md`: ejercicios para practicar armado y lectura de agregaciones.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- distinguir cuando alcanza una consulta con `find` y cuando conviene un `pipeline`
- leer una agregacion simple y explicar que devuelve
- construir pipelines cortos y medianos para resumir informacion
- elegir stages basicos con criterio segun la necesidad del negocio
- pensar resultados orientados a lo que necesita una API o un reporte interno
- escribir agregaciones iniciales legibles, progresivas y sin complejidad innecesaria
