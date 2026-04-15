# Ejemplos

## 1. Relacion uno a uno: usuario con direccion principal

### Escenario

Una API de perfil necesita devolver siempre los datos basicos del usuario junto con su direccion principal.

### Modelo embebido

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "mainAddress": {
    "street": "San Martin 123",
    "city": "Cordoba",
    "zipCode": "5000",
    "country": "AR"
  },
  "updatedAt": { "$date": "2026-04-15T10:00:00Z" }
}
```

### Por que tiene sentido

- la direccion principal suele viajar con el perfil
- no necesita vida propia fuerte
- la actualizacion ocurre dentro del flujo de usuario

### Actualizacion tipica

```js
db.users.updateOne(
  { _id: "u1001" },
  {
    $set: {
      "mainAddress.street": "San Martin 125",
      updatedAt: new Date()
    }
  }
)
```

### Lectura backend que simplifica

`GET /api/users/u1001/profile`

## 2. Relacion uno a muchos con embedding: usuario con varias direcciones

### Escenario

Una cuenta de ecommerce permite guardar pocas direcciones favoritas para checkout.

### Modelo

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "addresses": [
    {
      "addressId": "addr-home",
      "label": "Casa",
      "street": "San Martin 123",
      "city": "Cordoba",
      "zipCode": "5000"
    },
    {
      "addressId": "addr-office",
      "label": "Trabajo",
      "street": "Colon 450",
      "city": "Cordoba",
      "zipCode": "5000"
    }
  ]
}
```

### Por que puede ser un buen diseño

- la cantidad esperada es acotada
- se leen juntas en perfil y checkout
- no hace falta una coleccion `addresses` para este caso simple

### Actualizacion puntual de una direccion

```js
db.users.updateOne(
  { _id: "u1001", "addresses.addressId": "addr-office" },
  {
    $set: {
      "addresses.$.street": "Colon 480",
      "addresses.$.zipCode": "5001"
    }
  }
)
```

## 3. Relacion uno a muchos con referencing: usuario y ordenes

### Escenario

La API necesita listar el historial de compras de un usuario, pero un usuario puede tener cientos o miles de ordenes.

### Modelo

`users`

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com"
}
```

`orders`

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "total": 125000,
  "createdAt": { "$date": "2026-04-15T10:30:00Z" }
}
```

### Por que aqui conviene referencia

- las ordenes pueden crecer sin control
- no tiene sentido meter historial completo dentro de `users`
- el backend puede listar ordenes filtrando por `userId`

### Consulta tipica

```js
db.orders.find({ userId: "u1001" }).sort({ createdAt: -1 })
```

### Intencion de modelado

Separa perfil de usuario de historial operativo.

## 4. Relacion uno a muchos con snapshot: orden con order items

### Escenario

Una orden debe mostrar items, nombre del producto y precio pagado, incluso si el catalogo cambia despues.

### Modelo recomendado

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "quantity": 2,
      "unitPrice": 25000
    },
    {
      "productId": "prod-11",
      "productName": "Teclado Mecanico",
      "quantity": 1,
      "unitPrice": 78000
    }
  ],
  "total": 128000,
  "createdAt": { "$date": "2026-04-15T10:30:00Z" }
}
```

### Que combina este modelo

- referencia operativa por `productId`
- datos historicos por `productName` y `unitPrice`

### Por que es util

Una orden es un hecho historico. No quieres recalcularla segun el catalogo actual.

### Lectura backend que simplifica

`GET /api/orders/ord-9001`

## 5. Relacion muchos a muchos pragmatica: estudiantes, cursos e inscripciones

### Escenario

Una plataforma de cursos necesita:

- saber en que cursos esta inscripto un estudiante
- listar estudiantes por curso
- guardar estado de inscripcion y progreso

### Mal atajo

Guardar todo solo en arrays duplicados:

`students`

```json
{
  "_id": "stu-10",
  "name": "Lucia Gomez",
  "courseIds": ["course-java-01", "course-spring-01"]
}
```

`courses`

```json
{
  "_id": "course-java-01",
  "title": "Java Backend",
  "studentIds": ["stu-10", "stu-25"]
}
```

### Problema

La relacion queda duplicada en dos lados y ademas no hay donde guardar:

- `status`
- `enrolledAt`
- `progressPercent`

### Mejora pragmatica

`enrollments`

```json
{
  "_id": "enr-9001",
  "studentId": "stu-10",
  "studentName": "Lucia Gomez",
  "courseId": "course-java-01",
  "courseTitle": "Java Backend",
  "status": "ACTIVE",
  "progressPercent": 35,
  "enrolledAt": { "$date": "2026-04-15T11:00:00Z" }
}
```

### Por que mejora

- la relacion tiene entidad propia
- los atributos de la inscripcion quedan en su lugar natural
- puedes consultar por estudiante o por curso
- la duplicacion descriptiva es acotada y deliberada

### Trade-off

Si cambia `studentName` o `courseTitle`, debes decidir si sincronizas o si lo usas como snapshot operativo.

## 6. Catalogo de productos con categorias y tags

### Escenario

El backend necesita listar productos con:

- nombre
- precio
- categoria visible
- tags para filtros rapidos

### Modelo practico

```json
{
  "_id": "prod-10",
  "name": "Mouse Bluetooth",
  "price": 25000,
  "category": {
    "categoryId": "cat-perifericos",
    "name": "Perifericos"
  },
  "tags": ["bluetooth", "home-office", "logitech"]
}
```

### Que decisiones hay aqui

- `category` usa una referencia extendida con id y nombre
- `tags` se modela como clasificacion liviana dentro del producto

### Por que puede ser razonable

- la UI y la API suelen necesitar mostrar el nombre de categoria sin otra lectura
- los tags suelen usarse como atributo del producto, no como entidad compleja

### Riesgo a vigilar

Si el nombre de categoria cambia, los productos deben sincronizarse si ese campo debe estar alineado.

## 7. Duplicacion que mejora lecturas pero exige cuidado

### Escenario

Un panel administrativo lista ordenes con nombre del usuario sin necesidad de ir a `users` en cada fila.

### Modelo

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "userSummary": {
    "name": "Ana Perez",
    "email": "ana@example.com"
  },
  "status": "PAID",
  "total": 125000
}
```

