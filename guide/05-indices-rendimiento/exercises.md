# Ejercicios

## 1. Elegir el campo a indexar

### Consigna

Tienes una coleccion `users` y estas consultas aparecen seguido:

- `findOne({ email: "..." })`
- `find({ name: "..." })`
- `find({ country: "AR" })`

Indica que campo seria tu primera prioridad para indexar y explica por que.

## 2. Diseñar un indice para historial de ordenes

### Consigna

Una API consulta ordenes de esta manera:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

Propone un indice y justifica el orden de sus campos.

## 3. Detectar un indice malo

### Consigna

Analiza este caso:

Consulta frecuente:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
})
```

Indice creado:

```js
db.orders.createIndex({ createdAt: 1, userId: 1 })
```

Explica por que este indice podria no ser la mejor opcion.

## 4. Decidir entre indice simple o compuesto

### Consigna

Para cada escenario, indica si usarías un indice simple o compuesto.

1. login por email
2. listado de ordenes por usuario y fecha
3. filtro de productos activos por categoria
4. busqueda administrativa por estado y fecha de creacion

Justifica cada caso en una o dos lineas.

## 5. Detectar sobre-indexacion

### Consigna

Un equipo propone crear indices en todos estos campos de `products`:

- `name`
- `category`
- `price`
- `active`
- `stock`
- `createdBy`
- `updatedBy`

Sin conocer las consultas reales, explica por que esa decision es riesgosa y que pregunta deberian hacerse antes.

## 6. Escenario real de backend: login

### Consigna

Un servicio de autenticacion consulta `users` por `email` en cada intento de login.

Explica:

1. que indice crearías
2. que problema de rendimiento ayuda a evitar
3. como impacta eso en el tiempo de respuesta de la API

## 7. Escenario real de backend: historial de compras

### Consigna

Un endpoint `GET /api/orders/history` recibe:

- `userId`
- fecha desde
- fecha hasta

Ademas devuelve las ordenes mas recientes primero.

Propone un indice razonable y explica por que ese indice se relaciona con el uso real del servicio.

## 8. Campo de baja selectividad

### Consigna

En `products`, casi todos los documentos tienen `active: true`.

Responde:

1. por que un indice solo sobre `active` puede ser poco util
2. en que caso igual lo revisarias con mas cuidado

## 9. Diseñar indices para un pequeno backend de ecommerce

### Consigna

Tienes estas consultas frecuentes:

- buscar usuario por `email`
- listar productos por `category` y `active`
- listar ordenes por `userId` y `createdAt`

Propone los indices que crearías primero y explica por que los priorizas en ese orden.

## 10. Detectar el error de criterio

### Consigna

Lee esta frase:

"Como los indices mejoran lecturas, conviene agregar uno por cada campo importante."

Explica por que esa afirmacion es incompleta o incorrecta.

## 11. Comparar consulta con y sin indice

### Consigna

Toma una de estas consultas:

- `findOne({ email: "ana@example.com" })`
- `find({ category: "notebooks", active: true })`

Describe conceptualmente:

1. que pasa si no hay indice
2. que cambia si si lo hay
3. que efecto tendria eso sobre una API que ejecuta esa consulta muchas veces

## 12. Elegir el mejor indice entre dos opciones

### Consigna

Para esta consulta:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-03-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

Elige entre estas opciones y justifica:

1. `{ userId: 1 }`
2. `{ createdAt: -1 }`
3. `{ userId: 1, createdAt: -1 }`

### Objetivo

Practicar criterio de diseño y no solo definiciones.
