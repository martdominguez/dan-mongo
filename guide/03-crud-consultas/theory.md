# Teoria

## 1. Que problema resuelve este modulo

En un backend real no alcanza con tener MongoDB instalado. Necesitas operar datos con seguridad y de forma legible.

Eso aparece enseguida en tareas comunes:

- registrar un usuario nuevo
- guardar un pedido
- listar productos para una API
- actualizar el estado de una orden
- borrar datos obsoletos o pruebas de desarrollo

CRUD resume ese trabajo basico:

- Create
- Read
- Update
- Delete

En MongoDB, estas operaciones se hacen sobre colecciones y documentos. Aprenderlas bien importa porque son la base de casi todo lo que hara despues una aplicacion Spring Boot.

## 2. Create: insertar documentos

Insertar documentos resuelve el problema mas simple de todos: persistir un dato nuevo.

Las dos operaciones iniciales mas comunes son:

- `insertOne`
- `insertMany`

### `insertOne`

Sirve para insertar un solo documento.

Ejemplo:

```js
db.users.insertOne({
  name: "Ana Perez",
  email: "ana@example.com",
  role: "customer",
  active: true
})
```

### Resultado esperado

MongoDB devuelve un objeto con `acknowledged: true` y el `_id` insertado.

### Cuando aparece en backend

Esto se parece mucho a un caso como:

- `POST /api/users`
- registrar un usuario
- crear un producto
- guardar un pedido generado por el checkout

Si tu servicio recibe un payload valido y debe persistir una sola entidad, `insertOne` es la forma mas directa de pensarlo.

### `insertMany`

Sirve para insertar varios documentos en una sola operacion.

Ejemplo:

```js
db.products.insertMany([
  {
    name: "Mouse Bluetooth",
    category: "accessories",
    price: 25000,
    active: true
  },
  {
    name: "Teclado Mecanico",
    category: "accessories",
    price: 78000,
    active: true
  }
])
```

### Cuando conviene

`insertMany` suele aparecer cuando:

- cargas datos iniciales de catalogo
- preparas fixtures de desarrollo
- migras un conjunto pequeno de datos
- una tarea backend genera varios documentos juntos

### Buena practica inicial

Aunque MongoDB sea flexible, conviene insertar documentos con una estructura consistente. Eso hace mas faciles las consultas posteriores y prepara mejor el camino para Spring Data MongoDB.

## 3. Read: leer documentos

Leer datos es la operacion mas frecuente en muchos backends. Una API suele consultar mucho mas de lo que escribe.

Las dos operaciones basicas son:

- `find`
- `findOne`

### `find`

Devuelve varios documentos que cumplen un criterio.

Ejemplo:

```js
db.products.find({ active: true })
```

Esto devuelve todos los productos activos.

### `findOne`

Devuelve un solo documento.

Ejemplo:

```js
db.users.findOne({ email: "ana@example.com" })
```

Esto es util cuando esperas un resultado puntual, por ejemplo:

- buscar un usuario por email
- traer un pedido por `_id`
- obtener un producto por codigo

### Como se mapea a backend

Piensa en estas traducciones simples:

- `GET /api/products` -> `find`
- `GET /api/orders/ord-9001` -> `findOne`
- servicio `findUserByEmail(...)` -> `findOne`

Todavia no estamos usando Java, pero el patron mental ya es el mismo que luego usaras en un servicio backend.

## 4. Filtros basicos

Una consulta sin filtro trae mucho ruido. En una API real casi siempre necesitas acotar.

Los filtros basicos permiten responder preguntas como:

- que usuarios siguen activos
- que pedidos estan pendientes
- que productos cuestan menos de cierto valor
- que ordenes pertenecen a un usuario

### Igualdad simple

```js
db.orders.find({ status: "CREATED" })
```

Esto sirve para listar pedidos en un estado puntual.

### Comparaciones frecuentes

MongoDB usa operadores como:

- `$gt`: mayor que
- `$gte`: mayor o igual que
- `$lt`: menor que
- `$lte`: menor o igual que

Ejemplo:

```js
db.products.find({
  price: { $lte: 50000 }
})
```

Esto podria alimentar un endpoint como `GET /api/products?maxPrice=50000`.

### Filtros combinados

Tambien puedes combinar condiciones en el mismo objeto:

