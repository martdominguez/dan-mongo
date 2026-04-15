# Teoria

## 1. Que problema resuelve este modulo

Hasta este punto del curso ya vimos:

- como guardar documentos
- como consultar documentos
- como modelar colecciones
- como pensar indices para que las consultas frecuentes respondan mejor

Ahora aparece una necesidad muy comun en backend:

no solo quieres traer documentos, tambien quieres resumirlos, transformarlos o agruparlos.

Ejemplos reales:

- contar cuantas ordenes hay por estado
- calcular ventas por categoria
- obtener un resumen mensual de compras
- devolver solo una parte transformada del documento para una API
- tomar un array y trabajar cada elemento por separado

Con `find` puedes filtrar y proyectar campos. Pero hay escenarios donde eso no alcanza.

Si una API necesita responder:

- "cuantas ordenes hay por estado"
- "cuanto facturamos por mes"
- "que categorias vendieron mas"

entonces necesitas una herramienta que procese los documentos y genere una salida nueva.

Esa herramienta es Aggregation Framework.

## 2. Que es una agregacion

Una agregacion es un proceso en el que MongoDB toma un conjunto de documentos, los hace pasar por una secuencia de pasos y devuelve un resultado transformado.

Ese resultado puede ser:

- un subconjunto filtrado
- una version con menos o mas campos
- una agrupacion por algun criterio
- un conteo
- un resumen numerico

### Idea clave

Una agregacion no siempre devuelve documentos con la misma forma que tenian al inicio.

Muchas veces devuelve documentos nuevos, construidos para responder una pregunta de negocio.

## 3. Diferencia entre `find` y `aggregate`

Esta diferencia conviene tenerla muy clara.

### `find`

`find` sirve principalmente para:

- buscar documentos
- filtrar por campos
- ordenar resultados
- limitar cuantos documentos quieres traer
- proyectar algunos campos

Ejemplo:

```js
db.orders.find(
  { status: "PAID" },
  { userId: 1, total: 1, createdAt: 1 }
).sort({ createdAt: -1 })
```

Esto devuelve documentos de `orders` que siguen pareciendose a los documentos originales.

### `aggregate`

`aggregate` sirve cuando necesitas procesar los datos en varias etapas.

Ejemplo:

```js
db.orders.aggregate([
  { $match: { status: "PAID" } },
  {
    $group: {
      _id: "$userId",
      totalSpent: { $sum: "$total" }
    }
  },
  { $sort: { totalSpent: -1 } }
])
```

Ahora el resultado ya no es "cada orden". El resultado es "gasto total por usuario".

### Regla practica

Piensalo asi:

- usa `find` cuando necesitas recuperar documentos
- usa `aggregate` cuando necesitas construir una respuesta derivada de esos documentos

## 4. Por que importa en escenarios backend

En un backend no todo es CRUD puro.

Muy rapido aparecen necesidades como:

- dashboards internos
- paneles administrativos
- respuestas de API que ya vienen resumidas
- reportes para negocio
- estadisticas para observabilidad funcional

### Ejemplos concretos

1. Un panel de soporte necesita saber cuantos tickets estan `OPEN`, `PENDING` y `CLOSED`.
2. Un endpoint administrativo necesita devolver ventas por mes.
3. Un servicio de catalogo quiere saber cuantos productos hay por categoria activa.
4. Un panel de usuarios quiere mostrar cuantos pedidos hizo cada cliente VIP.

En todos esos casos, mandar documentos crudos al backend para que Java haga todo el trabajo suele ser menos directo y mas costoso.

MongoDB puede resolver gran parte de ese procesamiento dentro de la base mediante un `pipeline`.

## 5. Modelo mental del pipeline

Esta es la idea central del modulo.

Un `pipeline` es una lista ordenada de `stages`.

Cada `stage`:

- recibe documentos de entrada
- aplica una transformacion
- entrega documentos de salida al siguiente stage

### Sintaxis general

```js
db.collection.aggregate([
  { stage1: { ... } },
  { stage2: { ... } },
  { stage3: { ... } }
])
```

### Como pensarlo

No lo pienses como una sola consulta enorme.

Conviene pensarlo como una linea de produccion:

1. primero filtras
2. despues eliges o calculas campos
3. despues agrupas
4. despues ordenas o recortas el resultado

### Flujo de entrada y salida

Si empiezas con 1000 documentos:

- `$match` puede dejar 150
- `$project` puede reducir cada documento a 4 campos
- `$group` puede convertir esos 150 documentos en 6 grupos
- `$sort` puede ordenar esos 6 resultados

