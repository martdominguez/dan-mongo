# Quiz

## Preguntas de repaso

1. Que cambio de modelo mental hace falta al pasar de una base de datos relacional a MongoDB.

2. Por que en MongoDB no conviene asumir que los joins son la estrategia principal para todo.

3. Que significa diseñar para consultas en lugar de diseñar solo para normalizacion.

4. Cuando suele convenir embeder un documento dentro de otro.

5. Cuando suele convenir referenciar en lugar de embeder.

6. Que trade-off principal aparece entre embebidos y referencias en terminos de lectura y consistencia.

7. En una relacion uno a uno entre usuario y preferencias, que decision suele ser la mas natural y por que.

8. Por que embeder todas las ordenes historicas dentro del documento `users` suele ser una mala idea.

9. Tienes un endpoint de detalle de orden que se consulta todo el tiempo. Que tipo de diseño tenderia a ayudar mas y por que.

10. En un ecommerce, por que puede ser razonable que una orden guarde `productName` aunque exista una coleccion `products`.

11. Que problema puede generar la sobre-normalizacion en una API backend.

12. Que problema puede generar el sobre-embedding en una coleccion.

13. Cual de estos casos te preocuparia mas por crecimiento sin limite:

- preferencias del usuario
- items de una orden comun
- historial de eventos de varios años

Explica por que.

14. En una relacion muchos a muchos, por que no siempre hace falta copiar exactamente una tabla intermedia relacional.

15. Si una entidad cambia mucho y se reutiliza desde muchos documentos, que decision suele ganar fuerza: embeder o referenciar. Justifica.

## Autoevaluacion breve

Si puedes responder con claridad estas tres afirmaciones, el objetivo del modulo esta cumplido:

- Puedo justificar un modelo documental segun como el backend lee y escribe datos.
- Distingo cuando una duplicacion aporta valor y cuando solo agrega mantenimiento.
- Puedo detectar errores comunes como sobre-normalizacion, sobre-embedding y arreglos sin limite.
