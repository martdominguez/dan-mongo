# Teoria

## 1. Que problema resuelve MongoDB

Muchos backends necesitan trabajar con datos que se leen como una unidad. Pasa seguido con:

- perfiles de usuario con preferencias y direcciones
- pedidos con items y estado
- tickets con historial y comentarios
- catalogos con atributos que cambian segun el producto

En una base de datos relacional, estos casos suelen repartirse en varias tablas. Eso no es un problema en si mismo. De hecho, muchas veces es la mejor decision. El punto es que algunas APIs necesitan recuperar casi todo ese conjunto junto, una y otra vez.

MongoDB resulta util en ese escenario porque trabaja con documentos. Un documento puede representar una entidad con sus datos relacionados en una estructura cercana a la que luego consume el backend.

Ejemplo simple:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "items": [
    { "productId": "p10", "quantity": 1, "price": 25000 },
    { "productId": "p11", "quantity": 1, "price": 78000 }
  ],
  "total": 103000
}
```

Si una API de pedidos suele devolver exactamente ese agregado, MongoDB ya empieza a tener sentido como opcion.

## 2. Que es MongoDB

MongoDB es una base de datos orientada a documentos. En lugar de guardar filas dentro de tablas, guarda documentos dentro de colecciones.

La unidad principal de trabajo es el documento. Un documento representa datos estructurados en pares clave-valor y puede incluir:

- campos simples
- objetos anidados
- arreglos

Ejemplo:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "preferences": {
    "language": "es",
    "notifications": true
  }
}
```

La idea importante no es solo el formato. Lo importante es que la estructura del documento puede parecerse mucho a la forma en que el backend necesita leer y escribir los datos.

## 3. Que significa NoSQL en la practica

NoSQL no significa "sin consultas" ni "sin estructura". En la practica, suele significar:

- no depender del modelo relacional clasico como unica forma de organizar datos
- aceptar estructuras mas flexibles
- diseñar segun patrones reales de lectura y escritura
- asumir trade-offs distintos en modelado, consistencia y escalabilidad

Para un desarrollador backend, la traduccion util de "NoSQL" es esta:

- como se van a leer los datos desde la API
- que datos viajan juntos con frecuencia
- que partes cambian juntas
- que operaciones exigen mayor consistencia

Ejemplo:

Si un pedido casi siempre se consulta con sus items, su direccion de entrega y su estado, puede ser mas natural modelarlo como un documento. Si en cambio el problema depende de muchas relaciones fuertes y consultas cruzadas constantes, una base de datos relacional puede ser mejor opcion.

MongoDB no elimina la necesidad de diseñar bien. Cambia el tipo de decisiones que hacemos.

## 4. Base de datos, coleccion y documento

En MongoDB conviene pensar estos tres niveles juntos.

### Base de datos

Es el contenedor general. Una aplicacion puede tener una base de datos llamada `ecommerce`.

### Coleccion

Es un conjunto de documentos del mismo dominio. Por ejemplo:

- `users`
- `orders`
- `products`
- `tickets`

Una coleccion se parece a una tabla solo a nivel intuitivo. La diferencia importante es que no exige que todos los documentos tengan exactamente la misma forma.

### Documento

Es la unidad de almacenamiento. Un documento representa una entidad concreta, por ejemplo un usuario o un pedido.

