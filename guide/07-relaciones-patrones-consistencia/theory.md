# Teoria

## 1. Que problema resuelve este modulo

En el modulo 04 vimos el criterio base para decidir entre documentos embebidos y referencias.

Eso alcanza para empezar. Pero en un backend real enseguida aparece una capa extra de complejidad:

- una relacion no solo hay que representarla
- tambien hay que mantenerla cuando el dato cambia
- y hay que decidir que costo aceptamos en lectura, escritura y sincronizacion

Ejemplos muy comunes:

- un usuario tiene varias direcciones
- una orden guarda items y datos del producto al momento de la compra
- un curso tiene muchos estudiantes y un estudiante puede inscribirse en muchos cursos
- un producto pertenece a categorias y tambien tiene tags para busqueda

En todos esos casos no alcanza con preguntar "que se relaciona con que".

Tambien hay que preguntar:

1. que se lee junto
2. que cambia junto
3. que cambia por separado
4. que informacion necesita conservarse como snapshot
5. que pasa si el mismo dato esta duplicado

Este modulo trabaja exactamente sobre esas decisiones.

## 2. Puente con el modulo 04

El modulo 04 ya introdujo ideas clave:

- no copiar normalizacion relacional por reflejo
- diseñar para consultas reales
- comparar embebidos contra referencias
- pensar en crecimiento de arrays y en reutilizacion del dato

Ahora avanzamos sobre ese mismo terreno.

### Que agrega este modulo

- relaciones vistas como decisiones de lectura y actualizacion
- patrones de modelado reutilizables
- consistencia entendida desde la logica de aplicacion
- duplicacion deliberada y sus consecuencias
- estrategias practicas de sincronizacion

### Que no vamos a hacer todavia

- no vamos a profundizar en transacciones
- no vamos a convertir el tema en una clase completa de sistemas distribuidos
- no vamos a repetir toda la base de embebidos vs referencias desde cero

La idea es extender el criterio, no reiniciar la explicacion.

## 3. Relaciones en MongoDB

MongoDB tambien trabaja con relaciones entre datos. La diferencia es que no obliga a representarlas del mismo modo que una base de datos relacional.

La pregunta principal no es "cual es la clave foranea".

La pregunta principal es:

como conviene representar esta relacion para que el backend lea, escriba y mantenga el dato con menos friccion.

## 3.1. Relacion uno a uno

### Concepto

Una entidad principal se relaciona con otra entidad que aparece una sola vez.

Ejemplos:

- usuario y preferencias
- usuario y perfil publico
- usuario y direccion principal

### Forma mas comun

Muchas relaciones uno a uno se resuelven muy bien con embebido.

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "mainAddress": {
    "street": "San Martin 123",
    "city": "Cordoba",
    "zipCode": "5000"
  }
}
```

### Intencion de backend

Si la parte hija:

- se lee casi siempre junto con el padre
- no necesita consultas independientes fuertes
- no crece sin control

entonces embeder suele simplificar mucho el modelo.

### Cuando puede convenir referencia

Si esa parte:

- tiene permisos o ciclo de vida propio
- cambia con mucha frecuencia por separado
- debe consultarse o administrarse de forma independiente

puede tener sentido separarla.

## 3.2. Relacion uno a muchos

### Concepto

Un documento se relaciona con varios elementos del mismo tipo o de un tipo subordinado.

Ejemplos:

- usuario con direcciones
- orden con items
- usuario con ordenes
- curso con modulos

### Variacion 1: uno a muchos con embebido

Sirve cuando el conjunto es acotado y suele leerse junto.

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "addresses": [
    {
      "addressId": "addr-home",
      "type": "home",
      "street": "San Martin 123",
      "city": "Cordoba"
    },
    {
      "addressId": "addr-office",
      "type": "office",
      "street": "Colon 450",
      "city": "Cordoba"
    }
  ]
}
```

### Variacion 2: uno a muchos con referencia

Sirve cuando la relacion puede crecer mucho o el hijo tiene vida propia clara.

`users`

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com"
}
```

`orders`

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "total": 125000
}
```

### Criterio practico

No alcanza con decir "es uno a muchos".

Tambien importa:

- cuantos hijos puede haber
- si siempre se devuelven juntos
- si los hijos se actualizan por separado
- si el crecimiento puede volverse indefinido

## 3.3. Relacion muchos a muchos

### Concepto

Cada lado puede relacionarse con muchos elementos del otro lado.

Ejemplos:

- estudiantes y cursos
- productos y categorias
- productos y tags

### Error habitual

Intentar copiar una tabla intermedia relacional sin preguntarse si el backend realmente la necesita asi.

