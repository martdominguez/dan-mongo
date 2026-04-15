# Modulo 09: Spring Boot y MongoRepository

## Objetivo

Integrar MongoDB dentro de una aplicacion Spring Boot usando Spring Data MongoDB y `MongoRepository`, con foco en operaciones CRUD simples, consultas derivadas y una arquitectura backend facil de mantener.

## Que vas a aprender

- Que aporta Spring Data MongoDB dentro de una aplicacion Java con Spring Boot.
- Cuando `MongoRepository` resuelve bien un caso de uso y cuando empieza a quedarse corto.
- Como configurar una aplicacion para conectarse a MongoDB en un entorno local.
- Como mapear documentos MongoDB a clases Java con anotaciones basicas.
- Como implementar operaciones CRUD simples desde un repositorio.
- Como encadenar `controller`, `service` y `repository` en un flujo backend entendible.
- Que buenas practicas conviene aplicar desde el comienzo para no mezclar responsabilidades.
- Que limites tiene la abstraccion y por que el siguiente modulo trabajara con `MongoTemplate`.

## Enfoque del modulo

Hasta ahora el curso se concentro en MongoDB como base de datos: documentos, modelado, consultas, indices, agregaciones, relaciones, validacion y consistencia.

En este modulo damos el paso hacia una aplicacion real.

La pregunta central deja de ser solo "como se consulta MongoDB" y pasa a ser tambien:

- como se integra MongoDB en un backend Spring Boot
- que parte del trabajo resuelve Spring Data MongoDB
- como escribir persistencia simple sin caer en sobreingenieria

La idea no es cubrir todas las capacidades del ecosistema Spring Data ni entrar todavia en consultas dinamicas avanzadas. La idea es construir una base solida para casos cotidianos:

- guardar usuarios, productos, tickets o pedidos
- buscar por id
- listar documentos
- borrar por id
- resolver filtros simples con metodos derivados

`MongoRepository` es especialmente util cuando el caso de uso necesita claridad, velocidad de implementacion y operaciones bien conocidas.

Tambien vamos a marcar su limite con claridad:

- no es la mejor herramienta para consultas muy dinamicas
- no es la herramienta principal para agregaciones complejas
- no conviene forzarla cuando el problema pide mas control sobre la consulta

Ese limite prepara el terreno para el modulo 10.

## Archivos del modulo

- `theory.md`: conceptos, sintaxis, variaciones y criterios de decision.
- `examples.md`: ejemplos guiados en Java y Spring Boot con entidad, repositorio, servicio y controlador.
- `exercises.md`: ejercicios practicos centrados en backend real.
- `quiz.md`: preguntas de repaso conceptual sobre mapeo, repositorios y limites de la abstraccion.

## Resultado esperado

Al terminar este modulo deberias poder:

- configurar una aplicacion Spring Boot para conectarse a MongoDB local
- mapear un documento a una clase Java con anotaciones introductorias
- definir un `MongoRepository` para CRUD basico
- implementar un servicio simple que use el repositorio con criterio
- exponer endpoints REST introductorios sin mezclar demasiada logica en el controlador
- identificar cuando `MongoRepository` es suficiente y cuando conviene pasar a otra herramienta
