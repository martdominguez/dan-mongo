# Ejemplos

## 1. Mismo problema, distinto modelo

### Escenario

Una API de ecommerce necesita devolver el detalle de una orden.

La respuesta suele incluir:

- datos basicos del comprador
- items comprados
- direccion de entrega
- total
- estado

## 2. Modelo con datos importantes embebidos

### Documento ejemplo

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "userSnapshot": {
    "_id": "u1001",
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
      "productId": "p10",
      "productName": "Mouse Bluetooth",
      "quantity": 1,
      "unitPrice": 25000
    },
    {
      "productId": "p11",
      "productName": "Teclado Mecanico",
      "quantity": 1,
      "unitPrice": 78000
    }
  ],
  "total": 103000,
  "createdAt": { "$date": "2026-04-15T10:30:00Z" }
}
```

### Que problema resuelve

Permite que `GET /api/orders/ord-9001` encuentre casi toda la informacion principal en una sola lectura.

### Por que este modelo puede ser bueno

Porque una orden suele necesitar conservar el contexto historico de la compra. Si despues cambia el nombre del producto o el email del usuario, la orden no deberia perder lo que representaba en ese momento.

### Trade-off

Hay duplicacion de algunos datos. Aun asi, esa duplicacion puede estar justificada porque simplifica la lectura y preserva contexto historico.

### Buena practica backend

En muchos backends conviene combinar ambas ideas:

- una clave operativa estable como `userId` para filtrar, indexar o relacionar
- campos operativos como `status` y `createdAt` al nivel principal
- un snapshot embebido como `userSnapshot` para conservar contexto historico de la compra

Esa mezcla aparece mucho en servicios reales porque ayuda tanto a la lectura del detalle como a consultas operativas como historial de ordenes por usuario.

## 3. Modelo con referencias fuertes

### Documento ejemplo en `orders`

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "shippingAddressId": "addr-15",
  "itemRefs": [
    { "productId": "p10", "quantity": 1 },
    { "productId": "p11", "quantity": 1 }
  ],
  "total": 103000,
  "createdAt": { "$date": "2026-04-15T10:30:00Z" }
}
```