### Representaciones practicas

#### Opcion A: ids en uno de los lados

```json
{
  "_id": "prod-10",
  "name": "Mouse Bluetooth",
  "categoryIds": ["cat-perifericos", "cat-home-office"]
}
```

Esto puede ser suficiente si:

- la lectura principal parte desde `products`
- solo necesitas saber a que categorias pertenece
- la relacion no tiene demasiados atributos propios

#### Opcion B: ids en ambos lados

`courses`

```json
{
  "_id": "course-java-01",
  "title": "Java Backend",
  "studentIds": ["stu-10", "stu-22"]
}
```

`students`

```json
{
  "_id": "stu-10",
  "name": "Lucia Gomez",
  "courseIds": ["course-java-01", "course-spring-01"]
}
```

Esto simplifica algunas lecturas, pero agrega costo de sincronizacion porque la relacion vive duplicada.

#### Opcion C: coleccion intermedia orientada al caso

`enrollments`

```json
{
  "_id": "enr-9001",
  "studentId": "stu-10",
  "courseId": "course-java-01",
  "status": "ACTIVE",
  "enrolledAt": { "$date": "2026-04-15T10:00:00Z" },
  "progressPercent": 35
}
```

Esta opcion gana fuerza cuando la relacion:

- tiene atributos propios
- necesita filtrarse o auditarse
- cambia por separado

### Idea clave

En MongoDB una relacion muchos a muchos no tiene una unica forma correcta. La estructura depende de que lectura quieres simplificar y de cuanto cuesta mantener la coherencia.

## 4. Formas practicas de representar relaciones

En la practica, una relacion en MongoDB suele representarse de una de estas formas:

## 4.1. Documento embebido

### Sintaxis conceptual

```json
{
  "_id": "u1001",
  "profile": {
    "bio": "Backend developer",
    "timezone": "America/Argentina/Cordoba"
  }
}
```

### Cuando suele convenir

- datos pequenos
- lectura conjunta frecuente
- ciclo de vida dependiente

### Riesgo

- crecimiento incomodo
- dificultad si luego quieres tratar esa parte como entidad independiente

## 4.2. Id de referencia

### Sintaxis conceptual

```json
{
  "_id": "ord-9001",
  "userId": "u1001"
}
```

### Cuando suele convenir

- entidad reutilizada por muchos documentos
- cambios independientes
- relacion grande o abierta

### Riesgo

- mas trabajo de reconstruccion en el backend

## 4.3. Referencia dentro de arrays

### Sintaxis conceptual

```json
{
  "_id": "prod-10",
  "tagIds": ["tag-ofertas", "tag-logitech"]
}
```

### Cuando suele convenir

- conjunto acotado de relaciones
- necesidad de filtrar por pertenencia
- estructura simple sin demasiados atributos propios

### Riesgo

- si el arreglo crece mucho, deja de ser una solucion comoda

## 4.4. Snapshot o duplicacion parcial

### Sintaxis conceptual

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "userSnapshot": {
    "name": "Ana Perez",
    "email": "ana@example.com"
  }
}
```

### Cuando suele convenir

- cuando la lectura principal necesita datos descriptivos inmediatos
- cuando quieres preservar contexto historico
- cuando el dato duplicado cambia poco o no debe reescribirse retroactivamente

### Riesgo

- si duplicas datos inestables sin estrategia, aparecen inconsistencias

## 5. Patrones de modelado

Los patrones no son recetas obligatorias. Son formas repetidas de resolver problemas comunes.

## 5.1. Embedding

### Que resuelve

Agrupa en un mismo documento datos que se leen y actualizan como una unidad razonable.

### Sintaxis base

```json
{
  "_id": "ord-9001",
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "quantity": 1,
      "unitPrice": 25000
    }
  ]
}
```

### Variaciones frecuentes

- subdocumento unico
- arreglo de subdocumentos
- snapshot embebido junto a claves operativas

### Cuando conviene

- lecturas read-heavy del agregado
- datos dependientes del padre
- historial que debe conservarse tal como fue

### Cuando deja de ser buena idea

- cuando el arreglo crece sin control
- cuando la parte hija se administra de forma independiente
- cuando necesitas reusar el mismo dato desde muchos lugares

## 5.2. Referencing

### Que resuelve

Separa entidades con ciclo de vida propio y evita duplicacion excesiva del dato central.

### Sintaxis base

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "itemRefs": [
    { "productId": "prod-10", "quantity": 1 }
  ]
}
```

### Variaciones frecuentes

- referencia simple por id
- array de ids
- documento aparte para la relacion