```js
db.products.find({
  category: "accessories",
  active: true
})
```

Esto representa una necesidad muy comun de backend: no buscar "todo", sino solo lo que cumple una regla concreta del negocio.

### Ejemplo mas fluido

Imagina una API que lista productos visibles para la tienda online. No deberia devolver:

- productos inactivos
- productos de otra categoria si el filtro la especifica

La consulta podria verse asi:

```js
db.products.find({
  category: "accessories",
  active: true
})
```

La idea no es solo aprender sintaxis. La idea es entender que el filtro ya contiene una regla de negocio simple.

## 5. Proyecciones simples

No siempre conviene devolver el documento completo. Muchas respuestas de API necesitan solo algunos campos.

Para eso sirven las proyecciones.

Ejemplo:

```js
db.products.find(
  { active: true },
  { name: 1, price: 1 }
)
```

Esto pide:

- todos los productos activos
- mostrando solo `name` y `price`

### Por que importa en backend

En una lista de catalogo, tal vez no necesitas:

- descripciones extensas
- metadatos internos
- campos de auditoria

Una proyeccion simple ayuda a:

- devolver menos datos
- hacer mas clara la respuesta
- separar mejor una vista de listado de una vista de detalle

### Detalle importante

En MongoDB, cuando incluyes campos con `1`, `_id` aparece salvo que lo excluyas explicitamente.

Ejemplo:

```js
db.products.find(
  { active: true },
  { _id: 0, name: 1, price: 1 }
)
```

Esto es muy util cuando quieres una respuesta mas parecida al DTO que luego usaria una API.

## 6. Ordenar y limitar resultados

Dos operaciones muy comunes en listados son:

- ordenar
- limitar

### `sort`

Permite ordenar los resultados.

Ejemplo:

```js
db.orders.find({ userId: "u1001" }).sort({ createdAt: -1 })
```

Con `-1` ordenas de mayor a menor. En este caso, de la orden mas reciente a la mas antigua.

### `limit`

Permite traer solo una cantidad acotada de documentos.

Ejemplo:

```js
db.orders.find({ userId: "u1001" }).sort({ createdAt: -1 }).limit(5)
```

### Caso backend tipico

Un endpoint de historial puede necesitar "las ultimas 5 ordenes de un usuario". Esa necesidad se traduce casi directo en:

```js
db.orders.find({ userId: "u1001" }).sort({ createdAt: -1 }).limit(5)
```

### Puente con modelado

Esta consulta funciona mejor si `orders` guarda campos operativos como `userId`, `status` y `createdAt` al nivel principal del documento.

Mas adelante, en modelado, veremos que una orden puede tener ademas datos embebidos como `userSnapshot` o `items`, sin perder esos campos clave para consultar e indexar bien.

### Buena practica

Si una consulta alimenta un listado o una API publica, limitar el volumen suele ser mejor que devolver todo sin control.

## 7. Update: actualizar documentos

Actualizar resuelve el problema de cambiar datos existentes sin reinsertar todo el documento.

Las operaciones mas comunes son:

- `updateOne`
- `updateMany`

### `updateOne`

Actualiza el primer documento que coincide con el filtro.

Ejemplo:

```js
db.orders.updateOne(
  { _id: "ord-9001" },
  { $set: { status: "PAID" } }
)
```

### Que esta pasando

El filtro identifica que documento quieres modificar.

`$set` indica que campo quieres cambiar.

### Como se mapea a backend

Esto representa muy bien un servicio como:

- confirmar pago de un pedido
- actualizar el rol de un usuario
- corregir el precio visible de un producto

Si un endpoint hace `PATCH /api/orders/ord-9001/status`, internamente esta idea aparece enseguida.

### Aclaracion sobre `_id` en los ejemplos

En varios ejemplos usamos valores como `"ord-9001"` para que el caso sea mas facil de leer.

En MongoDB, si no envias `_id`, normalmente se genera un `ObjectId` automaticamente.

En sistemas reales tambien puedes encontrar ids de negocio propios o un campo separado como `orderNumber`.

La idea pedagogica aqui es entender el patron de actualizacion. Mas adelante conviene distinguir con claridad:

- `_id` tecnico del documento
- identificador funcional que viaja en URLs o reglas de negocio

### `updateMany`

Actualiza varios documentos que cumplen el mismo criterio.