### Documentos relacionados

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com"
}
```

```json
{
  "_id": "p10",
  "name": "Mouse Bluetooth",
  "price": 25000
}
```

```json
{
  "_id": "p11",
  "name": "Teclado Mecanico",
  "price": 78000
}
```

### Que problema resuelve

Reduce duplicacion y deja cada entidad con vida propia clara.

### Trade-off

El backend necesita mas trabajo para reconstruir la respuesta completa de la orden. Si esa lectura es muy frecuente, este diseño puede quedarse corto para el caso principal.

## 4. Misma situacion, dos decisiones posibles

### Criterio backend

Si tu sistema:

- consulta ordenes completas todo el tiempo
- necesita preservar snapshot historico de compra
- cambia menos seguido de lo que lee

el modelo con embebidos parciales suele ser mas natural.

Si en cambio tu sistema:

- actualiza mucho la informacion relacionada
- necesita reutilizar entidades centrales sin duplicarlas
- consulta las partes de manera mas independiente

puede inclinarse hacia referencias.

## 5. Relacion uno a uno: usuario y preferencias

### Modelo embebido

```json
{
  "_id": "u2001",
  "name": "Lucia Gomez",
  "email": "lucia@example.com",
  "preferences": {
    "language": "es",
    "notifications": true,
    "theme": "light"
  }
}
```

### Por que suele ser buena idea

Las preferencias normalmente se leen junto con el usuario y no suelen tener valor por separado como coleccion independiente.

### Trade-off

Muy bajo. Este es un caso donde embeder suele ser la opcion mas simple y razonable.

## 6. Relacion uno a muchos: usuario y direcciones

### Modelo embebido

```json
{
  "_id": "u2001",
  "name": "Lucia Gomez",
  "email": "lucia@example.com",
  "addresses": [
    {
      "type": "home",
      "street": "Av. Colon 1200",
      "city": "Cordoba",
      "zipCode": "5000"
    },
    {
      "type": "office",
      "street": "Dean Funes 230",
      "city": "Cordoba",
      "zipCode": "5000"
    }
  ]
}
```

### Por que puede funcionar bien

Si un usuario tiene pocas direcciones y la API de perfil suele devolverlas juntas, embeder simplifica mucho el acceso.

### Que mirar antes de elegirlo

El punto importante no es solo que "pertenecen al usuario". Lo importante es que la cantidad es acotada y que suelen viajar juntas en la respuesta.

## 7. Relacion uno a muchos: usuario y ordenes

### Modelo recomendado con referencia

`users`

```json
{
  "_id": "u2001",
  "name": "Lucia Gomez",
  "email": "lucia@example.com"
}
```

`orders`

```json
{
  "_id": "ord-9100",
  "userId": "u2001",
  "status": "CREATED",
  "total": 55000
}
```

### Por que no conviene embeder todas las ordenes dentro del usuario

Porque un usuario puede tener muchisimas ordenes a lo largo del tiempo. Ese arreglo podria crecer sin limite y volver torpe la lectura y la escritura del documento `users`.

### Idea clave

Uno a muchos no implica automaticamente embebido. Tambien importa cuanto puede crecer la relacion.

## 8. Muchos a muchos: productos y categorias

### Modelo simple basado en ids

```json
{
  "_id": "p30",
  "name": "Notebook Pro 14",
  "price": 1850000,
  "categoryIds": ["cat-notebooks", "cat-premium"]
}
```

### Por que puede alcanzar

Si la consulta principal parte desde productos, guardar los ids de categoria en el mismo documento puede ser suficiente y mas simple que una estructura intermedia heredada de SQL.

### Trade-off

Si la relacion necesita muchos atributos propios, tal vez haya que modelarla como entidad separada.

## 9. Write-heavy vs read-heavy

### Escenario read-heavy

Un endpoint de detalle de orden se consulta miles de veces por dia y casi siempre necesita items, total y direccion.

Modelo que prioriza lectura:

```json
{
  "_id": "ord-9200",
  "userId": "u3001",
  "status": "SHIPPED",
  "shippingAddress": {
    "street": "Belgrano 450",
    "city": "Rosario"
  },
  "items": [
    {
      "productId": "p50",
      "productName": "Monitor 27",
      "quantity": 1,
      "unitPrice": 320000
    }
  ],
  "total": 320000
}
```

### Por que puede ser buena decision

La lectura importante queda muy directa.

### Escenario write-heavy

Ahora piensa en `products`, donde el stock y algunos datos operativos cambian seguido.

Modelo prudente:

```json
{
  "_id": "p50",
  "name": "Monitor 27",
  "category": "monitors",
  "price": 320000,
  "stock": 4,
  "active": true
}
```

### Por que aqui conviene mas cuidado con la duplicacion

Si campos muy cambiantes se copian en demasiados lugares, mantener consistencia se vuelve mas caro para el backend.

## 10. Bad design vs good design

### Mal diseño: sobre-normalizacion

`orders`

```json
{
  "_id": "ord-9300",
  "userId": "u4001",
  "shippingAddressId": "addr-44",
  "itemIds": ["oi-1", "oi-2"],
  "pricingId": "price-9300",
  "statusId": "st-paid"
}
```

### Por que es un mal diseño para este caso

Una lectura comun de una orden obliga al backend a recorrer demasiadas colecciones para armar una respuesta que conceptualmente era una sola unidad.

### Buen diseño: agregado de orden

```json
{
  "_id": "ord-9300",
  "userId": "u4001",
  "status": "PAID",
  "shippingAddress": {
    "street": "Mitre 88",
    "city": "Mendoza"
  },
  "items": [
    {
      "productId": "p80",
      "productName": "Webcam HD",
      "quantity": 1,
      "unitPrice": 52000
    },
    {
      "productId": "p81",
      "productName": "Microfono USB",
      "quantity": 1,
      "unitPrice": 91000
    }
  ],
  "total": 143000
}
```

### Por que es mejor

La estructura responde mejor a la consulta principal del backend y evita fragmentar artificialmente una entidad que suele viajar unida.

## 11. Mal diseño: sobre-embedding

### Documento problematico

```json
{
  "_id": "u5001",
  "name": "Carlos Ruiz",
  "email": "carlos@example.com",
  "orders": [
    { "_id": "ord-1", "total": 1000 },
    { "_id": "ord-2", "total": 2500 },
    { "_id": "ord-3", "total": 1800 }
  ],
  "notifications": [
    { "_id": "n1", "message": "Pago recibido" },
    { "_id": "n2", "message": "Pedido enviado" }
  ],
  "auditEvents": [
    { "_id": "e1", "type": "LOGIN" },
    { "_id": "e2", "type": "PASSWORD_CHANGE" }
  ]
}
```

### Por que es peligroso

Al principio parece comodo porque todo esta junto. Pero si `orders`, `notifications` y `auditEvents` crecen mucho, el documento del usuario deja de representar un perfil y pasa a contener historiales sin limite.

### Mejor enfoque

Mantener en `users` solo la informacion estable y dejar historiales o colecciones crecientes en documentos separados relacionados por `userId`.