### Cuando conviene

- el dato cambia seguido
- el dato se comparte entre muchos documentos
- el conjunto relacionado puede crecer mucho

### Costo principal

Cada lectura completa puede necesitar mas trabajo de ensamblado en el backend o mediante agregacion.

## 5.3. Subset pattern

### Idea

Guardar en el documento principal solo la porcion mas util o mas consultada de un conjunto mayor.

### Problema que resuelve

Hay casos donde:

- necesitas acceso rapido a una parte pequena
- pero no quieres embeder el conjunto completo porque puede crecer demasiado

### Ejemplo conceptual

`products`

```json
{
  "_id": "prod-10",
  "name": "Notebook Pro 14",
  "topReviews": [
    {
      "reviewId": "rev-1",
      "rating": 5,
      "title": "Excelente bateria"
    },
    {
      "reviewId": "rev-2",
      "rating": 5,
      "title": "Muy buena para trabajo"
    }
  ],
  "reviewsCount": 1824
}
```

`reviews`

```json
{
  "_id": "rev-1",
  "productId": "prod-10",
  "rating": 5,
  "title": "Excelente bateria",
  "comment": "La use dos semanas y rindio perfecto"
}
```

### Intencion de backend

El listado o detalle rapido del producto no necesita cargar 1824 reviews. Le alcanza con:

- algunas reviews destacadas
- el conteo total
- luego una consulta aparte si el usuario entra a ver todas

### Beneficio

Combina lectura rapida con crecimiento controlado.

### Riesgo

Debes decidir bien:

- que parte entra en el subset
- cuando se recalcula
- que pasa cuando cambia el conjunto real

## 5.4. Extended reference

### Idea introductoria

Es una referencia que no guarda solo el id. Tambien guarda algunos campos descriptivos estables para evitar lecturas adicionales.

### Sintaxis conceptual

```json
{
  "_id": "prod-10",
  "name": "Mouse Bluetooth",
  "category": {
    "categoryId": "cat-perifericos",
    "name": "Perifericos"
  }
}
```

O en una orden:

```json
{
  "_id": "ord-9001",
  "items": [
    {
      "productId": "prod-10",
      "productName": "Mouse Bluetooth",
      "quantity": 1,
      "unitPrice": 25000
    }
  ]
}
```

### Que resuelve

- mantiene una referencia operativa estable
- evita ir a otra coleccion para mostrar un nombre o un dato descriptivo

### Nivel de este modulo

No hace falta tratarlo como patron avanzado. Aqui alcanza con entenderlo como una idea muy util:

referenciar no siempre significa guardar solo el id. A veces conviene guardar id mas algunos campos seleccionados.

### Advertencia

Los campos extra deben ser elegidos con cuidado. Si duplicas demasiados campos inestables, el patron pierde valor y solo suma mantenimiento.

## 5.5. Duplicacion deliberada

### Idea

En MongoDB duplicar no siempre es un error. A veces es una decision de diseño para mejorar lecturas, preservar contexto o simplificar respuestas.

### Ejemplo valido

Una orden puede guardar:

- `userId`
- `userSnapshot.name`
- `userSnapshot.email`
- `items[].productId`
- `items[].productName`
- `items[].unitPrice`

### Por que no es necesariamente incorrecto

Porque la orden representa un hecho historico. No necesariamente quieres que cada cambio actual en `users` o `products` reescriba el pasado.

### Pregunta que debes hacerte

El dato duplicado:

- es historico
- es relativamente estable
- mejora una lectura critica
- o requiere estar perfectamente sincronizado siempre

Si la ultima opcion domina, la duplicacion se vuelve mas peligrosa.

## 6. Consistencia en bases documentales

En este modulo vamos a usar "consistencia" en un sentido practico:

que el dato que usa tu backend mantenga una coherencia suficiente para el caso de uso.

No hace falta convertir esto en una teoria formal completa.

## 6.1. Consistencia a nivel de aplicacion

### Idea

Cuando duplicas o distribuyes informacion en varios documentos, muchas garantias dejan de depender solo del esquema y pasan a depender de la aplicacion.

Ejemplo:

si cambias el nombre de una categoria y ese nombre esta duplicado en muchos productos, alguien debe decidir:

- si se actualiza todo
- si se deja historico
- si se recalcula solo en algunos lugares

Esa decision vive en la logica del sistema.

### Preguntas utiles para backend

1. que dato debe estar siempre sincronizado
2. que dato puede tolerar un pequeño retraso
3. que dato debe quedar congelado por valor historico
4. quien ejecuta la actualizacion: servicio, job o proceso administrativo