Ejemplo:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "addresses": [
    {
      "type": "home",
      "city": "Cordoba",
      "street": "San Martin 123"
    }
  ],
  "active": true
}
```

Este ejemplo muestra dos ideas centrales:

- `_id` identifica al documento de forma unica
- los datos relacionados pueden viajar juntos sin repartirlos en varias tablas

## 5. Como funciona `_id`

Cada documento tiene un campo especial llamado `_id`. Ese campo identifica al documento de manera unica dentro de su coleccion.

En la practica, `_id` cumple un papel parecido al de una clave primaria en una base de datos relacional:

- identifica un documento sin ambiguedad
- permite buscarlo de forma directa
- impide que existan dos documentos con el mismo identificador en la misma coleccion

Si no defines `_id` al insertar un documento, MongoDB crea uno automaticamente. Lo mas comun es que ese valor sea un `ObjectId`.

### Por que MongoDB usa una clave subrogada

En modelado de datos, una clave primaria tiene una regla central: no deberia cambiar.

A veces el dominio parece ofrecer una clave natural, por ejemplo:

- un email
- un DNI
- un codigo de cliente

El problema es que un dato del dominio puede dejar de ser buena clave con el tiempo:

- puede cambiar
- puede dejar de ser unico
- puede depender de reglas de negocio que evolucionan

Por eso suele ser mas seguro usar una clave subrogada. Una clave subrogada no tiene significado de negocio. Su objetivo es otro:

- ser unica
- ser estable
- servir como identificador tecnico

MongoDB usa `ObjectId` como una opcion muy comun para ese rol.

### Por que no usar un autonumerico tradicional

En una base de datos relacional centralizada, un autonumerico puede ser suficiente. El motor genera `1`, `2`, `3`, `4` y asi sucesivamente.

En un entorno distribuido, esa estrategia se vuelve mas costosa. Para generar el siguiente numero sin colisiones, distintos procesos o nodos necesitan coordinarse.

Eso introduce problemas practicos:

- hay que determinar cual fue el ultimo valor valido
- varios procesos pueden competir por el siguiente numero
- la coordinacion agrega costo y complejidad
- ese punto de asignacion puede convertirse en cuello de botella

MongoDB evita ese problema con un identificador que puede generarse de forma descentralizada y que sigue siendo muy probable que sea unico.

Ejemplo legible para aprender:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com"
}
```

Ejemplo comun en un proyecto real:

```json
{
  "_id": { "$oid": "661c1f9d8f9a4e2b7c123456" },
  "name": "Ana Perez",
  "email": "ana@example.com"
}
```

No hace falta memorizar el formato interno del `ObjectId` en este modulo. Lo importante es entender su uso cotidiano.

### Como se compone un `ObjectId`

Un `ObjectId` ocupa 12 bytes y suele verse como una cadena hexadecimal de 24 caracteres.

Ejemplo:

```json
{
  "_id": { "$oid": "661c1f9d8f9a4e2b7c123456" }
}
```

Esos 24 caracteres hexadecimales representan 12 bytes con esta estructura:

1. 4 bytes de timestamp
2. 5 bytes de valor aleatorio unico por proceso cliente
3. 3 bytes de contador incremental por proceso

### 1. Timestamp de 4 bytes

Los primeros 4 bytes representan el momento de creacion del `ObjectId`, medido en segundos desde la Unix epoch.

Esto aporta dos ventajas practicas:

- ayuda a que los `ObjectId` tiendan a crecer con el tiempo
- permite recuperar aproximadamente el instante de creacion

Por eso muchas veces ordenar por `_id` produce un resultado parecido a ordenar por fecha de creacion.

Pero hay un matiz importante: no es un orden perfecto.

No se puede garantizar al 100% porque:

- la resolucion temporal es de un segundo
- distintos clientes pueden generar ids dentro del mismo segundo
- los relojes de distintas maquinas pueden no estar perfectamente sincronizados

### 2. Valor aleatorio de 5 bytes

Los siguientes 5 bytes son un valor aleatorio generado una vez por proceso cliente.

La documentacion oficial actual lo describe como un valor unico para la maquina y el proceso que lo genera. Su objetivo es reducir la probabilidad de colision entre procesos diferentes sin obligarlos a coordinarse entre si.

La idea practica es esta:

- dos procesos distintos no necesitan preguntarse mutuamente que id van a usar
- cada proceso ya tiene una parte propia en la composicion del `ObjectId`

### 3. Contador incremental de 3 bytes

Los ultimos 3 bytes forman un contador incremental por proceso.

Ese contador:

- se inicializa con un valor aleatorio
- aumenta con cada nuevo `ObjectId` generado por ese proceso
- se reinicia cuando el proceso se reinicia

