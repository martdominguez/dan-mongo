# Teoria

## 1. Que problema resuelve este modulo

Hasta ahora ya vimos:

- como guardar datos
- como consultarlos
- como modelarlos

El siguiente problema aparece enseguida en un backend real:

la consulta funciona, pero tarda demasiado.

Eso se vuelve visible cuando una API:

- busca usuarios por email en cada login
- lista ordenes de un usuario con filtros de fecha
- filtra productos por categoria en cada request del catalogo

Si MongoDB tiene que recorrer demasiados documentos para responder, el tiempo de respuesta sube. Y cuando el trafico crece, ese costo se repite muchas veces.

Los indices existen para reducir ese trabajo de busqueda.

### Puente con el modulo anterior

El indice correcto depende del modelo documental que elegiste antes.

Si en `orders` guardas un snapshot del usuario pero tambien mantienes `userId` al tope del documento, una consulta de historial puede indexar `userId` y `createdAt` sin problema.

Si escondes todos los datos de acceso dentro de estructuras menos practicas para tu backend, despues indexar y consultar bien tambien se vuelve mas dificil.

## 2. Que es un indice

Un indice es una estructura auxiliar que ayuda a encontrar documentos mas rapido sin revisar toda la coleccion documento por documento.

Dicho simple:

- la coleccion guarda los datos reales
- el indice ayuda a ubicarlos mejor

### Analogia simple: el indice de un libro

Si quieres buscar un tema en un libro grande, tienes dos caminos:

1. leer todas las paginas hasta encontrarlo
2. ir al indice, ubicar el termino y saltar a la pagina correcta

Sin indice, MongoDB se parece al primer caso.

Con indice, MongoDB puede acercarse mucho mas al segundo.

### Idea clave

Un indice no reemplaza al documento. Solo le da a MongoDB una ruta mas eficiente para llegar a el.

## 3. Por que los indices importan

En backend, la diferencia no es academica. Impacta directamente en:

- latencia de endpoints
- tiempo de respuesta de servicios internos
- uso de CPU y disco en la base
- capacidad de soportar mas trafico sin degradarse

### Ejemplo mental simple

Imagina una coleccion `users` con cientos de miles de documentos.

Si tu login hace esto:

```js
db.users.findOne({ email: "ana@example.com" })
```

y `email` no tiene indice, MongoDB puede necesitar revisar una gran cantidad de documentos hasta encontrar el correcto.

Si `email` si tiene indice, la busqueda se vuelve mucho mas directa.

### Impacto en APIs

Una consulta lenta aislada ya es molesta.

Pero en una API real el problema se multiplica porque:

- la misma consulta se ejecuta muchas veces por minuto
- varios endpoints dependen de filtros parecidos
- una latencia extra en base termina afectando el tiempo total del request

Por eso un indice bien elegido no solo mejora una consulta. Mejora el comportamiento del servicio.

## 4. Como usa MongoDB los indices

MongoDB intenta resolver una consulta con la ruta mas conveniente segun los indices disponibles y el filtro pedido.

No hace falta entrar en internals avanzados para entender la idea principal:

- si hay un indice util para la consulta, puede usarlo
- si no lo hay, puede recorrer la coleccion completa

## 5. Collection scan vs index scan

Estas dos ideas son centrales.

### `collection scan`

Significa que MongoDB recorre documentos de la coleccion para encontrar los que cumplen la condicion.

Esto no siempre es un problema en colecciones pequeñas. Pero en una coleccion grande puede ser costoso.

Ejemplo conceptual:

```js
db.products.find({ category: "monitors" })
```

Si `category` no tiene indice, MongoDB puede revisar muchisimos productos para devolver solo los de esa categoria.

### `index scan`

Significa que MongoDB usa un indice para acotar mucho mejor por donde buscar.

Conceptualmente, el flujo se parece a esto:

1. revisa el indice
2. ubica las claves que coinciden
3. llega mas rapido a los documentos necesarios

### Diferencia practica

La diferencia entre ambos caminos suele sentirse en:

- menos trabajo por consulta
- menos tiempo de respuesta
- menos degradacion cuando sube el volumen de datos

Para una API esto puede ser la diferencia entre un endpoint estable y uno que se vuelve lento con el crecimiento del producto.

## 6. Ejemplo rapido: usuario por email

Caso tipico de backend:

```js
db.users.findOne({ email: "ana@example.com" })
```

Si `email` es un criterio de busqueda frecuente, no conviene dejar esa consulta librada a recorrer toda la coleccion.

Indice recomendado:

```js
db.users.createIndex({ email: 1 })
```

### Por que tiene sentido

Porque el backend suele necesitar ese dato de manera puntual y repetida:

- login
- recuperacion de cuenta
- panel de administracion
- validacion de usuario existente

Este es un caso clasico de campo que merece un indice.

### Nota practica

