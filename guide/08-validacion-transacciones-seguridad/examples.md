# Ejemplos

## 1. Regla de validacion para una coleccion `orders`

### Escenario

Una API de ecommerce necesita evitar pedidos incompletos o con tipos inconsistentes antes de que lleguen a reportes, pagos o soporte.

### Regla introductoria

```js
db.createCollection("orders", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["userId", "status", "items", "total", "createdAt"],
      properties: {
        userId: {
          bsonType: "string"
        },
        status: {
          bsonType: "string",
          enum: ["PENDING", "PAID", "CANCELLED"]
        },
        items: {
          bsonType: "array"
        },
        total: {
          bsonType: "number",
          minimum: 0
        },
        createdAt: {
          bsonType: "date"
        }
      }
    }
  }
})
```

### Que protege

- obliga a que existan los campos centrales del pedido
- evita estados improvisados
- evita `total` negativo
- deja documentada la forma minima aceptada por la coleccion

## 2. Documento valido vs documento invalido

### Documento valido

```json
{
  "_id": "ord-1001",
  "userId": "u1001",
  "status": "PENDING",
  "items": [
    {
      "productId": "prod-10",
      "quantity": 2,
      "unitPrice": 25000
    }
  ],
  "total": 50000,
  "createdAt": { "$date": "2026-04-15T10:00:00Z" }
}
```

### Por que pasaria la validacion

- tiene todos los campos requeridos
- `status` pertenece al conjunto permitido
- `total` es numerico y no negativo
- `createdAt` tiene tipo fecha

### Documento invalido

```json
{
  "_id": "ord-1002",
  "userId": "u1002",
  "status": "WAITING_PAYMENT",
  "items": [],
  "total": "50000",
  "createdAt": "2026-04-15"
}
```

### Problemas del documento invalido

- `status` no coincide con los valores permitidos
- `total` llega como string
- `createdAt` llega como string y no como fecha BSON

### Intencion del ejemplo

Mostrar que la validacion protege estructura minima. No decide todavia si `items` deberia tener al menos un elemento o si el total coincide con el detalle.

## 3. Ajustar validacion de una coleccion existente

### Escenario

La coleccion `orders` ya existe, pero ahora el equipo quiere endurecer la calidad del dato.

### Ejemplo con `collMod`

```js
db.runCommand({
  collMod: "orders",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["userId", "status", "items", "total"],
      properties: {
        userId: { bsonType: "string" },
        status: {
          bsonType: "string",
          enum: ["PENDING", "PAID", "CANCELLED"]
        },
        items: { bsonType: "array" },
        total: { bsonType: "number", minimum: 0 }
      }
    }
  }
})
```

### Criterio practico

Antes de endurecer validacion en una coleccion existente, conviene revisar si ya hay documentos viejos que no cumplen las nuevas reglas.

## 4. Transaccion simple: registrar pago y marcar orden como pagada

### Escenario

Una API registra un pago aprobado y luego actualiza la orden. El negocio no quiere tolerar que exista uno sin el otro.

### Ejemplo conceptual en `mongosh`

```js
const session = db.getMongo().startSession()
const shopDb = session.getDatabase("shop")

try {
  session.startTransaction()

  shopDb.payments.insertOne({
    _id: "pay-9001",
    orderId: "ord-9001",
    amount: 125000,
    status: "APPROVED",
    createdAt: new Date()
  })

  shopDb.orders.updateOne(
    { _id: "ord-9001", status: "PENDING" },
    {
      $set: {
        status: "PAID",
        paidAt: new Date()
      }
    }
  )

  session.commitTransaction()
} catch (error) {
  session.abortTransaction()
  throw error
} finally {
  session.endSession()
}
```

### Que enseña este ejemplo

- la transaccion vive dentro de una sesion
- el flujo coordina dos colecciones
- si algo falla, no queremos estado intermedio

## 5. Caso backend donde conviene evitar una transaccion innecesaria

### Escenario

Una orden guarda `status`, `items`, `shippingAddress` y `total` dentro del mismo documento. El checkout necesita confirmar la direccion y recalcular el total final.

