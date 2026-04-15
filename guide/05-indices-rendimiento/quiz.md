# Quiz

## Preguntas de repaso

1. Que es un indice en MongoDB y que problema resuelve.

2. Que analogia simple ayuda a entender como funciona un indice.

3. Cual es la diferencia conceptual entre usar un indice y recorrer una coleccion completa.

4. Por que los indices impactan directamente en el tiempo de respuesta de una API.

5. Que tipo de indice crearias para buscar usuarios por `email` y por que.

6. Que diferencia hay entre un indice simple y un indice compuesto.

7. Por que en un indice compuesto importa el orden de los campos.

8. Para una consulta de ordenes por `userId` y `createdAt`, que tipo de indice suele tener mas sentido.

9. Que es un indice de texto y para que tipo de necesidad puede servir en una introduccion.

10. Que idea basica debes recordar sobre indices multikey y arrays.

11. Menciona dos costos que agrega un indice ademas de sus beneficios de lectura.

12. Por que tener demasiados indices puede ser un problema en una coleccion con muchas escrituras.

13. Que significa sobre-indexar.

14. Por que indexar un campo de baja selectividad puede ser una mala decision.

15. Cual de estos casos parece mejor candidato para indexar primero y por que:

- `email` en `users`
- `active` en `products`

16. Una API filtra productos por `category` miles de veces por dia. Que decision tecnica gana fuerza y por que.

17. Una consulta frecuente usa `userId` y fecha. Que error comun podrias cometer aunque elijas los campos correctos.

18. Por que este modulo prepara el terreno para trabajar mas adelante con Spring Boot y repositorios.

## Autoevaluacion breve

Si puedes responder con claridad estas tres afirmaciones, el objetivo del modulo esta cumplido:

- Entiendo que un indice mejora una consulta al reducir el trabajo de busqueda.
- Puedo pensar un indice desde patrones reales de API y no desde intuicion aislada.
- Distingo beneficios, costos y errores comunes al diseñar indices en MongoDB.
