# Ejemplos

## 1. Filtrar y proyectar ordenes pagadas para una API administrativa

### Contexto

Una API de backoffice necesita listar solo ordenes pagadas y devolver una respuesta compacta con los campos mas utiles para una tabla.

### Documentos de ejemplo

```json
[
  {
    "_id": "ord-1001",
    "userId": "u10",
    "status": "PAID",
    "total": 120000,
    "createdAt": { "$date": "2026-04-10T10:30:00Z" }
  },
  {
    "_id": "ord-1002",
    "userId": "u11",
    "status": "PENDING",
    "total": 45000,
    "createdAt": { "$date": "2026-04-11T12:00:00Z" }
  }
]
```

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  {
    $project: {
      _id: 0,
      orderId: "$_id",
      userId: 1,
      total: 1,
      createdAt: 1
    }
  },
  { $sort: { createdAt: -1 } }
])
```

### Resultado esperado

Devuelve solo las ordenes pagadas, ordenadas por fecha descendente y con una salida mas cercana a lo que podria consumir una tabla administrativa.

### Por que un backend developer lo usaria

Porque muchas veces el endpoint no necesita el documento entero. Necesita una version filtrada y mas clara para la respuesta de API.

## 2. Contar ordenes por estado

### Contexto

Un panel operativo necesita mostrar cuantas ordenes estan en cada estado.

### Documentos de ejemplo

```json
[
  { "_id": "ord-2001", "status": "PENDING", "total": 30000 },
  { "_id": "ord-2002", "status": "PAID", "total": 50000 },
  { "_id": "ord-2003", "status": "PAID", "total": 70000 },
  { "_id": "ord-2004", "status": "CANCELLED", "total": 25000 }
]
```

### Pipeline

```js
db.orders.aggregate([
  {
    $group: {
      _id: "$status",
      totalOrders: { $sum: 1 }
    }
  },
  { $sort: { totalOrders: -1 } }
])
```

### Resultado esperado

Genera un documento por cada estado, con la cantidad de ordenes que pertenecen a ese grupo.

### Por que un backend developer lo usaria

Porque este tipo de conteo aparece mucho en dashboards, widgets de resumen y endpoints administrativos.

## 3. Filtrar antes de agrupar para contar solo tickets abiertos y pendientes

### Contexto

El equipo de soporte quiere un resumen de tickets activos, sin mezclar los tickets ya cerrados.

### Documentos de ejemplo

```json
[
  { "_id": "t-10", "status": "OPEN", "priority": "HIGH" },
  { "_id": "t-11", "status": "PENDING", "priority": "MEDIUM" },
  { "_id": "t-12", "status": "CLOSED", "priority": "LOW" },
  { "_id": "t-13", "status": "OPEN", "priority": "HIGH" }
]
```

### Pipeline

```js
db.supportTickets.aggregate([
  {
    $match: {
      status: { $in: ["OPEN", "PENDING"] }
    }
  },
  {
    $group: {
      _id: "$status",
      totalTickets: { $sum: 1 }
    }
  },
  { $sort: { _id: 1 } }
])
```

### Resultado esperado

Cuenta solo los tickets que siguen activos en la operacion diaria.

### Por que un backend developer lo usaria

Porque filtrar antes de agrupar evita mezclar informacion irrelevante y hace que la respuesta del endpoint sea mas fiel a la necesidad de negocio.

## 4. Totales por categoria de producto

### Contexto

Un reporte interno necesita saber cuantos productos activos hay por categoria.

### Documentos de ejemplo

```json
[
  { "_id": "p10", "name": "Mouse", "category": "accessories", "active": true },
  { "_id": "p11", "name": "Teclado", "category": "accessories", "active": true },
  { "_id": "p12", "name": "Notebook", "category": "computers", "active": true },
  { "_id": "p13", "name": "Monitor", "category": "monitors", "active": false }
]
```

### Pipeline

```js
db.products.aggregate([
  { $match: { active: true } },
  {
    $group: {
      _id: "$category",
      totalProducts: { $sum: 1 }
    }
  },
  { $sort: { totalProducts: -1, _id: 1 } }
])
```

### Resultado esperado

Devuelve el total de productos activos agrupados por categoria.

### Por que un backend developer lo usaria

Porque un endpoint de administracion o catalogo puede necesitar un resumen por categoria sin transferir todos los productos al servicio.

## 5. Ventas totales por categoria a partir de ordenes

### Contexto

Cada orden ya guarda una categoria principal para el reporte comercial y el equipo quiere saber cuanto se vendio por categoria.

### Documentos de ejemplo

```json
[
  { "_id": "ord-3001", "status": "PAID", "category": "computers", "total": 450000 },
  { "_id": "ord-3002", "status": "PAID", "category": "accessories", "total": 60000 },
  { "_id": "ord-3003", "status": "PAID", "category": "computers", "total": 300000 },
  { "_id": "ord-3004", "status": "CANCELLED", "category": "computers", "total": 200000 }
]
```

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  {
    $group: {
      _id: "$category",
      revenue: { $sum: "$total" },
      totalOrders: { $sum: 1 }
    }
  },
  { $sort: { revenue: -1 } }
])
```