La salida de un stage es la entrada del siguiente.

Eso explica por que el orden importa.

## 6. Transformacion progresiva de documentos

Una agregacion suele cambiar progresivamente la forma del dato.

### Ejemplo mental

Documento de entrada en `orders`:

```json
{
  "_id": "ord-1001",
  "userId": "u10",
  "status": "PAID",
  "total": 120000,
  "createdAt": { "$date": "2026-04-10T10:30:00Z" }
}
```

Luego de un `$match`, el documento puede seguir igual, solo que pasan menos.

Luego de un `$project`, puede quedar asi:

```json
{
  "userId": "u10",
  "total": 120000,
  "createdAt": { "$date": "2026-04-10T10:30:00Z" }
}
```

Luego de un `$group`, ya puede transformarse en algo nuevo:

```json
{
  "_id": "u10",
  "ordersCount": 8,
  "totalSpent": 540000
}
```

Eso muestra un punto importante:

el `pipeline` no solo filtra. Tambien cambia el nivel de informacion con el que estas trabajando.

## 7. Stage: `$match`

## 7.1. Que resuelve

`$match` filtra documentos.

Es el stage que mas se parece a una condicion de `find`.

### Sintaxis

```js
{ $match: { status: "PAID" } }
```

Tambien puedes usar operadores conocidos:

```js
{
  $match: {
    total: { $gte: 50000 },
    status: "PAID"
  }
}
```

## 7.2. Cuando usarlo

- para reducir el volumen de datos cuanto antes
- para quedarte con el subconjunto que realmente importa
- para preparar una agrupacion posterior

## 7.3. Intencion de backend

En backend casi nunca quieres agrupar "todo" si la API solo necesita una parte.

Ejemplo:

- si el reporte es de ordenes pagadas, filtra `status: "PAID"` antes de agrupar
- si el panel es del ultimo trimestre, filtra por `createdAt` antes del resto

## 7.4. Buena practica

Cuando sea posible, coloca `$match` temprano.

Eso mejora legibilidad y suele ayudar a que el pipeline procese menos documentos en los stages siguientes.

## 8. Stage: `$project`

## 8.1. Que resuelve

`$project` define que campos quieres conservar, excluir o calcular en la salida.

### Sintaxis basica

```js
{
  $project: {
    _id: 0,
    userId: 1,
    total: 1
  }
}
```

### Que hace

- `1` incluye el campo
- `0` excluye el campo
- tambien puedes crear campos derivados

Ejemplo:

```js
{
  $project: {
    _id: 0,
    orderId: "$_id",
    userId: 1,
    total: 1
  }
}
```

## 8.2. Cuando usarlo

- para hacer mas clara la salida
- para renombrar campos en la respuesta
- para dejar solo lo necesario antes de seguir procesando

## 8.3. Intencion de backend

Muchas APIs no quieren exponer el documento entero.

Quieren devolver una forma de respuesta mas cercana a la necesidad del endpoint:

- un resumen de pedido
- un DTO de reporte
- una lista compacta para tabla o dashboard

`$project` ayuda a construir esa salida desde la agregacion.

## 8.4. Variacion util

Puedes proyectar campos existentes y calculados en el mismo stage, siempre que la salida siga siendo legible.

En un modulo introductorio conviene evitar proyecciones excesivamente densas.

## 9. Stage: `$group`

## 9.1. Que resuelve

`$group` agrupa documentos por una clave y calcula valores agregados.

Es uno de los stages mas importantes del modulo.

### Sintaxis general

```js
{
  $group: {
    _id: "$status",
    totalOrders: { $sum: 1 }
  }
}
```

### Que significa

- `_id` define el criterio de agrupacion
- el resto de campos define acumulaciones por grupo

## 9.2. Operaciones comunes dentro de `$group`

### Contar documentos

```js
{
  $group: {
    _id: "$status",
    total: { $sum: 1 }
  }
}
```

### Sumar montos

```js
{
  $group: {
    _id: "$category",
    revenue: { $sum: "$total" }
  }
}
```

### Promedio

```js
{
  $group: {
    _id: "$category",
    averagePrice: { $avg: "$price" }
  }
}
```

Aunque este modulo no profundiza en demasiados acumuladores, conviene recordar que `$sum` y `$avg` son muy comunes.

## 9.3. Cuando usarlo

- para contar por estado
- para totalizar por categoria
- para resumir por usuario
- para agrupar por periodos

## 9.4. Intencion de backend