Ejemplo:

```js
db.users.updateMany(
  { active: false },
  { $set: { archived: true } }
)
```

### Cuando aparece

Esto es util en tareas como:

- marcar usuarios inactivos
- desactivar productos de una categoria
- ajustar un campo comun en muchos documentos

### Advertencia importante

En una primera etapa, conviene ser muy cuidadoso con `updateMany`. Un filtro mal armado puede afectar muchos documentos.

## 8. Delete: borrar documentos

Borrar datos tambien forma parte del trabajo normal de un backend.

Las dos operaciones basicas son:

- `deleteOne`
- `deleteMany`

### `deleteOne`

Elimina un documento que coincide con el filtro.

Ejemplo:

```js
db.users.deleteOne({ email: "test-user@example.com" })
```

Esto puede representar un caso de limpieza de datos de prueba o la eliminacion puntual de un registro.

### `deleteMany`

Elimina varios documentos.

Ejemplo:

```js
db.orders.deleteMany({ status: "CANCELLED" })
```

### Criterio de uso

`deleteMany` puede servir para:

- limpiar datos temporales
- borrar pruebas de desarrollo
- eliminar registros obsoletos

Pero en sistemas reales muchas veces se prefiere una baja logica, por ejemplo con un campo `active` o `deletedAt`. Eso depende del negocio y lo veremos mejor al conectar estas ideas con servicios y modelado.

## 9. Un flujo CRUD completo y realista

Conviene ver CRUD como una secuencia de trabajo y no como comandos sueltos.

Ejemplo con una API de productos:

1. el administrador crea un producto
2. la tienda lo consulta para mostrarlo
3. despues actualiza su precio
4. finalmente lo desactiva o lo elimina

Esa historia se puede traducir asi:

```js
db.products.insertOne({
  name: "Monitor 27",
  category: "monitors",
  price: 320000,
  active: true
})

db.products.findOne({ name: "Monitor 27" })

db.products.updateOne(
  { name: "Monitor 27" },
  { $set: { price: 299000 } }
)

db.products.deleteOne({ name: "Monitor 27" })
```

La idea importante es esta: un backend trabaja sobre el ciclo de vida del dato.

## 10. Preparando el camino para Spring Boot

Mas adelante, estas mismas operaciones apareceran con otra forma:

- repositorios
- servicios
- controladores
- consultas derivadas o construidas desde Java

Pero el fondo no cambia demasiado.

Si hoy entiendes que una API necesita:

- insertar un usuario
- buscar pedidos por estado
- devolver solo algunos campos
- actualizar un documento por `_id`

despues sera mucho mas natural entender que hace un `MongoRepository` o cuando usar `MongoTemplate`.

Por eso este modulo no es solo sintaxis de `mongosh`. Es la base mental para trabajar MongoDB desde una aplicacion backend.

## 11. Consultas complementarias para tener en el radar

Lo que sigue no es el nucleo del modulo. Son operadores utiles para extender CRUD cuando una API necesita filtros un poco mas expresivos.

Si todavia estas consolidando `find`, proyecciones, `sort`, `limit` y actualizaciones puntuales, conviene dominar eso primero y volver luego a estas variantes.

Hasta aqui vimos filtros simples. Con eso ya puedes resolver muchos casos reales. Pero un backend de verdad suele pedir un poco mas:

- comparar valores con distintas condiciones
- combinar reglas de negocio
- consultar arrays
- filtrar por listas de valores admitidos
- verificar si un campo existe
- hacer busquedas textuales o parciales

Conviene aprender estos operadores por intencion de uso, no como una lista suelta.

### Nivel 1: uso core para servicios y APIs

Este nivel aparece muy seguido en endpoints, paneles administrativos y servicios de negocio.

### Cuando necesitas comparar un valor concreto

A veces una API necesita responder preguntas como:

- que productos cuestan mas de cierto valor
- que ordenes no estan canceladas
- que usuarios siguen activos

MongoDB ofrece operadores de comparacion como:

- `$eq`
- `$ne`
- `$gt`
- `$gte`
- `$lt`
- `$lte`

Ejemplo orientado a backend:

```js
db.products.find({
  price: { $gte: 50000, $lte: 300000 },
  active: { $eq: true }
})
```

Esto podria alimentar un endpoint como `GET /api/products?minPrice=50000&maxPrice=300000`.

