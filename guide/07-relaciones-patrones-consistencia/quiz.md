# Quiz

## Preguntas de repaso

1. En MongoDB, por que representar una relacion no implica copiar automaticamente una clave foranea y una tabla intermedia como en SQL.

2. En una relacion uno a uno entre usuario y direccion principal, que condiciones vuelven natural el embebido.

3. Que diferencia practica importante hay entre una relacion uno a muchos pequena y acotada frente a una potencialmente ilimitada.

4. Por que una orden suele ser un buen lugar para embeder `items` con `productId`, `productName` y `unitPrice`.

5. Que problema intenta resolver el `subset pattern`.

6. Que significa, a nivel introductorio, la idea de referencia extendida.

7. Por que duplicar un campo no siempre es un error en MongoDB.

8. Que preguntas deberias responder antes de duplicar un dato descriptivo en muchos documentos.

9. Que significa pensar consistencia a nivel de aplicacion.

10. En que escenarios una inconsistencia temporal pequena podria ser aceptable y en cuales te preocuparia mucho mas.

11. Tienes `studentIds` en `courses` y `courseIds` en `students`. Que costo de mantenimiento aparece aunque las lecturas puedan simplificarse.

12. Por que guardar todo el historial de ordenes dentro del documento `users` suele ser una mala idea.

13. En un catalogo, que ventaja puede tener guardar `categoryId` junto con `category.name` dentro del producto.

14. Que riesgo aparece si duplicas datos muy inestables sin una estrategia explicita de sincronizacion.

15. Cual es la diferencia entre un dato duplicado que debe quedar historico y un dato duplicado que debe sincronizarse.

16. En una plataforma de cursos, por que una coleccion `enrollments` puede ser mejor que solo arrays de ids cuando la relacion tiene atributos propios.

17. Que error de modelado aparece cuando una coleccion queda compuesta casi solo por ids hacia otras colecciones.

18. Por que el criterio de consistencia deberia influir en el modelo desde el comienzo y no recien despues.

## Autoevaluacion breve

Si puedes responder con claridad estas afirmaciones, el objetivo del modulo esta cumplido:

- Puedo representar relaciones frecuentes en MongoDB sin copiar literalmente un modelo relacional.
- Puedo justificar cuando conviene embeder, referenciar o duplicar parte de un dato.
- Puedo detectar riesgos de consistencia y proponer una estrategia simple de actualizacion.
