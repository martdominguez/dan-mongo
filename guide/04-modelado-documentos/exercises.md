# Ejercicios

## 1. Rediseñar un esquema relacional simple

### Consigna

Tienes este modelo relacional:

- `users`
- `orders`
- `order_items`
- `addresses`

Piensa en un endpoint `GET /api/orders/{id}` que casi siempre devuelve:

- datos basicos del usuario
- items
- direccion de entrega
- total
- estado

Propone un posible documento `orders` en MongoDB y explica por que elegiste esa estructura.

## 2. Decidir entre embebido y referencia

### Consigna

Para cada caso, indica si embederias o referenciarias. Justifica en 2 o 3 lineas.

1. preferencias de un usuario
2. ordenes historicas de un usuario
3. direccion principal de un usuario
4. catalogo de productos reutilizado por muchas compras

## 3. Detectar errores de modelado

### Consigna

Analiza este documento:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "orders": [
    { "_id": "ord-1", "total": 1000 },
    { "_id": "ord-2", "total": 2000 },
    { "_id": "ord-3", "total": 3000 }
  ],
  "notifications": [
    { "_id": "n1", "message": "Pago recibido" }
  ],
  "auditEvents": [
    { "_id": "e1", "type": "LOGIN" }
  ]
}
```

Identifica al menos tres problemas potenciales de este diseño y explica por que pueden complicar a un backend real.

## 4. Proponer una mejor estructura

### Consigna

Reescribe el caso del ejercicio anterior con una estructura mas razonable para MongoDB.

### Restriccion

Debes decidir:

- que queda en `users`
- que pasa a otra coleccion
- que campo usarias para relacionar documentos

## 5. Mismo escenario, dos modelos

### Consigna

Modela un caso de `orders` de dos maneras distintas:

1. con datos importantes embebidos
2. con referencias principales

Despues compara ambos modelos y responde:

- cual favorece mas la lectura
- cual reduce mas duplicacion
- en que escenario backend elegirias cada uno

## 6. Ejercicio abierto de diseño: ecommerce

### Consigna

Diseña la estructura general de estas tres colecciones para un ecommerce:

- `users`
- `products`
- `orders`

No hace falta escribir todos los campos posibles. Enfocate en:

- que datos irian embebidos
- que datos irian referenciados
- que lectura principal intentas simplificar con cada decision

## 7. Ejercicio abierto de diseño: API read-heavy

### Consigna

Una API interna consulta miles de veces por dia el detalle de una orden. Casi nunca edita la informacion historica de compras, pero si consulta el detalle completo con mucha frecuencia.

Propon un diseño de `orders` orientado a ese patron y explica por que priorizaste esa estructura.

## 8. Identificar arreglos sin limite

### Consigna

Lee estos casos y marca cuales podrian convertirse en arreglos sin limite si se embeben dentro de un solo documento:

- direcciones favoritas del usuario
- comentarios de un producto muy popular
- historial de logins de 5 años
- items de una orden comun

Despues justifica brevemente por que.

## 9. Elegir segun write-heavy o read-heavy

### Consigna

Tienes dos necesidades:

1. un detalle de orden que se lee muchisimo
2. un stock de producto que cambia seguido

Explica como cambia tu criterio de modelado en cada caso y que riesgo aparece si duplicas demasiado en el segundo escenario.

## 10. Detectar sobre-normalizacion

### Consigna

Imagina una coleccion `orders` que solo guarda:

- `userId`
- `shippingAddressId`
- `itemIds`
- `pricingId`
- `statusId`

Explica por que ese diseño puede ser una mala adaptacion de un modelo relacional y que costo puede introducir en la logica del backend.

## 11. Conectar modelado con consultas futuras

### Consigna

Imagina que modelaste `orders` asi:

```json
{
  "_id": "ord-9500",
  "userId": "u1001",
  "userSnapshot": {
    "name": "Ana Perez",
    "email": "ana@example.com"
  },
  "status": "PAID",
  "createdAt": { "$date": "2026-04-15T10:30:00Z" },
  "items": [
    {
      "productId": "p10",
      "productName": "Mouse Bluetooth",
      "quantity": 1,
      "unitPrice": 25000
    }
  ],
  "total": 25000
}
```

Explica por que tiene sentido que `userId`, `status` y `createdAt` queden en el nivel principal aunque la orden ya guarde un `userSnapshot`.

### Objetivo

Relacionar una decision de modelado con consultas e indices que apareceran despues.