Tambien puede servir para excluir un estado:

```js
db.orders.find({
  status: { $ne: "CANCELLED" }
})
```

Eso es util si un servicio interno necesita listar solo ordenes vigentes.

### Cuando una sola condicion no alcanza

Un backend no siempre filtra por una sola regla. Muchas veces necesita combinar criterios.

Para eso aparecen:

- `$and`
- `$or`

Ejemplo con `$and`:

```js
db.products.find({
  $and: [
    { active: true },
    { stock: { $gt: 0 } },
    { category: "accessories" }
  ]
})
```

Aunque este ejemplo podria escribirse tambien sin `$and`, mostrarlo asi ayuda a entender su papel cuando la consulta crece.

Ejemplo con `$or`:

```js
db.users.find({
  $or: [
    { role: "admin" },
    { role: "support" }
  ],
  active: true
})
```

Esto se parece a un caso donde una API interna necesita usuarios habilitados con alguno de varios roles permitidos.

### Cuando el documento tiene arrays simples

Los arrays son una parte muy importante de MongoDB. No son un detalle secundario.

Si una coleccion `products` guarda etiquetas, por ejemplo:

```json
{
  "name": "Mouse Bluetooth",
  "tags": ["wireless", "office", "sale"]
}
```

puedes consultar coincidencias simples asi:

```js
db.products.find({
  tags: "sale"
})
```

Esto devuelve productos cuyo array `tags` contiene ese valor.

En backend eso puede servir para:

- listar productos destacados
- filtrar por etiquetas visibles en catalogo
- resolver reglas sencillas de merchandising

## 12. Nivel 2: uso intermedio para filtros mas reales

Este nivel aparece cuando una API deja de ser trivial y necesita expresar reglas algo mas ricas.

### Cuando el filtro depende de una lista permitida o bloqueada

Si un endpoint acepta varias categorias o varios estados, resultan muy utiles:

- `$in`
- `$nin`

Ejemplo:

```js
db.orders.find({
  status: { $in: ["CREATED", "PAID", "SHIPPED"] }
})
```

Esto puede representar una vista de operacion que excluye estados finales irrelevantes.

Y tambien puedes hacer lo contrario:

```js
db.products.find({
  category: { $nin: ["internal", "deprecated"] },
  active: true
})
```

Eso ayuda a evitar que una API publica devuelva categorias que no deberian exponerse.

### Cuando importa si un campo existe o no

En colecciones flexibles, no todos los documentos tienen siempre la misma forma. Para eso sirve:

- `$exists`

Ejemplo:

```js
db.users.find({
  lastLoginAt: { $exists: true }
})
```

Esto puede servir para un reporte o servicio que quiera trabajar solo con usuarios que ya iniciaron sesion alguna vez.

Tambien puedes buscar lo contrario:

```js
db.users.find({
  phone: { $exists: false },
  active: true
})
```

Eso podria alimentar una tarea de onboarding o completitud de perfil.

### Cuando el array forma parte real del negocio

MongoDB se vuelve especialmente interesante cuando los arrays son parte natural del modelo.

#### Buscar documentos que contengan varios valores

Si quieres pedir que un array contenga todos los valores indicados, puedes usar:

- `$all`

Ejemplo:

```js
db.products.find({
  tags: { $all: ["wireless", "office"] },
  active: true
})
```

Eso puede servir para una API de catalogo con filtros acumulativos.

#### Buscar arrays con una cantidad exacta de elementos

Si necesitas una condicion por longitud, puedes usar:

- `$size`

Ejemplo:

```js
db.orders.find({
  items: { $size: 1 }
})
```

Esto puede servir para detectar ordenes de un solo item, por ejemplo en una regla promocional o en un analisis operativo simple.

#### Buscar dentro de arrays de objetos

Este es uno de los operadores mas utiles cuando trabajas con MongoDB:

- `$elemMatch`

Imagina un pedido asi:

```json
{
  "_id": "ord-9001",
  "items": [
    { "productId": "p10", "quantity": 1, "price": 25000 },
    { "productId": "p11", "quantity": 3, "price": 15000 }
  ]
}
```

Si quieres encontrar pedidos donde exista al menos un item con `productId` igual a `"p11"` y cantidad mayor a `1`, puedes escribir:

