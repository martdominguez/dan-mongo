# Quiz

## Preguntas de repaso

1. Por que la flexibilidad de MongoDB vuelve importante definir algun nivel de validacion en colecciones reales.

2. Que diferencia practica hay entre validar en la aplicacion y validar en la coleccion.

3. Que problema resuelve `required` dentro de una regla con `$jsonSchema`.

4. Para que sirve `bsonType` y por que ayuda a la calidad del dato.

5. En que casos `enum` resulta especialmente util dentro de un documento de backend.

6. Por que una validacion de coleccion no reemplaza por completo las reglas de negocio.

7. Que es una transaccion, explicado en terminos de resultado final y no de sintaxis.

8. Cuando una transaccion suele ser razonable en MongoDB.

9. Cuando una transaccion suele ser innecesaria porque una sola escritura de documento ya alcanza.

10. Que relacion hay entre buen modelado documental y menor necesidad de transacciones.

11. Que idea cumple una sesion cuando trabajas con transacciones en MongoDB.

12. Que costos o limitaciones introductorias conviene recordar antes de usar transacciones para todo.

13. Por que registrar un pago y actualizar la orden asociada puede ser un mejor candidato a transaccion que escribir un log secundario.

14. Que diferencia conceptual hay entre autenticacion y autorizacion.

15. En que consiste el principio de menor privilegio aplicado a una aplicacion backend.

16. Por que es riesgoso conectar un servicio con un usuario que tiene permisos administrativos generales.

17. Que tipo de servicio podria funcionar correctamente con permisos solo de lectura.

18. Por que operaciones como `deleteMany({})` o `updateMany({})` sin filtro fuerte merecen especial cuidado.

19. Cual es el error de confiar solo en la validacion del backend y no proteger tambien la coleccion.

20. Cual es el problema de confundir validacion estructural con reglas completas del negocio.

21. Si una orden ya contiene `items`, `total` y `status` en el mismo documento, por que abrir una transaccion para cada cambio puede ser una mala decision.

22. Que riesgos aparecen si varias APIs distintas comparten la misma credencial poderosa contra produccion.

## Autoevaluacion breve

Si puedes responder con claridad estas afirmaciones, el objetivo del modulo esta cumplido:

- Puedo explicar para que sirve la validacion de coleccion y que tipo de reglas conviene poner primero.
- Puedo distinguir una transaccion necesaria de una transaccion innecesaria.
- Puedo explicar que rol cumple una sesion en una transaccion MongoDB.
- Puedo detectar permisos excesivos y proponer un acceso mas seguro desde un backend.
