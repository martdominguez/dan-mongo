# Ejercicios

## 1. Insertar un usuario para una API

### Consigna

Escribe un `insertOne` sobre la coleccion `users` para guardar un usuario con estos campos:

- `name`
- `email`
- `role`
- `active`

### Contexto

Imagina que ese documento se crea desde `POST /api/users`.

## 2. Cargar varios productos juntos

### Consigna

Escribe un `insertMany` sobre `products` para insertar tres productos reales de un ecommerce.

### Restriccion

Cada documento debe incluir:

- `name`
- `category`
- `price`
- `active`

### Pista

Usa categorias realistas como `accessories`, `monitors` o `notebooks`.

## 3. Buscar un pedido puntual

### Consigna

Escribe una consulta con `findOne` para recuperar una orden con `_id` igual a `"ord-1001"`.

### Objetivo

Relacionar una consulta puntual con un endpoint de detalle como `GET /api/orders/ord-1001`.

## 4. Filtrar productos activos

### Consigna

Escribe una consulta con `find` para traer solo productos activos.

Despues responde en una linea:

Que necesidad de una API podria resolverse con esa consulta.

## 5. Filtrar con una condicion de negocio simple

### Consigna

Escribe una consulta para traer productos:

- de la categoria `accessories`
- con `price` menor o igual a `80000`
- activos

### Objetivo

Practicar filtros combinados como los que aparecen en una API de catalogo.

## 6. Aplicar una proyeccion simple

### Consigna

Sobre la coleccion `products`, escribe una consulta que devuelva solo:

- `name`
- `price`
- `category`

Excluye `_id`.

### Contexto

Piensa en una respuesta de listado para frontend o para otro servicio.

## 7. Ordenar y limitar resultados

### Consigna

Escribe una consulta sobre `orders` que:

1. filtre por `userId: "u1001"`
2. ordene por `createdAt` descendente
3. limite a 5 resultados

### Pista

Piensa en un endpoint de historial como `GET /api/orders/history?userId=u1001`.

## 8. Actualizar el estado de una orden

### Consigna

Escribe un `updateOne` para cambiar el estado de la orden `"ord-1001"` a `PAID`.

### Objetivo

Pensar una actualizacion puntual como parte de un flujo de checkout o confirmacion de pago.

## 9. Actualizar varios documentos

### Consigna

Escribe un `updateMany` para marcar como inactivos todos los productos de la categoria `monitors`.

Despues explica en 2 lineas que riesgo hay si el filtro esta mal armado.

## 10. Borrar un documento puntual

### Consigna

Escribe un `deleteOne` para borrar un usuario de prueba con email `test-user@example.com`.

### Contexto

Imagina que estas limpiando datos de testing en desarrollo.

## 11. Borrar varios documentos

### Consigna

Escribe un `deleteMany` para eliminar pedidos con `status: "CANCELLED"`.

### Pregunta corta

En que contexto de desarrollo o mantenimiento podria tener sentido esta operacion.

## 12. Secuencia CRUD completa

### Consigna

Escribe una secuencia minima de comandos para este flujo:

1. crear un producto
2. consultarlo por nombre
3. actualizar su precio
4. borrarlo

### Restriccion

Usa la coleccion `products` y nombres de campos realistas.

### Objetivo

Ver CRUD como ciclo de vida del dato y no como comandos aislados.
