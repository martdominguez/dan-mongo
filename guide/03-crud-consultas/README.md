# Modulo 03: CRUD y consultas fundamentales

## Objetivo

Aprender las operaciones basicas de escritura, lectura, actualizacion y borrado en MongoDB, junto con consultas simples que aparecen seguido en servicios backend y APIs.

## Que vas a aprender

- Como usar `insertOne` e `insertMany` para crear documentos.
- Como consultar datos con `find` y `findOne`.
- Como aplicar filtros basicos sobre campos comunes de negocio.
- Como usar proyecciones simples para devolver solo lo necesario.
- Como ordenar y limitar resultados con `sort` y `limit`.
- Como modificar datos con `updateOne` y `updateMany`.
- Como eliminar documentos con `deleteOne` y `deleteMany`.
- Como traducir estas operaciones a tareas reales de un backend.

## Enfoque del modulo

Este modulo se mete por primera vez en el trabajo cotidiano con datos. La idea no es aprender comandos aislados, sino entender que consulta o escritura haria un servicio backend en un caso real.

Por eso los ejemplos usan dominios cercanos a una API real:

- `users`
- `orders`
- `products`
- respuestas y necesidades tipicas de servicios backend

Todavia no vamos a profundizar en agregaciones ni en indices. Primero necesitamos dominar bien el flujo basico: crear datos, buscarlos, actualizarlos y borrarlos con criterio.

Tambien vamos a dejar preparado el terreno para los modulos futuros de Spring Boot. Mas adelante, estas operaciones se van a traducir en repositorios, servicios y consultas desde Java.

## Archivos del modulo

- `theory.md`: conceptos clave, sintaxis y criterio de uso.
- `examples.md`: ejemplos guiados con `mongosh` y contexto backend.
- `exercises.md`: ejercicios breves y progresivos.
- `quiz.md`: preguntas de repaso conceptual y aplicado.

## Resultado esperado

Al terminar este modulo deberias poder:

- insertar documentos de forma intencional en una coleccion
- consultar uno o varios documentos segun un filtro simple
- devolver solo algunos campos cuando una API no necesita el documento completo
- aplicar orden y limite en consultas frecuentes
- actualizar y borrar documentos sin confundir la operacion ni su alcance
- relacionar cada comando con una necesidad concreta de un servicio backend