### Beneficio

El listado de ordenes puede responder con una sola lectura principal por documento.

### Riesgo

Si `userSummary.email` debe reflejar el valor actual, hay que sincronizar cuando cambia `users.email`.

### Decision de backend que debes tomar

El resumen de usuario en la orden:

- es historico
- o debe reflejar el estado actual

No responder eso a tiempo termina generando bugs funcionales y discusiones en el equipo.

## 8. Ejemplo de mal diseño y mejora

### Diseno malo: copiar SQL literalmente

`orders`

```json
{
  "_id": "ord-9900",
  "userId": "u1001",
  "addressId": "addr-1",
  "itemIds": ["item-1", "item-2"],
  "pricingId": "pricing-1",
  "statusId": "status-paid"
}
```

### Por que es un mal diseño

- casi no hay informacion util en la orden
- el backend necesita demasiadas consultas o ensamblado
- la coleccion pierde valor como agregado de negocio

### Mejora orientada a lectura real

```json
{
  "_id": "ord-9900",
  "userId": "u1001",
  "userSummary": {
    "name": "Ana Perez",
    "email": "ana@example.com"
  },
  "status": "PAID",
  "shippingAddress": {
    "street": "San Martin 123",
    "city": "Cordoba",
    "zipCode": "5000"
  },
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "quantity": 1,
      "unitPrice": 25000
    },
    {
      "productId": "prod-11",
      "productName": "Teclado Mecanico",
      "quantity": 1,
      "unitPrice": 78000
    }
  ],
  "total": 103000,
  "createdAt": { "$date": "2026-04-15T10:30:00Z" }
}
```

### Por que mejora

- la orden vuelve a representar una unidad real de negocio
- la lectura principal del backend es mucho mas directa
- los snapshots historicos quedan claros
- la duplicacion se justifica por contexto y no por accidente

## 9. Subset pattern en un producto con reviews

### Escenario

El detalle de producto quiere mostrar algunas reviews destacadas, pero el historial completo vive aparte.

### Modelo

`products`

```json
{
  "_id": "prod-99",
  "name": "Notebook Pro 14",
  "topReviews": [
    {
      "reviewId": "rev-1",
      "rating": 5,
      "title": "Excelente bateria"
    },
    {
      "reviewId": "rev-2",
      "rating": 4,
      "title": "Muy buena pantalla"
    }
  ],
  "reviewsCount": 1824
}
```

`reviews`

```json
{
  "_id": "rev-1",
  "productId": "prod-99",
  "rating": 5,
  "title": "Excelente bateria",
  "comment": "Rinde perfecto para trabajo diario"
}
```

### Que resuelve

- evita embeder miles de reviews
- mantiene un detalle rapido para la lectura principal
- permite una consulta separada para la seccion completa de opiniones

## 10. Actualizacion de dato duplicado con estrategia explicita

### Escenario

Cambias el nombre de la categoria `cat-perifericos` y decides que `products.category.name` debe mantenerse sincronizado.

### Escritura del origen

```js
db.categories.updateOne(
  { _id: "cat-perifericos" },
  { $set: { name: "Perifericos y Accesorios" } }
)
```

### Propagacion a productos

```js
db.products.updateMany(
  { "category.categoryId": "cat-perifericos" },
  { $set: { "category.name": "Perifericos y Accesorios" } }
)
```

### Lo importante no es solo la consulta

Lo importante es que aqui ya existe una politica:

- la fuente de verdad es `categories`
- `products.category.name` es duplicacion util para lectura
- cuando cambia el origen, la aplicacion debe propagar el cambio

Esa claridad evita que la duplicacion se vuelva caotica.
