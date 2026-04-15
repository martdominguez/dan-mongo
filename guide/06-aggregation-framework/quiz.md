# Quiz

## Preguntas de repaso

1. Que es una agregacion en MongoDB y que tipo de problema resuelve mejor que una consulta simple.

2. Cual es la diferencia principal entre `find` y `aggregate`.

3. Que significa pensar una agregacion como un `pipeline`.

4. Por que el orden de los `stages` importa en una agregacion.

5. Que stage usarias para filtrar documentos al comienzo de un pipeline.

6. Que stage usarias para construir una salida con menos campos o renombrar campos.

7. Que stage usarias para contar documentos por `status`.

8. Que diferencia hay entre usar `$count` y usar `$group` con `$sum: 1`.

9. Para que sirve `$unwind` y en que tipo de estructura de documento suele aparecer.

10. Que ventaja practica tiene usar `$match` temprano cuando es posible.

11. Si una API necesita devolver ventas por categoria, por que `aggregate` suele ser mejor opcion que traer todas las ordenes y procesarlas fuera de MongoDB.

12. Que devuelve este pipeline:

```js
db.users.aggregate([
  { $match: { active: true } },
  { $count: "totalActiveUsers" }
])
```

13. Que devuelve este pipeline:

```js
db.orders.aggregate([
  {
    $group: {
      _id: "$status",
      totalOrders: { $sum: 1 }
    }
  }
])
```

14. Que problema de backend ayuda a resolver un pipeline con `$group`, `$sort` y `$limit`.

15. Si quieres paginar resultados agregados, que stages suelen aparecer juntos.

16. En una agregacion introductoria, por que conviene evitar pipelines innecesariamente complejos.

17. Que stage elegirias si quieres agregar un campo temporal dentro del pipeline sin redefinir toda la salida.

18. Que devuelve este pipeline:

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  { $unwind: "$items" },
  {
    $group: {
      _id: "$items.productId",
      totalUnits: { $sum: "$items.quantity" }
    }
  },
  { $sort: { totalUnits: -1 } }
])
```

19. En un resumen mensual de ordenes, por que suele tener sentido ordenar el resultado al final.

20. Como se relaciona este modulo con un futuro uso de `MongoTemplate` en Spring Boot.

## Autoevaluacion breve

Si puedes explicar con claridad estas tres ideas, el objetivo del modulo esta bien encaminado:

- Distingo cuando una necesidad se resuelve con `find` y cuando conviene `aggregate`.
- Entiendo que un `pipeline` transforma documentos paso a paso.
- Puedo leer y escribir agregaciones basicas para resumenes y reportes comunes de backend.