`$group` aparece cuando la respuesta ya no habla de documentos individuales sino de resumenes.

Ejemplos:

- no quieres cada ticket, quieres cuántos tickets hay por prioridad
- no quieres cada orden, quieres total vendido por mes

## 9.5. Advertencia

Despues de `$group`, la forma del documento cambia.

No asumas que siguen existiendo todos los campos originales.

Si mas adelante quieres ordenar por un valor agregado, ese valor debe existir en la salida del grupo.

## 10. Stage: `$sort`

## 10.1. Que resuelve

`$sort` ordena documentos.

### Sintaxis

```js
{ $sort: { totalSpent: -1 } }
```

### Valores

- `1` ascendente
- `-1` descendente

## 10.2. Cuando usarlo

- para mostrar resultados mas relevantes primero
- para ordenar totales de mayor a menor
- para devolver resumenes mensuales en orden cronologico

## 10.3. Intencion de backend

Una API administrativa o un dashboard casi nunca quiere resultados sin orden.

Si agrupas por categoria y calculas ventas, normalmente querras ver primero la categoria con mayor facturacion o mostrar los meses en secuencia.

## 11. Stage: `$limit`

## 11.1. Que resuelve

`$limit` corta la cantidad de documentos de salida.

### Sintaxis

```js
{ $limit: 5 }
```

## 11.2. Cuando usarlo

- top de categorias
- top de usuarios por gasto
- widgets que muestran pocos resultados

## 11.3. Relacion con `$sort`

Una combinacion muy comun es:

1. agrupar
2. ordenar
3. limitar

Ejemplo mental:

quieres las 3 categorias con mas ventas.

## 12. Stage: `$skip`

## 12.1. Que resuelve

`$skip` salta cierta cantidad de documentos.

### Sintaxis

```js
{ $skip: 10 }
```

## 12.2. Cuando usarlo

- paginacion simple de resultados agregados
- omitir los primeros N elementos ya vistos

## 12.3. Intencion de backend

Si una API arma un ranking o un reporte paginado, `$skip` puede servir junto con `$limit`.

En un modulo introductorio alcanza con entender la idea:

- `$skip` mueve el punto de inicio
- `$limit` controla cuantos documentos devolver

## 13. Stage: `$unwind`

## 13.1. Que resuelve

`$unwind` toma un campo array y genera un documento por cada elemento del array.

### Ejemplo de entrada

```json
{
  "_id": "ord-2001",
  "items": [
    { "productId": "p10", "quantity": 2 },
    { "productId": "p20", "quantity": 1 }
  ]
}
```

### Idea de salida conceptual

Despues de aplicar `$unwind` sobre `items`, MongoDB trabaja como si existieran dos documentos separados, uno por cada item.

### Sintaxis

```js
{ $unwind: "$items" }
```

## 13.2. Cuando usarlo

- cuando necesitas analizar elementos dentro de arrays
- cuando quieres agrupar por producto vendido dentro de una orden
- cuando quieres contar tags, categorias o eventos contenidos en un array

## 13.3. Intencion de backend

En documentos reales, muchos datos importantes viven dentro de arrays:

- items de una orden
- tags de un producto
- historial de eventos

Si quieres trabajar cada elemento como unidad de analisis, `$unwind` es el stage correcto.

## 14. Stage: `$count`

## 14.1. Que resuelve

`$count` devuelve la cantidad de documentos que llegan a ese punto del pipeline.

### Sintaxis

```js
{ $count: "totalPaidOrders" }
```

## 14.2. Cuando usarlo

- para contar resultados filtrados
- para devolver un total rapido en una respuesta de reporte
- para validar cuántos documentos cumplen cierta condicion

## 14.3. Diferencia con contar dentro de `$group`

- usa `$count` cuando quieres un unico total final
- usa `$group` con `$sum: 1` cuando quieres contar por categoria, estado o grupo

## 15. Stage: `$addFields`

## 15.1. Que resuelve

`$addFields` agrega campos nuevos o recalcula campos existentes sin eliminar automaticamente el resto.

### Sintaxis

```js
{
  $addFields: {
    hasDiscount: true
  }
}
```

### Diferencia conceptual con `$project`

- `$project` define explicitamente la forma de salida
- `$addFields` agrega o modifica campos manteniendo los demas

## 15.2. Cuando usarlo

- para preparar un campo antes de agrupar
- para construir una salida mas clara
- para hacer una transformacion simple sin reescribir todos los campos

## 15.3. Intencion de backend