Esto permite generar muchos ids dentro del mismo segundo sin depender solo del timestamp.

### Que garantiza esta composicion

La combinacion de timestamp, valor unico por proceso y contador incremental hace que el `ObjectId` sea:

- pequeño
- rapido de generar
- muy probable que sea unico
- aproximadamente ordenable por tiempo de creacion

Eso es justo lo que lo vuelve util como identificador tecnico en sistemas distribuidos.

### Nota importante sobre material mas antiguo

Es comun encontrar explicaciones mas viejas que describen el `ObjectId` como:

- 4 bytes de timestamp
- 3 bytes de identificador de maquina
- 2 bytes de PID
- 3 bytes de contador

Esa explicacion corresponde a implementaciones historicas. La documentacion oficial actual de MongoDB describe la composicion como `4 bytes de timestamp + 5 bytes aleatorios por proceso + 3 bytes de contador`.

Consulta directa en `mongosh`:

```js
db.users.findOne({ _id: "u1001" })
```

En backend, esto importa porque una ruta como `GET /api/users/u1001` normalmente termina buscando una entidad por su identificador.

Buena practica inicial:

- si estas aprendiendo, piensa `_id` como el identificador unico del documento
- en muchos casos conviene dejar que MongoDB genere `ObjectId`
- mas adelante vas a ver cuando puede tener sentido usar un identificador propio del dominio

## 6. BSON: que es y por que importa

MongoDB trabaja internamente con BSON, que significa Binary JSON.

La idea practica es esta:

- los documentos se piensan y se leen de una forma parecida a JSON
- MongoDB los almacena y procesa en BSON

La diferencia importa porque BSON agrega tipos de datos utiles que JSON puro no maneja bien, por ejemplo:

- fechas
- identificadores como `ObjectId`
- numeros con distintos tamaños
- datos binarios

Ejemplo:

```json
{
  "_id": "ord-9010",
  "status": "CREATED",
  "createdAt": { "$date": "2026-04-14T12:00:00Z" },
  "total": 45000
}
```

Ese `createdAt` no es solo texto. Para el backend eso importa mucho porque permite filtrar, ordenar y comparar correctamente.

```js
db.orders.find({
  createdAt: { $gte: ISODate("2026-04-01T00:00:00Z") }
}).sort({ createdAt: -1 })
```

Cuando luego trabajemos con una aplicacion backend, estos tipos van a impactar directamente en como se representan fechas, ids y numeros.

## 7. Diferencias con bases relacionales

La mejor manera de comparar ambos modelos es mirar como piensan los datos.

### Estructura

En una base de datos relacional, la estructura suele definirse antes con tablas, columnas y relaciones.

En MongoDB, la estructura vive principalmente en el diseño de documentos y en las reglas que defina el equipo o la aplicacion.

### Relaciones

En SQL es natural modelar relaciones con claves foraneas y joins.

En MongoDB es comun decidir entre:

- embeber datos dentro del documento
- referenciar otros documentos

Ejemplo mental rapido:

- si el backend casi siempre lee el pedido con sus items, embeber puede ser razonable
- si una entidad necesita mucha vida propia y cambios independientes, referenciar puede tener mas sentido

Mas adelante vamos a trabajar ese criterio con detalle. Por ahora alcanza con entender que MongoDB favorece modelar segun como la aplicacion lee y actualiza los datos.

### Lectura de datos

En un backend relacional, una respuesta de API puede requerir varias tablas y joins.

En MongoDB, muchas veces se diseña el documento para que esa lectura salga de una consulta directa o de una agregacion acotada.

Comparacion breve:

Enfoque relacional:

- tabla `orders`
- tabla `order_items`
- tabla `users`
- tabla `addresses`

Enfoque documental:

```json
{
  "_id": "ord-9002",
  "userId": "u1002",
  "status": "SHIPPED",
  "items": [
    { "productId": "p30", "quantity": 2, "price": 15000 }
  ],
  "total": 30000
}
```

La diferencia no es solo tecnica. Es una diferencia de modelado basada en como la aplicacion usa los datos.

