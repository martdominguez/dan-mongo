# Ejemplos

## 1. Usuario con datos agrupados

### Que problema resuelve

Una API de usuarios suele necesitar devolver perfil, direcciones y preferencias en una sola respuesta.

### Documento ejemplo

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "role": "customer",
  "addresses": [
    {
      "type": "home",
      "city": "Cordoba",
      "street": "San Martin 123"
    }
  ],
  "preferences": {
    "language": "es",
    "notifications": true
  },
  "createdAt": { "$date": "2026-04-14T10:00:00Z" }
}
```

### Lectura backend tipica

Si el endpoint es `GET /api/users/u1001`, este documento ya contiene casi todo lo necesario para responder sin reconstruir la entidad desde varias tablas.

### Idea clave

MongoDB favorece modelar segun el acceso frecuente del backend.

## 2. Pedido con items embebidos

### Que problema resuelve

Una API de pedidos normalmente devuelve el pedido con sus items, total y estado.

### Documento ejemplo

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "currency": "ARS",
  "items": [
    {
      "productId": "p10",
      "name": "Mouse Bluetooth",
      "quantity": 1,
      "price": 25000
    },
    {
      "productId": "p11",
      "name": "Teclado Mecanico",
      "quantity": 1,
      "price": 78000
    }
  ],
  "shippingAddress": {
    "city": "Cordoba",
    "street": "San Martin 123"
  },
  "total": 103000,
  "createdAt": { "$date": "2026-04-14T10:30:00Z" }
}
```

### Consulta simple en `mongosh`

```js
db.orders.findOne({ _id: "ord-9001" })
```

### Resultado esperado

La consulta recupera el pedido completo, incluyendo los items. Para muchos endpoints esto simplifica mucho la lectura.

### Advertencia

No todo debe ir embebido. Si los items o productos necesitan vida propia y actualizaciones independientes muy frecuentes, hay que evaluar mejor el diseño.

## 3. Producto con atributos variables

### Que problema resuelve

En un catalogo real, distintos productos tienen distintos atributos.

### Documento ejemplo

```json
{
  "_id": "p20",
  "name": "Notebook Pro 14",
  "category": "notebooks",
  "price": 1850000,
  "attributes": {
    "ramGb": 16,
    "storageGb": 512,
    "cpu": "Ryzen 7"
  }
}
```

```json
{
  "_id": "p21",
  "name": "Silla Ergonomica",
  "category": "furniture",
  "price": 320000,
  "attributes": {
    "material": "mesh",
    "hasLumbarSupport": true,
    "color": "black"
  }
}
```

### Idea clave

En una base de datos relacional esto puede terminar en muchas columnas opcionales o tablas auxiliares. En MongoDB, el documento tolera mejor esta variacion.

## 4. Comparacion rapida con modelo relacional

### Escenario

Queremos devolver un pedido desde una API.

### En un enfoque relacional

Podriamos tener:

- tabla `orders`
- tabla `order_items`
- tabla `users`
- tabla `addresses`

Para construir una respuesta completa, el backend probablemente necesite joins o varias consultas.

### En un enfoque documental

Podemos guardar el agregado del pedido listo para la lectura principal:

```json
{
  "_id": "ord-9002",
  "userId": "u1002",
  "status": "SHIPPED",
  "items": [
    { "productId": "p30", "quantity": 2, "price": 15000 }
  ],
  "total": 30000
}
```

### Conclusión

La diferencia no es solo tecnica. Es una diferencia de modelado basada en como la aplicacion usa los datos.

## 5. BSON en una situacion real

### Que problema resuelve

Un backend necesita filtrar pedidos por fecha y ordenar por creacion.

### Documento ejemplo

```json
{
  "_id": "ord-9010",
  "status": "CREATED",
  "createdAt": { "$date": "2026-04-14T12:00:00Z" },
  "total": 45000
}
```

### Consulta en `mongosh`

```js
db.orders.find({
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

### Idea clave

Esto funciona bien porque `createdAt` no es un string cualquiera. MongoDB maneja tipos adecuados mediante BSON.

## 6. Caso donde MongoDB no seria la primera opcion

### Escenario

Un sistema financiero necesita:

- operaciones contables entre muchas entidades
- consistencia estricta en cada movimiento
- auditoria muy detallada
- consultas relacionales complejas de forma constante

### Criterio

MongoDB puede participar en partes del ecosistema, pero probablemente no sea la primera opcion para el nucleo contable.

### Aprendizaje

Elegir MongoDB bien tambien implica saber cuando no usarlo.

## 7. Anticipo de uso futuro en backend

Mas adelante, cuando integremos MongoDB desde una aplicacion backend, estos documentos se van a transformar en entidades y consultas reales de negocio.

Por ahora alcanza con quedarnos con esta idea: si el modelo del documento refleja bien la forma en que una API lee y escribe datos, MongoDB puede simplificar mucho el trabajo.