## 6.2. Trade-off entre duplicacion y sincronizacion

Duplicar un dato puede simplificar mucho la lectura.

Pero cada duplicacion agrega una nueva pregunta:

como se mantiene coherente cuando el origen cambia.

### Dos extremos

#### Modelo muy normalizado

- menos duplicacion
- mas referencias
- lecturas mas caras o mas complejas

#### Modelo muy duplicado

- lecturas mas simples
- mas puntos a sincronizar
- mas riesgo de desalineacion

### Criterio sano

No busques "cero duplicacion" ni "todo embebido".

Busca el punto donde:

- la lectura principal sea razonable
- la escritura siga siendo mantenible
- la estrategia de actualizacion sea clara

## 6.3. Consistencia eventual a nivel introductorio

### Idea

A veces aceptas que no todos los documentos reflejen el cambio al mismo tiempo exacto.

Eso se conoce, de forma simple, como consistencia eventual.

### Ejemplo practico

Cambias el nombre de una categoria de producto.

Puede pasar que:

- `categories` se actualice primero
- algunos productos se actualicen unos segundos despues
- mientras tanto convivan temporalmente ambos valores

### Cuando puede ser aceptable

- el dato no es critico al milisegundo
- el sistema tiene una forma clara de converger al estado correcto
- el usuario no sufre un error grave por ese lapso

### Cuando conviene evitarlo

- montos
- estados sensibles
- datos regulatorios
- operaciones donde una diferencia temporal rompe reglas de negocio

En este modulo alcanza con entender la idea y usarla con prudencia.

## 6.4. Cuando la consistencia debe influir en el modelo

La consistencia no se discute al final. Influye desde el modelado.

### Senales de alerta

- el dato cambia seguido
- el mismo campo aparece duplicado en muchos documentos
- una inconsistencia pequena genera bugs visibles
- no tienes una estrategia clara de sincronizacion

### Consecuencia de diseño

Si detectas esas senales, tal vez convenga:

- referenciar en lugar de duplicar
- duplicar solo un subset estable
- separar datos historicos de datos operativos
- evitar que la relacion se replique en varios lados

## 7. Patrones de actualizacion

Modelar relaciones no termina en la lectura. Tambien debes pensar como se actualizan.

## 7.1. Actualizar un documento

### Escenario tipico

Usuario con direcciones embebidas.

```js
db.users.updateOne(
  { _id: "u1001", "addresses.addressId": "addr-home" },
  {
    $set: {
      "addresses.$.street": "San Martin 125",
      "addresses.$.updatedAt": new Date()
    }
  }
)
```

### Ventaja

La informacion relacionada vive dentro del mismo agregado y la actualizacion es directa.

### Cuando esto ayuda

- cambios locales
- pocos subdocumentos
- dato claramente dependiente del padre

## 7.2. Actualizar varios documentos relacionados

### Escenario tipico

Cambio del nombre de una categoria que esta duplicado en `products`.

Primero actualizas la categoria:

```js
db.categories.updateOne(
  { _id: "cat-perifericos" },
  { $set: { name: "Perifericos y Accesorios" } }
)
```

Despues actualizas productos afectados:

```js
db.products.updateMany(
  { "category.categoryId": "cat-perifericos" },
  { $set: { "category.name": "Perifericos y Accesorios" } }
)
```

### Problema que aparece

La coherencia ya no depende de una sola escritura. Depende de una secuencia o de una estrategia posterior.

## 7.3. Riesgos cuando hay datos duplicados

Si duplicas sin estrategia, aparecen estos problemas:

- una pantalla muestra nombre viejo y otra nombre nuevo
- un job corrige solo una parte de los documentos
- un servicio actualiza el origen pero olvida los snapshots operativos
- el equipo deja de saber cual es la fuente de verdad

### Regla practica

Cada dato duplicado deberia tener una respuesta clara para estas preguntas:

1. cual es el origen
2. si debe sincronizarse o quedar historico
3. quien lo actualiza
4. con que momento o frecuencia

## 7.4. Estrategias simples para mantener sincronizacion

No vamos a profundizar en transacciones. Pero si conviene pensar estrategias.

### Estrategia 1: no sincronizar porque el dato es historico

Caso:

- ordenes con `productName` y `unitPrice`

Decision:

- no se reescribe cuando cambia `products`

Motivo:

- la orden representa el estado al momento de compra

### Estrategia 2: sincronizacion inmediata en la misma operacion de aplicacion

Caso:

- actualizas categoria y productos relacionados dentro del flujo del servicio

Motivo:

