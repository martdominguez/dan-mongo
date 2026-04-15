# Ejemplos

## 1. Registrar un usuario nuevo con `insertOne`

### Que problema resuelve

Una API de autenticacion o de administracion necesita crear un usuario nuevo.

### Comando en `mongosh`

```js
db.users.insertOne({
  name: "Lucia Gomez",
  email: "lucia@example.com",
  role: "customer",
  active: true,
  createdAt: ISODate("2026-04-15T10:00:00Z")
})
```

### Resultado esperado

MongoDB devuelve confirmacion de la insercion y el `_id` generado.

### Mapeo a backend

Esto se parece a un endpoint como `POST /api/users`.

La idea tecnica es simple: llega un payload, se valida y se persiste un documento.

## 2. Cargar productos iniciales con `insertMany`

### Que problema resuelve

Un backend de ecommerce necesita datos iniciales para probar listados y filtros.

### Comando en `mongosh`

```js
db.products.insertMany([
  {
    name: "Mouse Bluetooth",
    category: "accessories",
    price: 25000,
    active: true,
    stock: 18
  },
  {
    name: "Teclado Mecanico",
    category: "accessories",
    price: 78000,
    active: true,
    stock: 7
  },
  {
    name: "Monitor 27",
    category: "monitors",
    price: 320000,
    active: true,
    stock: 4
  }
])
```

### Resultado esperado

Se insertan tres documentos de una sola vez.

### Advertencia

En un backend real esto puede servir para datos de prueba, seeds o una carga inicial acotada.

## 3. Buscar un usuario puntual con `findOne`

### Que problema resuelve

Un servicio necesita recuperar un usuario por email durante login o administracion.

### Comando en `mongosh`

```js
db.users.findOne({ email: "lucia@example.com" })
```

### Resultado esperado

Devuelve un unico documento si existe.

### Mapeo a backend

Se parece a una operacion como `findByEmail(...)` dentro de un servicio o repositorio.

## 4. Listar productos activos con `find`

### Que problema resuelve

La API publica del catalogo no deberia mostrar productos inactivos.

### Comando en `mongosh`

```js
db.products.find({ active: true })
```

### Resultado esperado

Devuelve todos los productos activos.

### Idea clave

Aunque la consulta es simple, ya esta expresando una regla de negocio del backend.

## 5. Filtrar pedidos por estado

### Que problema resuelve

Un panel interno quiere ver solo los pedidos pendientes de pago.

### Comando en `mongosh`

```js
db.orders.find({ status: "CREATED" })
```

### Resultado esperado

Devuelve las ordenes cuyo estado actual es `CREATED`.

### Mapeo a backend

Esto encaja con un endpoint como `GET /api/orders?status=CREATED`.

## 6. Filtrar productos por precio maximo

### Que problema resuelve

La API de catalogo necesita soportar un filtro por precio.

### Comando en `mongosh`

```js
db.products.find({
  price: { $lte: 80000 },
  active: true
})
```

### Resultado esperado

Devuelve productos activos con precio menor o igual a `80000`.

### Idea clave

Este tipo de filtro aparece seguido cuando una API expone parametros de consulta simples.

## 7. Devolver solo algunos campos con una proyeccion simple

### Que problema resuelve

Una pantalla de listado no necesita toda la informacion del producto.

### Comando en `mongosh`

```js
db.products.find(
  { active: true },
  { _id: 0, name: 1, price: 1, category: 1 }
)
```

### Resultado esperado

Cada documento devuelve solo:

- `name`
- `price`
- `category`

### Mapeo a backend

Esto se parece mucho a un DTO de respuesta para un endpoint de listado.

## 8. Ordenar y limitar historial de ordenes

### Que problema resuelve

Una API necesita devolver las ultimas 5 ordenes de un usuario.

### Comando en `mongosh`

```js
db.orders.find({ userId: "u1001" })
  .sort({ createdAt: -1 })
  .limit(5)
```

### Resultado esperado

Trae solo cinco ordenes del usuario `u1001`, empezando por la mas reciente.

### Idea clave

`sort` y `limit` son dos piezas basicas para construir listados usables desde una API.

### Puente con modelado

Este tipo de consulta es una buena pista para el diseño del documento: aunque una orden tenga `items` o `userSnapshot` embebidos, conviene mantener `userId` y `createdAt` como campos directos para filtrar e indexar mejor.

## 9. Actualizar el estado de una orden con `updateOne`

### Que problema resuelve

Cuando el pago se confirma, el backend debe cambiar el estado del pedido.

### Comando en `mongosh`

```js
db.orders.updateOne(
  { _id: "ord-9001" },
  { $set: { status: "PAID" } }
)
```

### Resultado esperado

MongoDB indica cuantos documentos coincidieron y cuantos fueron modificados.

### Mapeo a backend

Se parece a una operacion disparada por:

- confirmacion de pago
- webhook externo
- servicio interno de checkout

## 10. Actualizar muchos productos de una categoria con `updateMany`

### Que problema resuelve

Un ajuste operativo necesita desactivar temporalmente todos los productos de una categoria.

### Comando en `mongosh`

```js
db.products.updateMany(
  { category: "monitors" },
  { $set: { active: false } }
)
```

### Resultado esperado

Todos los productos de la categoria `monitors` quedan marcados como inactivos.

### Advertencia

Antes de usar `updateMany`, conviene revisar bien el filtro porque el alcance puede ser grande.

## 11. Borrar un usuario de prueba con `deleteOne`

### Que problema resuelve

En desarrollo o testing suele hacer falta limpiar un registro puntual.

### Comando en `mongosh`

```js
db.users.deleteOne({ email: "lucia@example.com" })
```

### Resultado esperado

Se elimina un solo documento si existe coincidencia.

### Idea clave

`deleteOne` es mas seguro cuando la intencion es borrar un registro especifico.

## 12. Limpiar ordenes canceladas con `deleteMany`

### Que problema resuelve

Un entorno de pruebas puede necesitar remover pedidos cancelados y descartables.

### Comando en `mongosh`

```js
db.orders.deleteMany({ status: "CANCELLED" })
```

### Resultado esperado

Se eliminan todas las ordenes canceladas que coincidan con el filtro.

### Criterio backend

En produccion muchas veces se prefiere una baja logica. Aun asi, entender `deleteMany` sigue siendo importante para mantenimiento, pruebas y administracion.

## 13. Flujo corto de un servicio de productos

### Escenario

Un administrador crea un producto, la API lo lista y luego actualiza su precio.

### Secuencia en `mongosh`

```js
db.products.insertOne({
  name: "Webcam HD",
  category: "accessories",
  price: 55000,
  active: true,
  stock: 12
})

db.products.find(
  { active: true, category: "accessories" },
  { _id: 0, name: 1, price: 1 }
).sort({ price: 1 }).limit(10)

db.products.updateOne(
  { name: "Webcam HD" },
  { $set: { price: 52000 } }
)
```

### Aprendizaje

Aqui ya aparecen juntas varias ideas del modulo:

- insercion
- filtro
- proyeccion
- orden
- limite
- actualizacion

Eso se parece mucho mas al trabajo real de un backend que un ejemplo aislado de cada comando.