### Resultado esperado

Resume solo ordenes pagadas y calcula tanto facturacion como cantidad de ordenes por categoria.

### Por que un backend developer lo usaria

Porque es un patron tipico para reportes de negocio y endpoints internos que muestran KPIs simples.

## 6. Resumen mensual de ordenes pagadas

### Contexto

Un dashboard financiero necesita una vista mensual de facturacion.

### Documentos de ejemplo

```json
[
  {
    "_id": "ord-4001",
    "status": "PAID",
    "total": 120000,
    "createdAt": { "$date": "2026-01-15T09:00:00Z" }
  },
  {
    "_id": "ord-4002",
    "status": "PAID",
    "total": 80000,
    "createdAt": { "$date": "2026-01-28T18:00:00Z" }
  },
  {
    "_id": "ord-4003",
    "status": "PAID",
    "total": 150000,
    "createdAt": { "$date": "2026-02-10T11:00:00Z" }
  }
]
```

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  {
    $group: {
      _id: {
        year: { $year: "$createdAt" },
        month: { $month: "$createdAt" }
      },
      totalRevenue: { $sum: "$total" },
      totalOrders: { $sum: 1 }
    }
  },
  { $sort: { "_id.year": 1, "_id.month": 1 } }
])
```

### Resultado esperado

Devuelve un resumen por año y mes, con total facturado y cantidad de ordenes en cada periodo.

### Por que un backend developer lo usaria

Porque los reportes mensuales son una necesidad muy comun en backoffice, analitica operativa y endpoints de reporting.

## 7. Agregar un campo calculado antes de agrupar con `$addFields`

### Contexto

Un equipo quiere clasificar tickets segun si son prioritarios o no, usando la prioridad ya guardada en el documento.

### Documentos de ejemplo

```json
[
  { "_id": "t-20", "status": "OPEN", "priority": "HIGH" },
  { "_id": "t-21", "status": "PENDING", "priority": "LOW" },
  { "_id": "t-22", "status": "OPEN", "priority": "HIGH" }
]
```

### Pipeline

```js
db.supportTickets.aggregate([
  {
    $addFields: {
      urgentLabel: {
        $cond: [{ $eq: ["$priority", "HIGH"] }, "URGENT", "NORMAL"]
      }
    }
  },
  {
    $group: {
      _id: "$urgentLabel",
      totalTickets: { $sum: 1 }
    }
  },
  { $sort: { totalTickets: -1 } }
])
```

### Resultado esperado

Genera un campo temporal llamado `urgentLabel` y luego cuenta tickets por esa clasificacion.

### Por que un backend developer lo usaria

Porque a veces conviene enriquecer el documento dentro del pipeline para devolver una salida mas cercana al lenguaje del negocio.

## 8. Contar usuarios activos con `$count`

### Contexto

Una API interna necesita un unico numero: cuantos usuarios siguen activos.

### Documentos de ejemplo

```json
[
  { "_id": "u1", "email": "ana@example.com", "active": true },
  { "_id": "u2", "email": "leo@example.com", "active": false },
  { "_id": "u3", "email": "mara@example.com", "active": true }
]
```

### Pipeline

```js
db.users.aggregate([
  { $match: { active: true } },
  { $count: "totalActiveUsers" }
])
```

### Resultado esperado

Devuelve un solo documento con la cantidad total de usuarios activos.

### Por que un backend developer lo usaria

Porque algunos endpoints o widgets necesitan un unico total y no una agrupacion por categorias.

## 9. Aplanar arrays de items con `$unwind`

### Contexto

Una orden contiene varios items y el equipo quiere analizar productos vendidos por separado.

### Documento de ejemplo

```json
{
  "_id": "ord-5001",
  "status": "PAID",
  "items": [
    { "productId": "p10", "name": "Mouse", "quantity": 2 },
    { "productId": "p20", "name": "Teclado", "quantity": 1 }
  ]
}
```

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  { $unwind: "$items" },
  {
    $project: {
      _id: 0,
      orderId: "$_id",
      productId: "$items.productId",
      productName: "$items.name",
      quantity: "$items.quantity"
    }
  }
])
```