Es util cuando quieres enriquecer el documento temporalmente dentro del pipeline.

Por ejemplo:

- derivar un mes a partir de una fecha
- calcular un subtotal
- exponer una bandera que simplifique la lectura del resultado

## 16. Patrones practicos frecuentes

## 16.1. Totales por categoria

Patron:

1. filtrar datos relevantes
2. agrupar por categoria
3. sumar montos o contar documentos
4. ordenar de mayor a menor

Uso real:

- ventas por categoria
- productos por categoria
- tickets por tipo

## 16.2. Conteo por estado

Patron:

1. opcionalmente filtrar por rango o contexto
2. agrupar por `status`
3. contar con `$sum: 1`

Uso real:

- ordenes por estado
- tickets por estado
- usuarios por rol o condicion

## 16.3. Resumenes mensuales

Patron:

1. filtrar por fecha o estado
2. derivar mes si hace falta
3. agrupar por periodo
4. ordenar cronologicamente

Uso real:

- ventas por mes
- nuevos usuarios por mes
- tickets cerrados por mes

## 16.4. Aplanar arrays

Patron:

1. filtrar documentos relevantes
2. aplicar `$unwind`
3. agrupar o proyectar a nivel de elemento

Uso real:

- productos vendidos dentro de ordenes
- tags mas repetidos
- eventos mas frecuentes dentro de historiales

## 16.5. Filtrar antes de agrupar

Patron:

1. `$match`
2. `$group`

Uso real:

si quieres ventas solo de ordenes pagadas, no tiene sentido agrupar tambien ordenes canceladas y recien despues separar.

## 17. Buenas practicas iniciales

## 17.1. Usar `$match` temprano cuando sea posible

Esto ayuda por dos motivos:

- el pipeline se entiende mejor
- los stages posteriores trabajan con menos documentos

## 17.2. Mantener pipelines legibles

Una agregacion introductoria debe poder leerse de arriba hacia abajo sin esfuerzo excesivo.

Buenas decisiones:

- pocos stages por ejemplo
- nombres de campos claros
- una idea principal por pipeline

## 17.3. Evitar complejidad innecesaria

No hace falta mezclar muchos operadores avanzados para resolver una necesidad simple.

Si el objetivo es contar por estado, un pipeline corto y claro suele ser mejor que uno mas "ingenioso" pero dificil de mantener.

## 17.4. Diseñar la salida pensando en el consumidor

Pregunta util:

que necesita realmente la API o el reporte?

No siempre necesitas devolver el documento entero ni todos los campos intermedios.

## 17.5. Revisar el orden de los stages

El orden importa.

Ejemplo:

- si necesitas filtrar antes de agrupar, el `$match` debe ir antes
- si quieres quedarte con el top 5 de ventas, primero debes ordenar y despues limitar

## 18. Errores comunes

## 18.1. Agrupar demasiado pronto

Si agrupas antes de filtrar, puedes hacer trabajar de mas al pipeline y complicar la lectura.

## 18.2. Usar `$project` sin una necesidad clara

Si el pipeline ya es claro, no agregues proyecciones innecesarias solo por costumbre.

## 18.3. Mezclar demasiadas transformaciones en un solo ejemplo

En una primera etapa conviene separar:

- filtrar
- agrupar
- ordenar

Eso ayuda a entender la intencion de cada stage.

## 18.4. Confundir contar total con contar por grupo

Recuerda:

- `$count` devuelve un total final
- `$group` con `$sum: 1` cuenta por grupo

## 19. Relacion con lo que sigue en el curso

Este modulo introduce la logica de agregacion desde `mongosh` y desde el punto de vista del diseño de consultas.

Mas adelante, cuando lleguemos a Spring Boot, veremos como construir estos pipelines desde Java usando `MongoTemplate`.

La idea es llegar a esa etapa con dos bases ya resueltas:

- entender que pregunta de negocio debe responder la agregacion
- saber que stages componen esa respuesta

## 20. Resumen operativo

Si quieres recordar una version corta del modulo, quedate con esto:

1. una agregacion procesa documentos por etapas
2. cada stage recibe una salida del stage anterior
3. `$match`, `$project` y `$group` son la base mas importante
4. `$sort`, `$limit` y `$skip` ordenan y recortan resultados
5. `$unwind` sirve para trabajar arrays
6. `$count` sirve para un total final
7. `$addFields` sirve para enriquecer documentos dentro del pipeline
8. en backend, agregacion aparece cuando necesitas resumenes, conteos o transformaciones utiles para una API