Si en tu sistema el email debe ser unico, muchas veces este indice tambien termina siendo `unique`.

En este modulo alcanza con entender primero la idea de indexarlo por rendimiento. La restriccion de unicidad la veremos mejor cuando aparezcan reglas de integridad y validacion.

## 7. Tipos de indices mas importantes para empezar

## 7.1. Indice de campo simple

Es el indice sobre un solo campo.

Ejemplo:

```js
db.users.createIndex({ email: 1 })
```

Este es el tipo mas facil de entender y uno de los mas utiles.

### Cuando suele servir

- busqueda por email
- busqueda por codigo
- filtro por estado en una coleccion donde ese campo ayuda realmente
- consultas por fechas cuando ese campo se usa seguido

## 7.2. Indice compuesto

Es un indice sobre varios campos en un orden definido.

Ejemplo:

```js
db.orders.createIndex({ userId: 1, createdAt: -1 })
```

Este indice puede ser muy bueno para consultas como:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-01-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

### Idea clave

En un indice compuesto importa mucho el orden de los campos.

No es lo mismo:

```js
{ userId: 1, createdAt: -1 }
```

que:

```js
{ createdAt: -1, userId: 1 }
```

La diferencia existe porque el indice debe acompañar el patron real de consulta.

## 7.3. Indice de texto

Es un tipo de indice pensado para busquedas de texto.

Ejemplo inicial:

```js
db.products.createIndex({ name: "text", description: "text" })
```

### Que conviene entender en este modulo

- sirve para introducir busquedas textuales
- no reemplaza cualquier estrategia de busqueda
- no hace falta profundizar todavia en ranking ni casos avanzados

Por ahora alcanza con saber que existe para escenarios como:

- buscar productos por nombre o descripcion
- ofrecer una busqueda basica dentro de un catalogo

## 7.4. Indice multikey

MongoDB usa indices multikey cuando indexa campos que contienen arreglos.

Ejemplo:

```js
db.products.createIndex({ tags: 1 })
```

Si un producto tiene:

```json
{
  "name": "Notebook Pro 14",
  "tags": ["notebook", "premium", "trabajo-remoto"]
}
```

ese indice puede ayudar en consultas como:

```js
db.products.find({ tags: "premium" })
```

### Que conviene recordar

No hace falta dominar detalles internos ahora. Lo importante es entender que MongoDB tambien puede indexar campos con arrays y que eso aparece seguido en modelos documentales.

## 8. Como diseñar un indice

Crear indices por intuicion no alcanza. El criterio correcto parte de las consultas reales.

### Pregunta base

Que filtros y ordenes usa de verdad mi backend.

No conviene empezar preguntando:

"que campos parecen importantes"

Conviene empezar preguntando:

"que consultas se ejecutan mucho y cuales son sensibles en tiempo de respuesta"

## 9. Elegir campos segun patrones de consulta

Un buen indice suele nacer de consultas repetidas como estas:

- buscar usuario por `email`
- listar ordenes por `userId`
- filtrar ordenes de un usuario por fecha
- filtrar productos por `category` y `active`

Eso conecta el indice con el uso real de la API.

### Ejemplo

Si tu backend consulta muy seguido:

```js
db.orders.find({ userId: "u1001" })
```

entonces `userId` es un candidato claro para indexar.

Si ademas muchas veces hace esto:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

entonces ya aparece un candidato fuerte para indice compuesto.

Lo mismo pasa si el catalogo publica casi siempre productos activos de una categoria:

```js
db.products.find({
  category: "notebooks",
  active: true
})
```

En ese caso ya vale la pena evaluar un indice compuesto que acompanhe mejor ese patron que uno sobre `active` por separado.

## 10. Orden en indices compuestos

El orden de campos en un indice compuesto debe responder al patron mas comun de consulta.

### Caso practico

Consulta frecuente:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

Indice razonable:

```js
db.orders.createIndex({ userId: 1, createdAt: -1 })
```

### Por que este orden tiene sentido

Porque la consulta primero restringe por `userId` y luego trabaja sobre fecha.

Si inviertes el orden sin una razon clara, el indice puede quedar peor alineado con la consulta principal.

### Regla practica para este nivel

En indices compuestos, no pienses solo en los campos. Piensa en la consulta completa:

- por que campo filtras primero
- que otro campo acota o acompaña la consulta
- si hay ordenamiento frecuente

## 11. Consulta sin indice vs consulta con indice

La idea central puede resumirse asi:

### Sin indice

- MongoDB revisa demasiados documentos
- la consulta tarda mas
- la API siente mas latencia

### Con indice adecuado

- MongoDB reduce mucho el espacio de busqueda
- la consulta responde mas rapido
- el servicio escala mejor para ese patron

No hace falta profundizar todavia en `explain()` para captar la diferencia. Lo importante es entender la logica de trabajo.

## 12. Trade-offs de usar indices

