# Quiz

## Preguntas de repaso

1. Que problema resuelve Spring Data MongoDB dentro de una aplicacion Spring Boot.

2. Que ventaja concreta ofrece `MongoRepository` frente a escribir manualmente cada operacion CRUD introductoria.

3. Cuando suele ser una buena decision usar `MongoRepository` como abstraccion principal.

4. Que anotacion se usa para mapear una clase Java a una coleccion MongoDB.

5. Que funcion cumple `@Id` dentro de una entidad persistida.

6. Para que sirve `@Field` y en que caso conviene usarlo.

7. Que responsabilidad tecnica resuelve la propiedad `spring.data.mongodb.uri`.

8. Cual es la diferencia entre configurar MongoDB con `application.yml` y con `application.properties`.

9. Que partes basicas componen una connection string como `mongodb://localhost:27017/support_db`.

10. Por que en este modulo se asume un entorno local simple y no una infraestructura compleja.

11. Que devuelve `findById` y por que ese detalle es importante para el servicio.

12. Que riesgos practicos tiene usar `findAll` sin pensar en el volumen de datos.

13. Que operaciones resuelven `save`, `findById`, `findAll` y `deleteById`.

14. Que es un metodo derivado en Spring Data MongoDB.

15. Da un ejemplo de consulta que se pueda expresar bien con un metodo derivado.

16. Que ventaja tiene mantener separado el flujo `controller -> service -> repository`.

17. Por que conviene usar inyeccion por constructor en servicios y controladores.

18. Por que no siempre es buena idea exponer directamente la entidad persistida como contrato HTTP.

19. Que tipo de problemas empiezan a volver incomodo el uso de `MongoRepository`.

20. Por que las consultas dinamicas con muchos filtros opcionales suelen señalar un limite de la abstraccion.

21. Por que las agregaciones complejas no son el foco principal de este modulo.

22. Que herramienta se presentara en el siguiente modulo y por que.

23. Cual es el error conceptual de intentar resolver cualquier necesidad MongoDB solo con repositorios.

24. Si un backend solo necesita crear, buscar por id, listar y borrar por id, por que `MongoRepository` suele ser suficiente.

## Autoevaluacion breve

Si puedes responder con claridad estas afirmaciones, el objetivo del modulo esta cumplido:

- Puedo explicar que aporta Spring Data MongoDB a una aplicacion Spring Boot.
- Puedo mapear un documento simple con `@Document` y `@Id`.
- Puedo definir un `MongoRepository` para CRUD y consultas derivadas basicas.
- Puedo distinguir entre un caso simple para repositorio y un caso que pide una herramienta mas flexible.
