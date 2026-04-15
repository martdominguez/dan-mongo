# Ejercicios

## 1. Definir validacion minima para `orders`

### Consigna

Diseña una regla introductoria de validacion para una coleccion `orders` que exija:

- `userId`
- `status`
- `items`
- `total`
- `createdAt`

Ademas:

- `status` solo puede ser `PENDING`, `PAID` o `CANCELLED`
- `total` no puede ser negativo

Escribe la estructura general del `validator` con `$jsonSchema`.

## 2. Detectar documentos invalidos

### Consigna

Analiza estos tres documentos y marca cuales deberian ser rechazados por una validacion razonable. Justifica cada respuesta.

```json
{
  "_id": "ord-1",
  "userId": "u1",
  "status": "PENDING",
  "items": [],
  "total": 1000,
  "createdAt": { "$date": "2026-04-15T10:00:00Z" }
}
```

```json
{
  "_id": "ord-2",
  "userId": "u2",
  "status": "WAITING",
  "items": [],
  "total": 1000,
  "createdAt": { "$date": "2026-04-15T10:00:00Z" }
}
```

```json
{
  "_id": "ord-3",
  "userId": "u3",
  "status": "PAID",
  "items": [],
  "total": "1000",
  "createdAt": "2026-04-15"
}
```

## 3. Separar validacion estructural de reglas de negocio

### Consigna

Para cada regla, indica si la pondrias principalmente en validacion de coleccion, en la aplicacion, o en ambas capas. Justifica brevemente.

1. `total` debe ser numerico
2. `status` debe pertenecer a un conjunto acotado
3. el usuario debe existir
4. la suma de los items debe coincidir con el total recalculado
5. una orden no puede cancelarse si ya fue enviada

## 4. Identificar cuando una transaccion es apropiada

### Consigna

Evalua estos casos e indica en cuales una transaccion tiene sentido y en cuales no. Explica por que.

1. insertar un pago en `payments` y actualizar `orders.status` a `PAID`
2. actualizar `status`, `total` y `shippingAddress` dentro del mismo documento `orders`
3. modificar una orden y escribir un log en `audit_logs`
4. descontar saldo de una wallet y acreditar saldo en otra

## 5. Identificar cuando una transaccion es overkill

### Consigna

Un equipo propone usar transacciones para:

- cada `updateOne`
- cada cambio de estado de una orden
- cada escritura de log
- cada ajuste de perfil de usuario

Explica al menos tres razones por las que esa estrategia puede ser innecesaria o contraproducente en MongoDB.

## 6. Backend scenario: checkout y modelado

### Consigna

Tienes esta estructura:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PENDING",
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "quantity": 2,
      "unitPrice": 25000
    }
  ],
  "shippingAddress": {
    "street": "San Martin 123",
    "city": "Cordoba"
  },
  "total": 50000
}
```

La API de checkout necesita actualizar direccion, total y estado final antes del pago.

Responde:

- usarias transaccion multi-documento o no
- que propiedad de MongoDB influye en esa decision
- que advertencia le darias al equipo si alguien propone abrir una transaccion por defecto

## 7. Backend scenario: pago y orden

### Consigna

Una API marca una orden como pagada y tambien inserta un documento en `payments`. Si el pago existe sin orden pagada, el negocio queda inconsistente.

Explica:

- por que este caso se parece mas a una transaccion razonable
- que papel cumple la sesion
- que costo o complejidad extra introduces al elegir este camino

## 8. Detectar decisiones de seguridad riesgosas

### Consigna

Marca los riesgos tecnicos de estas decisiones:

1. usar el mismo usuario administrador para `catalog-api`, `orders-api` y `reporting-api`
2. guardar credenciales de produccion en el repositorio
3. permitir que una API publica tenga permisos para borrar colecciones
4. compartir la misma credencial entre testing y produccion

## 9. Diseñar acceso seguro para un servicio backend

### Consigna

Debes proponer una estrategia de acceso para un servicio `reporting-api` que solo necesita leer:

- `orders`
- `payments`

Tu propuesta debe incluir:

- tipo de permisos generales
- por que no deberia reutilizar la credencial de la API operativa
- que riesgo reduces aplicando menor privilegio

## 10. Evitar operaciones destructivas accidentales

### Consigna

Un script de mantenimiento ejecuta estas instrucciones:

```js
db.orders.updateMany({}, { $set: { archived: true } })
db.orders.deleteMany({})
```

Explica por que son peligrosas y propone al menos cuatro medidas practicas para reducir el riesgo en un entorno real.

## 11. Analizar una confusion frecuente

### Consigna

Un desarrollador afirma:

"Si ya validamos en Spring Boot, no hace falta validar nada en MongoDB. Y si usamos transacciones, entonces no importa tanto el modelado."

Identifica al menos cuatro problemas conceptuales en esa afirmacion.
