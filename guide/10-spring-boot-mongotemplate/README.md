# Modulo 10: Spring Boot y MongoTemplate

## Objetivo

Trabajar MongoDB desde Spring Boot con `MongoTemplate`, con foco en consultas dinamicas, actualizaciones parciales, agregaciones programaticas y una organizacion de persistencia mas flexible para backends profesionales.

## Que vas a aprender

- Que es `MongoTemplate` y que problema resuelve dentro de Spring Data MongoDB.
- Por que existe ademas de `MongoRepository`.
- En que escenarios `MongoTemplate` es una mejor herramienta que un repositorio derivado.
- Como construir consultas con `Query`, `Criteria`, ordenamiento y paginacion introductoria.
- Como resolver filtros dinamicos sin multiplicar metodos de repositorio.
- Como ejecutar actualizaciones parciales con `updateFirst` y `updateMulti`.
- Como construir agregaciones programaticas con la API `Aggregation`.
- Como organizar una capa de persistencia con servicio y repositorio custom.
- Como pensar transacciones y testing sin perder el foco practico.
- Que buenas practicas ayudan a mantener legible la persistencia y alineada con modelado e indices.

## Enfoque del modulo

El modulo anterior mostro una integracion muy productiva con `MongoRepository`.

Ese enfoque sigue siendo correcto para muchas APIs. Sin embargo, un backend real suele crecer hacia necesidades mas flexibles:

- filtros opcionales combinables
- actualizaciones parciales sobre varios documentos
- consultas con criterios que dependen del request
- pipelines de agregacion armados desde codigo
- logica de acceso a datos que ya no entra comodamente en metodos derivados

En ese punto aparece `MongoTemplate`.

La idea de este modulo no es reemplazar todo por `MongoTemplate`.

La idea es aprender a elegir mejor:

- `MongoRepository` para CRUD simple y consultas previsibles
- `MongoTemplate` para persistencia mas dinamica, expresiva y controlada

Tambien vamos a conectar este modulo con temas ya vistos:

- modulo 04 para recordar que las consultas dependen del modelado
- modulo 05 para alinear filtros y ordenamientos con indices
- modulo 06 para traducir pipelines al estilo programatico de Spring
- modulo 08 para ubicar transacciones y testing en un contexto realista

## Archivos del modulo

- `theory.md`: conceptos, sintaxis, variaciones, criterios de uso y buenas practicas.
- `examples.md`: ejemplos guiados en Java y Spring Boot con consultas, updates, agregaciones y repositorio custom.
- `exercises.md`: ejercicios practicos orientados a tareas backend reales.
- `quiz.md`: preguntas de repaso conceptual, tecnico y de criterio de diseno.

## Resultado esperado

Al terminar este modulo deberias poder:

- explicar cuando `MongoTemplate` agrega valor real en un proyecto Spring Boot
- construir consultas con `Query` y `Criteria` sin perder legibilidad
- implementar filtros dinamicos y ordenamientos simples con criterio backend
- ejecutar actualizaciones parciales sin reescribir documentos completos
- armar una agregacion introductoria con la API programatica de Spring Data
- separar persistencia compleja en una estructura de servicio y repositorio custom
- decidir con criterio cuando seguir con `MongoRepository` y cuando pasar a `MongoTemplate`