### Cambios de esquema

En SQL, agregar o cambiar columnas suele implicar migraciones mas formales.

En MongoDB, la evolucion del esquema puede ser mas gradual. Eso da flexibilidad, pero tambien exige disciplina para no terminar con datos inconsistentes entre documentos parecidos.

### Resumen practico

Una base de datos relacional suele destacar cuando:

- hay muchas relaciones fuertes entre entidades
- la integridad es muy estricta
- las transacciones complejas son centrales

MongoDB suele destacar cuando:

- el backend lee agregados completos con frecuencia
- la estructura cambia con el tiempo
- hay documentos con datos anidados o listas naturales
- conviene reducir joins para los casos de lectura comunes

## 8. Casos de uso reales en backend

### Usuarios con perfil flexible

Si una API necesita guardar configuraciones, preferencias, dispositivos o direcciones de un usuario, un documento puede representar bien esa estructura sin repartirla en muchas tablas desde el inicio.

### Pedidos de ecommerce

Un pedido suele incluir:

- datos del cliente
- direccion de entrega
- lista de items
- estado del pedido
- eventos del proceso

Gran parte de esa informacion se consulta junta. Eso hace que MongoDB sea una opcion natural en varios backends de ecommerce.

### Catalogos de productos

No todos los productos comparten exactamente los mismos atributos. Una notebook, una silla y un monitor no tienen las mismas propiedades.

Ejemplo:

```json
{
  "_id": "p20",
  "name": "Notebook Pro 14",
  "category": "notebooks",
  "attributes": {
    "ramGb": 16,
    "storageGb": 512
  }
}
```

```json
{
  "_id": "p21",
  "name": "Silla Ergonomica",
  "category": "furniture",
  "attributes": {
    "material": "mesh",
    "color": "black"
  }
}
```

Un modelo documental tolera mejor esa variacion.

### Sistemas de tickets o soporte

Un ticket puede incluir comentarios, historial, etiquetas y cambios de estado. Es un buen ejemplo de entidad con subestructuras naturales.

## 9. Cuando usar MongoDB

MongoDB tiene sentido cuando:

- quieres modelar datos como agregados o documentos completos
- la API lee casi siempre la entidad junto con sus datos anidados
- hay atributos opcionales o variables segun el caso
- el dominio evoluciona y necesita cierta flexibilidad
- el equipo entiende que la flexibilidad no reemplaza el diseño

Ejemplo concreto:

Si una API de pedidos casi siempre devuelve el pedido con sus items, direccion, total y estado, es razonable pensar ese agregado como un documento.

## 10. Cuando no usar MongoDB

MongoDB no es automaticamente la mejor opcion.

Conviene evaluar otra tecnologia cuando:

- el dominio depende mucho de joins complejos y frecuentes
- la consistencia transaccional entre muchas entidades es central
- el modelo relacional ya representa muy bien el problema
- el equipo necesita reportes SQL pesados desde el primer dia
- se quiere usar MongoDB solo por moda, sin un caso claro

Ejemplo concreto:

Un sistema contable o financiero con reglas muy estrictas, relaciones fuertes y auditoria detallada suele encajar mejor en una base de datos relacional.

## 11. Error comun: pensar que MongoDB es "guardar JSON y listo"

Ese enfoque trae problemas rapido:

- documentos gigantes
- duplicacion sin criterio
- campos inconsistentes
- consultas dificiles de mantener

La buena practica es pensar siempre en:

1. como se leen los datos
2. como se actualizan
3. que partes cambian juntas
4. que nivel de consistencia necesita el negocio

## 12. Conclusion

MongoDB no reemplaza a las bases de datos relacionales en todos los casos. Es otra herramienta, con otro modelo y otros trade-offs.

Para un desarrollador backend, la pregunta correcta no es "SQL o NoSQL". La pregunta util es: "que forma de modelar datos hace mas simple, segura y mantenible la aplicacion que estoy construyendo?"

En los proximos modulos vamos a pasar de la idea general a la practica: entorno, shell, operaciones y modelado.
