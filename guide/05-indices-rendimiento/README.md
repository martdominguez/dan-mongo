# Modulo 05: Indices y rendimiento en MongoDB

## Objetivo

Entender que es un indice en MongoDB, por que cambia el rendimiento de una consulta y como diseñarlo con criterio para mejorar tiempos de respuesta en APIs y servicios backend.

## Que vas a aprender

- Que es un indice y por que se parece al indice de un libro.
- Por que una consulta indexada puede responder mucho mas rapido que una consulta sin indice.
- Como decide MongoDB entre usar un indice o recorrer la coleccion.
- Que diferencia hay entre `collection scan` e `index scan` a nivel conceptual.
- Que tipos de indices aparecen primero en backend: campo simple, compuesto, texto y multikey.
- Como elegir campos y orden en un indice segun los patrones reales de consulta.
- Que costos agregan los indices en escritura y almacenamiento.
- Que errores comunes degradan el rendimiento en lugar de mejorarlo.

## Enfoque del modulo

Este modulo no trata los indices como una optimizacion abstracta. Los trata como una decision concreta que impacta en:

- tiempo de respuesta de una API
- carga sobre la base de datos
- latencia percibida por usuarios o servicios consumidores
- estabilidad de endpoints que hacen consultas frecuentes

Vamos a usar escenarios cercanos al trabajo de backend con Spring Boot:

- busqueda de usuarios por email
- ordenes filtradas por usuario y fecha
- productos filtrados por categoria

No vamos a profundizar en internals avanzados, sharding ni optimizacion profunda de agregaciones. La prioridad es entender que indice conviene crear, por que y que trade-off trae.

## Archivos del modulo

- `theory.md`: conceptos, criterios y advertencias practicas.
- `examples.md`: consultas realistas y comparaciones con y sin indice.
- `exercises.md`: ejercicios de decision y diseño.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- explicar con claridad que es un indice
- identificar consultas que hoy son lentas por falta de indice
- decidir que campo o combinacion de campos conviene indexar
- detectar indices mal pensados o innecesarios
- relacionar una decision de indexado con el rendimiento de endpoints y servicios backend
- dejar mejor preparado el terreno para usar MongoDB desde Spring Boot con criterio
