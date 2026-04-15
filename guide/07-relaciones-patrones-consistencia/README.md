# Modulo 07: Relaciones, patrones de modelado y consistencia

## Objetivo

Profundizar el modelado documental en MongoDB para representar relaciones reales de backend, aplicar patrones de diseño frecuentes y tomar decisiones conscientes sobre duplicacion, sincronizacion y consistencia.

## Que vas a aprender

- Como representar relaciones uno a uno, uno a muchos y muchos a muchos en MongoDB.
- Que diferencias practicas hay entre embebidos, referencias y combinaciones intermedias.
- Que patrones de modelado ayudan a equilibrar lecturas, escrituras y evolucion del dato.
- Cuando la duplicacion es un problema y cuando es una decision valida para simplificar lecturas.
- Como pensar consistencia a nivel de aplicacion sin convertir cada caso en una discusion teorica distribuida.
- Que riesgos aparecen cuando el mismo dato vive en varios documentos.
- Como definir estrategias de actualizacion cuando hay datos relacionados o duplicados.
- Que errores de diseño aparecen al intentar copiar una base relacional de forma literal.

## Enfoque del modulo

El modulo 04 introdujo el criterio general para decidir entre embebidos y referencias. Este modulo continua desde ese punto y baja un nivel mas:

- ya no solo preguntamos "donde vive el dato"
- ahora tambien preguntamos "que pasa cuando cambia"
- y "cuanto cuesta mantenerlo coherente en el tiempo"

La idea central es esta:

en MongoDB modelar bien no significa eliminar toda duplicacion. Significa elegir con criterio que relaciones conviene resolver dentro del documento, cuales conviene separar y que estrategia de sincronizacion necesita cada decision.

Vamos a trabajar con escenarios de backend cercanos a Spring Boot:

- usuario con direcciones
- orden con items y snapshots de producto
- plataforma de cursos con estudiantes e inscripciones
- catalogo de productos con categorias y tags

No vamos a profundizar todavia en transacciones ni en consistencia distribuida avanzada. La prioridad es desarrollar criterio practico de modelado para APIs y servicios.

## Archivos del modulo

- `theory.md`: relaciones, patrones, trade-offs y criterios de consistencia.
- `examples.md`: escenarios realistas con modelos comparados y decisiones justificadas.
- `exercises.md`: ejercicios de rediseño, analisis y toma de decisiones.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- representar relaciones frecuentes sin copiar esquemas relacionales de forma mecanica
- justificar cuando conviene embeder, referenciar o duplicar parte de un dato
- detectar riesgos de consistencia antes de que aparezcan bugs funcionales
- proponer estrategias simples de sincronizacion a nivel de aplicacion
- reconocer patrones como `subset` o referencia extendida en escenarios de lectura reales
- diseñar documentos mas alineados con necesidades de backend y mantenimiento futuro
