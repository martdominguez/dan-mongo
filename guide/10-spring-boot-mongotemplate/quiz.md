# Quiz

## Preguntas de repaso

1. Que es `MongoTemplate` dentro de Spring Data MongoDB.

2. Por que `MongoTemplate` existe ademas de `MongoRepository`.

3. Cual es la diferencia principal entre abstraccion y flexibilidad al comparar `MongoRepository` con `MongoTemplate`.

4. En que tipo de escenarios suele ser mejor opcion `MongoTemplate`.

5. En que tipo de escenarios sigue siendo preferible `MongoRepository`.

6. Que representa la clase `Query` en Spring Data MongoDB.

7. Que representa `Criteria` y por que resulta util para filtros dinamicos.

8. Como agregarias un filtro opcional por `status` a una consulta construida programaticamente.

9. Por que el ordenamiento no deberia verse como un detalle menor en una API paginada.

10. Que rol cumplen `skip` y `limit` en una paginacion introductoria.

11. Por que conviene usar un orden estable cuando paginas resultados.

12. Que problema practico evita un enfoque basado en filtros dinamicos frente a muchos metodos derivados.

13. Que hace `updateFirst` y en que caso encaja mejor.

14. Que hace `updateMulti` y que riesgo obliga a considerar antes de usarlo.

15. Que significa que una actualizacion sea parcial.

16. Por que una actualizacion parcial suele ser mejor opcion que reescribir el documento completo en ciertos casos.

17. Que informacion util puede aportar el resultado de una operacion de update.

18. Por que las agregaciones programaticas conectan naturalmente con lo aprendido en el modulo 06.

19. Que ventaja tiene construir una agregacion con la API `Aggregation` dentro de un backend Spring Boot.

20. Que beneficio tiene mapear el resultado de una agregacion a un DTO especifico.

21. Por que conviene encapsular consultas complejas en un repositorio custom o una clase dedicada a persistencia.

22. Que problema arquitectonico aparece si un controlador construye directamente `Query`, `Criteria` y `Update`.

23. Como puede convivir `MongoRepository` con `MongoTemplate` dentro del mismo codebase.

24. En que sentido el modulo 04 influye sobre el uso correcto de `MongoTemplate`.

25. En que sentido el modulo 05 influye sobre la forma de escribir queries y ordenamientos con `MongoTemplate`.

26. En que sentido el modulo 08 ayuda a ubicar el rol de transacciones en una aplicacion Spring con MongoDB.

27. Por que una transaccion no corrige por si sola un modelado deficiente.

28. Que tipo de pruebas suelen dar mas valor para una capa basada en `MongoTemplate`.

29. Por que no alcanza con usar mocks si quieres validar una query dinamica o una agregacion programatica.

30. Completa la idea: `MongoRepository` para ______ y `MongoTemplate` para ______.

## Autoevaluacion breve

Si puedes responder con claridad estas afirmaciones, el objetivo del modulo esta cumplido:

- Puedo explicar por que `MongoTemplate` existe y cuando agrega valor real.
- Puedo construir consultas con `Query` y `Criteria` sin mezclar la logica en el controlador.
- Puedo distinguir entre `updateFirst` y `updateMulti`.
- Puedo leer una agregacion programatica y relacionarla con el Aggregation Framework de MongoDB.
- Puedo decidir cuando mantener un caso en `MongoRepository` y cuando moverlo a una capa custom con `MongoTemplate`.
