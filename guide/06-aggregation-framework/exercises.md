# Ejercicios

## 1. Elegir entre `find` y `aggregate`

### Consigna

Para cada necesidad, indica si resolverias el caso con `find` o con `aggregate` y explica por que.

1. traer los productos activos de la categoria `monitors`
2. contar cuantas ordenes hay por `status`
3. obtener las ultimas 5 ordenes pagadas
4. calcular el total vendido por categoria

## 2. Pipeline para resumen por estado

### Consigna

La coleccion `supportTickets` guarda tickets con un campo `status`.

Construye un pipeline que devuelva cuántos tickets hay por estado y ordene el resultado de mayor a menor segun la cantidad.

## 3. Filtrar antes de agrupar

### Consigna

La coleccion `orders` contiene ordenes con campos `status`, `total` y `createdAt`.

Construye un pipeline que:

- tome solo las ordenes con `status: "PAID"`
- agrupe por mes de `createdAt`
- calcule cantidad de ordenes y suma total por mes
- ordene cronologicamente

## 4. Resumir datos por categoria

### Consigna

En la coleccion `products`, cada documento tiene `category` y `active`.

Escribe un pipeline que muestre cuántos productos activos hay por categoria y ordene las categorias alfabeticamente.

## 5. Contar registros por estado en un escenario de API

### Consigna

Un endpoint `GET /api/admin/orders/status-summary` debe devolver un resumen de ordenes por estado.

Define un pipeline adecuado para esta necesidad y explica en una o dos lineas por que ese resultado es mejor que traer todas las ordenes al backend y contar en Java.

## 6. Extraer datos desde arrays con `$unwind`

### Consigna

Cada orden de `orders` tiene un array `items` con:

```json
{
  "productId": "p10",
  "name": "Mouse",
  "quantity": 2
}
```

Construye un pipeline que:

- tome solo ordenes pagadas
- aplane `items`
- devuelva `orderId`, `productId`, `name` y `quantity`

## 7. Combinar `$match` + `$group` + `$sort`

### Consigna

En `supportTickets`, cada ticket tiene `priority` y `status`.

Construye un pipeline que:

- tome solo tickets `OPEN` y `PENDING`
- agrupe por `priority`
- cuente cuántos tickets hay por prioridad
- ordene de mayor a menor segun la cantidad

## 8. Escenario backend: dashboard de ventas

### Consigna

Un dashboard interno necesita mostrar las 3 categorias con mayor facturacion. La coleccion `orders` tiene `status`, `category` y `total`.

Construye un pipeline que:

- considere solo ordenes pagadas
- agrupe por categoria
- calcule la facturacion total
- ordene de mayor a menor
- devuelva solo las 3 primeras categorias

## 9. Escenario backend: resumen de usuarios activos

### Consigna

Una API interna necesita responder solo este dato:

"cantidad total de usuarios activos"

La coleccion `users` tiene el campo `active`.

Escribe un pipeline usando el stage mas adecuado para devolver un unico total final.

## 10. Proyectar una salida mas util para la API

### Consigna

Partiendo de `orders`, arma un pipeline que:

- filtre ordenes con `status: "PAID"`
- proyecte una salida con `orderId`, `userId`, `total` y `createdAt`
- excluya `_id`
- ordene por fecha descendente

Explica que ventaja tiene esa proyeccion para una API de backoffice.

## 11. Leer y explicar un pipeline

### Consigna

Lee este pipeline y explica con tus palabras que devuelve:

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

### Objetivo

Practicar lectura de pipelines, no solo escritura.

## 12. Diseñar un pipeline legible

### Consigna

Toma una de estas necesidades:

- conteo de tickets por estado
- ventas por categoria
- ordenes pagadas por mes

Escribe un pipeline corto y luego explica por que elegiste ese orden de stages.

### Restriccion

La solucion debe mantenerse introductoria y no usar stages u operadores avanzados que no aparecen en este modulo.
