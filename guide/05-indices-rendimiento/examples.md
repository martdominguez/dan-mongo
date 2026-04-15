# Ejemplos

## 1. Buscar usuarios por email sin indice

### Que problema resuelve

Un servicio de autenticacion o administracion necesita encontrar un usuario por email.

### Consulta

```js
db.users.findOne({ email: "ana@example.com" })
```

### Si no hay indice

MongoDB puede necesitar revisar muchos documentos de `users` hasta encontrar el email correcto.

### Impacto backend

Si esta consulta participa en login, recuperacion de cuenta o validacion de usuario, una coleccion grande puede sumar latencia innecesaria a cada request.

## 2. Buscar usuarios por email con indice

### Indice

```js
db.users.createIndex({ email: 1 })
```

### Consulta

```js
db.users.findOne({ email: "ana@example.com" })
```

### Diferencia conceptual

Ahora MongoDB tiene una estructura auxiliar para ubicar mucho mas rapido el documento correcto.

### Idea clave

Este es uno de los ejemplos mas claros de indice bien elegido:

- campo muy consultado
- busqueda puntual
- impacto directo en tiempo de respuesta de la API

## 3. Filtrar productos activos por categoria sin indice

### Que problema resuelve

Una API de catalogo necesita listar productos activos de una categoria.

### Consulta

```js
db.products.find({ category: "notebooks", active: true })
```

### Si no hay indice

MongoDB puede recorrer gran parte de `products` para devolver solo los documentos que cumplen ambas condiciones.

### Impacto backend

Si ese filtro aparece en listados publicos, la latencia puede crecer a medida que el catalogo aumenta.

## 4. Filtrar productos activos por categoria con indice

### Indice

```js
db.products.createIndex({ category: 1, active: 1 })
```

### Consulta

```js
db.products.find({ category: "notebooks", active: true })
```

### Diferencia conceptual

El indice ayuda a reducir el espacio de busqueda y mejora la respuesta de un endpoint que probablemente se usa mucho.

### Advertencia

Este indice tiene sentido si el catalogo consulta seguido por `category` y `active` juntos. No conviene indexar campos solo por intuicion.

## 5. Buscar ordenes por usuario y fecha sin indice compuesto

### Que problema resuelve

Un endpoint de historial necesita traer ordenes de un usuario desde cierta fecha y ordenarlas por las mas recientes.

### Consulta

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

### Si no hay indice adecuado

MongoDB puede revisar muchas ordenes y luego ordenar mas trabajo del necesario.

### Impacto backend

Esto afecta listados de historial, paneles de usuario y servicios internos que dependen de responder rapido por rango de fechas.

## 6. Buscar ordenes por usuario y fecha con indice compuesto

### Indice

```js
db.orders.createIndex({ userId: 1, createdAt: -1 })
```

### Consulta

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

### Diferencia conceptual

Este indice acompaña mejor el patron real:

- primero se restringe por `userId`
- despues se trabaja sobre `createdAt`
- el orden por fecha tambien forma parte del caso de uso

### Idea clave

Este es el tipo de indice que suele aparecer mucho en backends con consultas por cuenta, historial o actividad.

## 7. Mismo caso, indice mal pensado

### Indice menos alineado

```js
db.orders.createIndex({ createdAt: -1, userId: 1 })
```

### Por que puede ser peor

Los campos son los mismos, pero el orden no acompaña igual de bien la consulta principal cuando el filtro nace desde `userId`.

### Aprendizaje

En indices compuestos no basta con acertar los campos. Tambien hay que acertar el orden.

## 8. Indice malo vs indice bueno en productos

### Escenario

La API casi siempre consulta productos activos por categoria.

### Indice poco util

```js
db.products.createIndex({ active: 1 })
```

### Por que puede ser mala idea

Si casi todos los productos estan activos, ese campo distingue poco. Ademas, la consulta principal del backend no parte solo desde `active`.

### Indice mas alineado al uso real

```js
db.products.createIndex({ category: 1, active: 1 })
```

### Por que es mejor

Porque responde directamente a un filtro de negocio frecuente en el catalogo.

## 9. Crear un indice compuesto para una API administrativa

### Escenario

Un panel administrativo consulta ordenes por:

- `status`
- `createdAt`

### Consulta

```js
db.orders.find({
  status: "PAID",
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

### Posible indice

```js
db.orders.createIndex({ status: 1, createdAt: -1 })
```

### Que problema resuelve

Mejora un listado frecuente de backoffice donde importa traer rapido un subconjunto de ordenes recientes.

### Advertencia

Antes de crear este indice conviene revisar si `status` realmente filtra suficiente y si este patron se usa seguido.

## 10. Introduccion simple a indice de texto

### Escenario

Un catalogo quiere permitir una busqueda basica por nombre o descripcion.

### Indice

```js
db.products.createIndex({ name: "text", description: "text" })
```

### Uso conceptual

Sirve para empezar a resolver busquedas textuales sin entrar todavia en motores externos o estrategias mas avanzadas.

### Limite de este modulo

No vamos a profundizar en ranking, relevancia ni afinado fino. Solo nos interesa entender que este tipo de indice existe y cuando aparece.

## 11. Introduccion simple a indice multikey

### Escenario

Un backend guarda etiquetas de producto en un array.

### Documento

```json
{
  "_id": "p30",
  "name": "Notebook Pro 14",
  "category": "notebooks",
  "tags": ["premium", "trabajo-remoto", "16gb-ram"]
}
```

### Indice

```js
db.products.createIndex({ tags: 1 })
```

### Consulta

```js
db.products.find({ tags: "premium" })
```

### Idea clave

MongoDB tambien puede ayudar a buscar por valores dentro de arrays. Esto aparece seguido en modelos documentales.

## 12. Comparacion final: sin indice vs con indice

### Caso 1

```js
db.users.findOne({ email: "ana@example.com" })
```

Sin indice en `email`:

- mas trabajo de busqueda
- peor tiempo de respuesta cuando crece `users`

Con indice en `email`:

- busqueda mas directa
- mejor comportamiento para login y consultas administrativas

### Caso 2

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

Sin indice compuesto:

- mas documentos revisados
- mas costo para el endpoint de historial

Con indice compuesto `{ userId: 1, createdAt: -1 }`:

- mejor alineacion con el patron de la consulta
- respuesta mas estable para la API

### Cierre practico

En backend, los indices no se justifican por teoria sola. Se justifican cuando mejoran consultas reales que sostienen endpoints, procesos y servicios del sistema.
