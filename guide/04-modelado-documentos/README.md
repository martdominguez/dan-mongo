# Modulo 04: Modelado de documentos y diseño de colecciones

## Objetivo

Aprender a diseñar documentos y colecciones en MongoDB con criterio de backend, entendiendo cuando conviene embeder, cuando conviene referenciar y por que el diseño debe responder a las consultas reales de la aplicacion.

## Que vas a aprender

- Como cambiar el chip desde tablas hacia documentos.
- Por que en MongoDB los joins no son la estrategia por defecto.
- Por que conviene diseñar para las consultas y no copiar normalizacion relacional sin pensar.
- Cuando usar documentos embebidos y cuando usar referencias.
- Como modelar relaciones uno a uno, uno a muchos y muchos a muchos.
- Que trade-offs aparecen entre lectura, escritura, duplicacion y consistencia.
- Como elegir estructuras segun patrones de acceso del backend.
- Que errores de modelado aparecen seguido al empezar con MongoDB.

## Enfoque del modulo

Este modulo se centra en una idea clave: en MongoDB no gana el modelo mas "correcto" en abstracto, sino el modelo que mejor acompaña como tu backend lee y escribe los datos.

Por eso vamos a trabajar con escenarios realistas:

- `users`
- `orders`
- `products`
- endpoints y consultas frecuentes de APIs

Todavia no vamos a profundizar en indices ni en aggregation pipelines. En este punto lo importante es aprender a estructurar bien los documentos para que las operaciones basicas del backend resulten naturales, claras y eficientes.

## Archivos del modulo

- `theory.md`: conceptos, criterios y trade-offs de diseño.
- `examples.md`: modelos comparados con escenarios reales.
- `exercises.md`: ejercicios de rediseño y toma de decisiones.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- pensar una entidad de backend como documento y no solo como tabla
- decidir cuando conviene embeder y cuando conviene referenciar
- modelar relaciones frecuentes en MongoDB sin copiar automaticamente un esquema relacional
- justificar un diseño segun lecturas, escrituras y patrones de acceso
- detectar errores comunes de modelado antes de que compliquen a la API