```js
db.orders.find({
  items: {
    $elemMatch: {
      productId: "p11",
      quantity: { $gt: 1 }
    }
  }
})
```

Esto es muy valioso en backend porque evita ambiguedades al consultar arrays de subdocumentos.

Un caso real podria ser:

- detectar ordenes con multiples unidades de un producto
- buscar pedidos que requieren una regla especial de envio
- encontrar compras que contienen un item de una categoria promocional

## 13. Nivel 3: introduccion avanzada para no sorprenderte despues

Estos operadores existen y son utiles, pero por ahora conviene verlos como primera aproximacion.

### Cuando quieres controlar mejor el tipo de dato

MongoDB permite filtrar por tipo con:

- `$type`

Ejemplo:

```js
db.products.find({
  price: { $type: "number" }
})
```

Esto puede ayudar en auditorias de datos o migraciones donde sospechas documentos inconsistentes.

No es una consulta de negocio tipica del dia a dia, pero si una herramienta util de mantenimiento.

### Cuando el filtro depende de una regla matematica simple

MongoDB tambien ofrece:

- `$mod`

Ejemplo:

```js
db.orders.find({
  total: { $mod: [1000, 0] }
})
```

Esto encuentra ordenes cuyo total es multiplo de `1000`.

No es de los operadores mas frecuentes en CRUD cotidiano, pero puede aparecer en tareas internas, validaciones o depuracion de datos.

### Cuando necesitas una busqueda textual

MongoDB ofrece:

- `$text`

Ejemplo:

```js
db.products.find({
  $text: { $search: "bluetooth ergonomico" }
})
```

Esto puede ser util para una busqueda simple sobre catalogo o contenido indexado.

Pero hay un matiz importante: `$text` depende de haber preparado antes un indice de texto. Como en este modulo todavia no profundizamos en indices, alcanza con saber que existe y que mas adelante lo retomaremos con mejor contexto.

## 14. Otros operadores muy utiles para tener en el radar

Algunos operadores aparecen seguido aunque no siempre entren en la primera explicacion.

### Cuando necesitas una busqueda parecida a LIKE

Si vienes del mundo SQL, muchas veces querras una idea parecida a `LIKE`.

Para eso existe:

- `$regex`

Ejemplo:

```js
db.users.find({
  email: { $regex: "@example\\.com$", $options: "i" }
})
```

Esto puede servir para:

- buscar usuarios de un dominio concreto
- hacer filtros administrativos simples
- resolver autocompletados muy basicos

Conviene usarlo con criterio. No todo buscador deberia resolverse con regex, pero como herramienta puntual es muy util.

### Cuando quieres negar una sola condicion

Para negar una expresion puntual puedes usar:

- `$not`

Ejemplo:

```js
db.products.find({
  price: { $not: { $gt: 300000 } },
  active: true
})
```

En la practica, esto devuelve productos activos cuyo precio no supera ese valor.

### Cuando quieres descartar varios escenarios completos

Si necesitas negar varias alternativas juntas, puedes usar:

- `$nor`

Ejemplo:

```js
db.orders.find({
  $nor: [
    { status: "CANCELLED" },
    { status: "DELIVERED" }
  ]
})
```

Eso puede servir para una cola operativa que solo quiera ordenes aun accionables.

### Un comentario sobre `$expr`

Existe tambien:

- `$expr`

Permite comparar campos o construir expresiones mas complejas dentro del filtro.

Es potente, pero para este punto del curso conviene dejarlo para despues. Primero necesitamos dominar bien filtros sobre valores, arrays y condiciones de negocio comunes.

## 15. Que no estamos cubriendo todavia

En este modulo no vamos a profundizar en:

- agregaciones complejas
- indices y optimizacion
- modelado avanzado
- transacciones
- consultas construidas dinamicamente desde codigo Java

No porque no importen, sino porque primero necesitamos una base firme en operaciones cotidianas.

## 16. Cierre

Si al terminar este modulo puedes leer una necesidad de negocio simple y traducirla a:

- una insercion
- una consulta con filtro
- una proyeccion sencilla
- una actualizacion puntual
- un borrado controlado
- una consulta con operadores que expresen bien la regla del negocio

entonces ya tienes una base muy util para el resto del curso y para empezar a pensar MongoDB como parte real de un backend con Spring Boot.