### Operacion suficiente sin transaccion multi-documento

```js
db.orders.updateOne(
  { _id: "ord-9002", status: "PENDING" },
  {
    $set: {
      shippingAddress: {
        street: "San Martin 123",
        city: "Cordoba",
        zipCode: "5000"
      },
      total: 128000,
      status: "READY_FOR_PAYMENT",
      updatedAt: new Date()
    }
  }
)
```

### Por que no hace falta transaccion

- todo cambia dentro de un solo documento
- MongoDB ya garantiza atomicidad a ese nivel
- agregar una transaccion solo complica el flujo sin beneficio real

### Intencion de backend

Primero revisar si el modelo ya agrupa lo que cambia junto. Solo despues evaluar una transaccion.

## 6. Caso backend donde una transaccion seria overkill

### Escenario

Al cancelar una orden, el sistema tambien quiere escribir un registro en `audit_logs`.

### Decision recomendada

Actualizar la orden como operacion principal y tratar el log como una escritura secundaria que puede reintentarse si falla.

```js
db.orders.updateOne(
  { _id: "ord-9003", status: "PENDING" },
  {
    $set: {
      status: "CANCELLED",
      cancelledAt: new Date()
    }
  }
)

db.audit_logs.insertOne({
  entityType: "ORDER",
  entityId: "ord-9003",
  action: "CANCELLED",
  createdAt: new Date()
})
```

### Por que puede evitarse la transaccion

- el dato critico es el estado de la orden
- el log es valioso, pero no siempre exige coordinacion fuerte
- si el log falla, puede generarse un reintento o alerta posterior

## 7. Seguridad: aplicar principio de menor privilegio

### Escenario

Tienes dos servicios:

- `catalog-api`, que solo consulta productos
- `orders-api`, que lee y escribe ordenes y pagos

### Mala practica

Conectar ambos con el mismo usuario administrador.

### Mejor enfoque

- `catalog-api`: usuario solo lectura sobre `products`
- `orders-api`: usuario con lectura y escritura sobre `orders` y `payments`

### Intencion

Si `catalog-api` sufre un bug o una filtracion de credenciales, el dano no deberia extenderse a pagos u ordenes.

## 8. Seguridad: evitar operaciones destructivas accidentales

### Escenario

Un desarrollador quiere limpiar ordenes de testing y ejecuta un borrado demasiado amplio.

### Riesgo

```js
db.orders.deleteMany({})
```

Ese comando elimina todo el contenido de la coleccion.

### Enfoque mas seguro

```js
db.orders.deleteMany({
  environment: "test",
  createdBy: "seed-script"
})
```

### Que enseña este ejemplo

- las operaciones destructivas deben tener filtros claros
- un usuario sobre-privilegiado amplifica mucho el riesgo
- produccion necesita barreras y criterio, no solo comandos correctos

## 9. Validacion de aplicacion mas validacion de coleccion

### Escenario

Un backend Spring Boot recibe un pedido y aplica reglas de negocio antes de persistirlo.

### Validaciones de aplicacion razonables

- verificar que el usuario exista
- confirmar que hay stock suficiente
- recalcular el total servidor-side
- rechazar una orden con cupon vencido

### Validaciones razonables de coleccion

- `userId` obligatorio
- `status` tipado y controlado
- `total` numerico y no negativo
- `createdAt` como fecha

### Idea clave

La aplicacion entiende el contexto de negocio. La base protege estructura minima y calidad del dato persistido.

## 10. Ejemplo de uso seguro desde backend

### Escenario

Un servicio Spring Boot de reportes solo necesita leer ordenes cerradas para generar dashboards internos.

### Decisiones sanas

- usar una credencial separada de la API operativa
- dar permisos solo de lectura
- limitar el acceso a las colecciones necesarias
- no reutilizar el usuario que crea o borra datos

### Beneficio

Un servicio de reportes no deberia poder modificar el estado de una orden ni borrar documentos, aunque tenga un bug o una mala configuracion.
