# Ejercicios

## 1. Elegir entre embedding y referencing

### Consigna

Para cada caso, indica si modelarias con embebido, referencia o una combinacion parcial. Justifica cada decision en 2 o 3 lineas.

1. direccion principal de un usuario
2. historial completo de ordenes de un usuario
3. items de una orden ya pagada
4. categoria principal de un producto
5. comentarios de un producto muy popular

## 2. Redisenar un modelo demasiado relacional

### Consigna

Partes de este esquema inspirado en SQL:

- `users`
- `orders`
- `order_items`
- `addresses`
- `order_status`
- `pricing_snapshots`

La API `GET /api/orders/{id}` necesita devolver:

- resumen del usuario
- direccion de entrega
- items con nombre y precio
- total
- estado

Propone un documento `orders` mas natural para MongoDB y explica que datos dejarias fuera de la orden.

## 3. Detectar riesgos de consistencia

### Consigna

Analiza este documento de `orders`:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "userSummary": {
    "name": "Ana Perez",
    "email": "ana@example.com"
  },
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "categoryName": "Perifericos",
      "quantity": 1,
      "unitPrice": 25000
    }
  ],
  "status": "PAID"
}
```

Identifica al menos cuatro preguntas de consistencia que deberia responder el equipo antes de considerar sano este modelo.

## 4. Proponer una estrategia de actualizacion para datos duplicados

### Consigna

En `products` guardas esta referencia extendida:

```json
{
  "category": {
    "categoryId": "cat-perifericos",
    "name": "Perifericos"
  }
}
```

Explica como actuarias cuando cambia el nombre de la categoria.

### Tu respuesta debe incluir

- cual es la fuente de verdad
- si el cambio debe propagarse
- cuando se propagaria
- que riesgo existe mientras la propagacion no termino

## 5. Elegir entre varias formas de muchos a muchos

### Consigna

Tienes una plataforma de cursos con:

- estudiantes
- cursos
- inscripciones

Cada inscripcion necesita guardar:

- fecha de alta
- estado
- progreso

Compara estas opciones:

1. `studentIds` dentro de `courses`
2. `courseIds` dentro de `students`
3. coleccion `enrollments`

Indica cual elegirias como estructura principal y por que.

## 6. Escenario abierto: no hay una unica respuesta perfecta

### Consigna

Una empresa tiene una API de catalogo donde el detalle de producto se consulta muchisimo y necesita mostrar:

- categoria visible
- tags
- dos reviews destacadas
- conteo total de reviews

No hace falta que escribas todos los campos. Propone una estructura general y justifica:

- que dejarias embebido
- que dejarias referenciado
- que subset mantendrias en el producto

## 7. Escenario abierto: decisiones de consistencia

### Consigna

Una plataforma B2B muestra ordenes en un panel administrativo. El panel necesita responder rapido y por eso alguien propone duplicar en cada orden:

- nombre del cliente
- email del cliente
- nombre de la empresa

No hay una unica respuesta correcta. Explica:

- que beneficios trae esa decision
- que riesgos agrega
- que campos te preocuparia mas mantener sincronizados
- en que casos dejarias alguno como historico

## 8. Identificar arreglos que pueden crecer sin control

### Consigna

Marca cuales de estos casos te preocuparian por crecimiento no acotado si se embeben dentro de un solo documento. Despues justifica cada respuesta.

- direcciones favoritas de usuario
- log de auditoria de 4 anos
- comentarios de un producto viral
- items de una orden tipica
- tags de un producto normal

## 9. Redisenar una relacion uno a muchos

### Consigna

Partes de este modelo:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "orders": [
    { "_id": "ord-1", "total": 1200, "status": "PAID" },
    { "_id": "ord-2", "total": 5500, "status": "SHIPPED" }
  ]
}
```

Explica por que podria volverse problematico en un backend real y propone una alternativa mas mantenible.

## 10. Evaluar duplicacion deliberada

### Consigna

En una orden quieres guardar:

- `productId`
- `productName`
- `brandName`
- `categoryName`
- `unitPrice`
- `currentStock`

Decide cuales de esos campos te parecen razonables como snapshot historico y cuales evitarias duplicar dentro de la orden. Justifica cada eleccion.

## 11. Diseñar una politica simple de sincronizacion

### Consigna

Imagina que tu equipo acepta consistencia eventual para algunos campos descriptivos duplicados.

Propone una politica simple y clara para documentar esa decision. Incluye:

- que tipo de campos entrarian en esa categoria
- cuanto desalineamiento temporal seria tolerable
- quien deberia ejecutar la sincronizacion
- como evitar que el equipo confunda snapshot historico con dato sincronizable