- quieres reducir la ventana de desalineacion

Costo:

- mas complejidad de escritura

### Estrategia 3: sincronizacion diferida

Caso:

- se actualiza el dato origen y luego un proceso secundario propaga el cambio

Motivo:

- priorizas desacoplar la escritura principal

Costo:

- aceptas consistencia eventual

### Estrategia 4: duplicar solo campos estables

Caso:

- guardas `categoryId` y `category.name`, pero no toda la categoria

Motivo:

- mejoras lectura sin multiplicar demasiado el costo de sincronizar

## 8. Escenarios practicos de backend

## 8.1. Usuario con direcciones

### Decision frecuente

Embeder direcciones cuando:

- el numero esperado es pequeno
- se editan desde el perfil del usuario
- suelen devolverse juntas

### Alerta

Si empiezas a guardar:

- miles de direcciones historicas
- validaciones o workflows propios por direccion
- direcciones compartidas entre cuentas

tal vez la relacion ya no deba vivir totalmente embebida.

## 8.2. Orden con order items

### Decision frecuente

Embeder los items dentro de la orden.

### Motivo

- pertenecen a la orden
- se leen junto con ella
- la orden suele necesitar snapshot historico del producto y del precio

### Buena practica

Guardar en cada item:

- `productId`
- `productName`
- `quantity`
- `unitPrice`

Eso combina referencia operativa con contexto historico.

## 8.3. Plataforma de cursos con estudiantes e inscripciones

### Decision frecuente

No embeder todos los estudiantes dentro del curso ni todos los cursos dentro del estudiante si la relacion puede crecer mucho y tiene estado propio.

### Modelo razonable

Usar una coleccion `enrollments`.

Motivo:

- la relacion tiene atributos como `status`, `progressPercent`, `enrolledAt`
- la cantidad puede crecer
- quieres consultar inscripciones por estudiante o por curso

## 8.4. Catalogo de productos con categorias y tags

### Posible combinacion

- `categories`: coleccion separada
- `products.category`: referencia extendida con id y nombre
- `products.tags`: array acotado de strings o ids

### Motivo

- las categorias tienen vida propia
- el detalle o listado de producto necesita mostrar nombre de categoria sin trabajo extra
- los tags suelen servir mas como clasificacion ligera o filtro

## 9. Errores comunes

## 9.1. Copiar diseño relacional literalmente

### Sintoma

Una orden solo guarda ids de todo:

- `userId`
- `addressId`
- `itemIds`
- `pricingId`
- `statusId`

### Problema

La lectura principal queda fragmentada y el backend necesita demasiados pasos para devolver algo simple.

## 9.2. Crear demasiadas referencias

### Sintoma

Cada parte pequena del modelo vive en otra coleccion aunque casi siempre se consulta junto.

### Problema

- sobrecarga de ensamblado
- mas complejidad de consulta
- perdida de la ventaja documental

## 9.3. Duplicar datos inestables sin estrategia

### Sintoma

Copias nombres, estados o atributos que cambian seguido en muchos documentos.

### Problema

Las inconsistencias dejan de ser accidentales y pasan a ser inevitables.

## 9.4. Dejar crecer arrays sin control

### Sintoma

Embebes:

- ordenes historicas
- comentarios infinitos
- eventos de auditoria por anos

### Problema

El documento crece de forma incomoda y termina mezclando datos que no deberian compartirse como una sola unidad.

## 10. Guia de decision rapida

Antes de cerrar un modelo, conviene revisar estas preguntas:

1. Este dato se lee casi siempre junto con el documento padre.
2. Este dato cambia junto con el padre o tiene vida propia.
3. La cantidad relacionada es pequena, mediana o potencialmente ilimitada.
4. Si duplico este campo, debe quedar historico o sincronizado.
5. Si debe sincronizarse, quien lo hara.
6. Una inconsistencia temporal seria aceptable o romperia el negocio.
7. Estoy modelando para una lectura real del backend o para imitar una tabla.

Si puedes responder esas preguntas con claridad, normalmente el diseño ya esta mucho mejor encaminado.

## 11. Resumen del modulo

Las ideas mas importantes de este modulo son:

- MongoDB tambien modela relaciones, pero no obliga a resolverlas como SQL
- embeder, referenciar y duplicar son herramientas, no dogmas
- los patrones como `subset` y referencia extendida ayudan a resolver lecturas reales
- la consistencia debe pensarse junto con el modelo, no despues
- cada dato duplicado necesita una estrategia: historico, sincronizado o eventualmente sincronizado
- muchos errores de backend nacen de modelos que no pensaron como cambian los datos