Un indice no es gratis.

Si solo pensaramos en lectura, pondriamos indices en todos lados. Pero eso trae costos.

## 12.1. Costo en escritura

Cada vez que insertas o actualizas un documento, MongoDB no solo guarda el dato. Tambien puede tener que actualizar los indices relacionados.

Eso implica mas trabajo en operaciones de:

- `insertOne`
- `insertMany`
- `updateOne`
- `updateMany`

### Impacto backend

Si una coleccion recibe muchas escrituras, demasiados indices pueden volver mas caro cada cambio.

## 12.2. Costo en almacenamiento

Los indices ocupan espacio.

No guardan el documento completo como la coleccion, pero si agregan estructura extra que consume disco y memoria.

En una coleccion muy grande, varios indices innecesarios pueden convertirse en un costo real.

## 12.3. Problema de sobre-indexar

Sobre-indexar significa crear mas indices de los que realmente aportan valor.

Eso puede traer:

- mas costo de escritura
- mas espacio consumido
- mas complejidad para mantener criterio tecnico

### Error comun

Crear un indice por cada campo "por las dudas".

En backend casi nunca es una buena estrategia.

## 13. Errores comunes al empezar

## 13.1. Tener demasiados indices

Un indice debe justificar su existencia.

Si una consulta no es frecuente, no es sensible en latencia o no aporta al caso principal del backend, tal vez no necesita un indice dedicado.

## 13.2. Elegir mal el orden en un indice compuesto

Este error es muy comun.

Dos campos correctos con un orden incorrecto pueden rendir bastante peor que un indice realmente alineado con la consulta.

## 13.3. Indexar campos de baja selectividad sin analizar el caso

Un campo de baja selectividad es uno que repite pocos valores, por ejemplo:

- `active: true/false`
- `status` con muy pocos estados

No significa que nunca se puedan indexar. Significa que no conviene asumirlo automaticamente.

### Por que importa

Si un campo separa poco a los documentos, el indice puede aportar menos valor que en un campo mas discriminante.

Ejemplo:

Si casi todos los productos tienen `active: true`, un indice solo sobre `active` puede no ser una gran ayuda.

En cambio, `email` suele distinguir mucho mejor un usuario de otro.

## 14. Buen indice vs mal indice

### Buen caso

Consulta frecuente de login:

```js
db.users.findOne({ email: "ana@example.com" })
```

Indice:

```js
db.users.createIndex({ email: 1 })
```

### Por que es bueno

El campo coincide con una busqueda puntual, frecuente y muy importante para el backend.

### Caso dudoso o malo

Consulta frecuente:

```js
db.orders.find({
  userId: "u1001",
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
})
```

Indice creado:

```js
db.orders.createIndex({ createdAt: 1, userId: 1 })
```

### Por que puede ser mala decision

Porque el orden no acompaña tan bien el patron principal si la consulta nace primero desde `userId`.

Otro mal caso frecuente es este:

```js
db.products.createIndex({ active: 1 })
```

si casi todos los productos estan activos y la aplicacion rara vez consulta solo por ese campo.

Si el uso real fuera `category + active`, un mejor candidato seria evaluar un indice compuesto como `{ category: 1, active: 1 }`.

## 15. Como pensar indices con mentalidad de API

Cuando una consulta participa en un endpoint, el impacto del indice llega hasta el usuario final.

Ejemplos:

- `GET /api/users/by-email` necesita responder rapido para un panel interno
- `GET /api/orders?userId=u1001&from=...` afecta el historial de compras
- `GET /api/products?category=notebooks&active=true` alimenta listados visibles

Si esas consultas no tienen una estrategia razonable de indices:

- el endpoint se vuelve mas lento
- el servicio soporta peor el crecimiento
- una decision simple de datos termina afectando toda la experiencia

## 16. Preparando el terreno para Spring Boot

Mas adelante, cuando usemos Spring Boot, muchas consultas apareceran como:

- metodos de repositorio
- consultas derivadas
- filtros desde servicios
- endpoints que reciben parametros

Pero aunque la sintaxis cambie, la logica no cambia:

- si el backend consulta seguido por `email`, piensa en un indice para `email`
- si lista ordenes por usuario y fecha, piensa en un indice alineado con ese patron
- si filtra productos por categoria, evalua si `category` merece un indice

Aprender indices ahora evita que mas adelante tratemos problemas de rendimiento como si fueran solo problemas del framework.

## 17. Cierre

Un indice bien diseñado no es un detalle menor. Es parte del diseño de una API que quiere responder rapido y crecer sin volverse torpe.

La pregunta correcta no es:

"que indices puedo crear"

La pregunta util es:

"que consultas clave de mi backend necesitan una ruta mas eficiente"

Cuando piensas asi, los indices dejan de ser un tema aislado y pasan a ser una herramienta de diseño para servicios reales.