### Resultado esperado

Genera una salida donde cada item de la orden aparece como una fila independiente.

### Por que un backend developer lo usaria

Porque cuando los datos vienen en arrays, muchas veces la API necesita tratarlos como elementos individuales para analizarlos o exponerlos mejor.

## 10. Productos mas vendidos usando `$unwind` + `$group` + `$sort`

### Contexto

El equipo de ecommerce quiere ver que productos acumularon mas unidades vendidas.

### Documentos de ejemplo

```json
[
  {
    "_id": "ord-6001",
    "status": "PAID",
    "items": [
      { "productId": "p10", "name": "Mouse", "quantity": 2 },
      { "productId": "p20", "name": "Teclado", "quantity": 1 }
    ]
  },
  {
    "_id": "ord-6002",
    "status": "PAID",
    "items": [
      { "productId": "p10", "name": "Mouse", "quantity": 1 }
    ]
  }
]
```

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  { $unwind: "$items" },
  {
    $group: {
      _id: "$items.productId",
      productName: { $first: "$items.name" },
      totalUnits: { $sum: "$items.quantity" }
    }
  },
  { $sort: { totalUnits: -1 } },
  { $limit: 5 }
])
```

### Resultado esperado

Devuelve un ranking de productos por cantidad total de unidades vendidas.

### Por que un backend developer lo usaria

Porque este tipo de agregacion sirve para endpoints de analitica comercial, paneles internos y decisiones de catalogo.

## 11. Paginacion simple sobre resultados agregados con `$skip` y `$limit`

### Contexto

Un panel administrativo muestra usuarios con cantidad de ordenes y quiere paginar el ranking.

### Pipeline

```js
db.orders.aggregate([
  {
    $group: {
      _id: "$userId",
      totalOrders: { $sum: 1 },
      totalSpent: { $sum: "$total" }
    }
  },
  { $sort: { totalSpent: -1 } },
  { $skip: 10 },
  { $limit: 10 }
])
```

### Resultado esperado

Omite los primeros 10 resultados del ranking y devuelve los 10 siguientes.

### Por que un backend developer lo usaria

Porque algunos reportes paginados trabajan sobre resultados ya agregados y no sobre documentos crudos.

## 12. Pipeline corto pero bien orientado para una API de resumen

### Contexto

Un endpoint `GET /api/orders/summary/paid` solo necesita dos datos:

- cantidad de ordenes pagadas
- monto total facturado

### Pipeline

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  {
    $group: {
      _id: null,
      totalOrders: { $sum: 1 },
      totalRevenue: { $sum: "$total" }
    }
  },
  {
    $project: {
      _id: 0,
      totalOrders: 1,
      totalRevenue: 1
    }
  }
])
```

### Resultado esperado

Devuelve un unico documento con los dos valores de resumen.

### Por que un backend developer lo usaria

Porque es un ejemplo claro de agregacion que ya entrega a la API exactamente la forma de respuesta que el consumidor necesita.
