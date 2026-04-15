# Teoria

## 1. Que problema resuelve este modulo

Despues de aprender documentos, colecciones y operaciones CRUD, aparece la pregunta realmente importante:

como deberia verse el dato dentro de MongoDB.

Ese punto importa mucho porque un mal modelo puede hacer que una API:

- lea demasiados documentos para responder algo simple
- duplique informacion sin criterio
- actualice datos en lugares dificiles de mantener
- termine usando MongoDB como si fuera una base relacional mal copiada

El modelado documental no consiste en "guardar JSON". Consiste en decidir que datos conviene guardar juntos, cuales deben vivir separados y por que.

## 2. Cambio de modelo mental: pensar en documentos y no en tablas

En una base de datos relacional solemos partir de entidades separadas y luego conectarlas con claves foraneas.

Ejemplo mental clasico:

- tabla `users`
- tabla `orders`
- tabla `order_items`
- tabla `addresses`

Ese enfoque funciona muy bien cuando el problema necesita relaciones fuertes y consultas cruzadas frecuentes. Pero en MongoDB la pregunta inicial suele ser otra:

que necesita leer o escribir mi backend como una unidad.

Si un endpoint de detalle de orden casi siempre devuelve:

- datos basicos del usuario
- items comprados
- direccion de entrega
- total
- estado

entonces vale la pena preguntarse si parte de esa informacion deberia vivir junta dentro del documento `orders`.

### Idea clave

En MongoDB no empiezas normalizando todo por reflejo. Empiezas observando:

- que datos viajan juntos
- que datos cambian juntos
- que consultas aparecen mas seguido

## 3. Por que los joins no son la estrategia por defecto

MongoDB puede resolver relaciones entre colecciones, pero no esta pensado para que todo dependa de joins constantes como estrategia principal de modelado.

La razon no es solo tecnica. Tambien es de diseño.

Si una API necesita responder rapido y casi siempre devuelve el mismo agregado de datos, suele ser mejor que ese agregado este prearmado en el documento o muy cerca de estarlo.

Eso reduce friccion en el backend porque evita:

- reconstruir una respuesta desde muchas piezas
- repartir una lectura frecuente en varias consultas
- depender de relaciones para casos que podrian resolverse con una sola lectura principal

### Advertencia

Esto no significa que referenciar este mal. Significa que no deberias asumir que toda relacion debe resolverse igual que en SQL.

## 4. Diseñar para consultas y no para normalizacion pura

En modelado relacional, normalizar suele ser una buena estrategia inicial porque evita duplicacion y ordena dependencias.

En MongoDB, si aplicas esa misma idea sin adaptarla, puedes terminar con demasiadas colecciones pequeñas y demasiados enlaces entre documentos.

Eso genera una consecuencia concreta: la API necesita mas trabajo para devolver algo sencillo.

### Ejemplo

Imagina `GET /api/orders/ord-9001`.

Si para responder esa ruta tu backend necesita buscar:

- la orden
- cada item
- el usuario
- la direccion
- los productos

tal vez el problema no sea solo la consulta. Tal vez el modelo esta demasiado fragmentado.

### Regla practica

En MongoDB conviene preguntar:

1. que consulta necesito resolver
2. con que frecuencia aparece
3. que estructura simplifica esa lectura sin volver imposible la escritura

## 5. Documentos embebidos

Embeder significa guardar un documento dentro de otro documento.

Ejemplo:

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "email": "ana@example.com",
  "address": {
    "street": "San Martin 123",
    "city": "Cordoba",
    "zipCode": "5000"
  }
}
```

### Cuando suele convenir

Embeder suele ser buena opcion cuando:

- el dato hijo casi siempre se lee junto con el padre
- el dato hijo no tiene vida propia importante fuera del padre
- el tamaño del dato se mantiene razonable
- la actualizacion suele ocurrir dentro del mismo agregado

### Por que conviene

Porque simplifica mucho la lectura.

Para una API de perfil de usuario, tener la direccion dentro del documento puede ser natural si casi siempre se devuelve junto con el usuario.

### Riesgo

Si embebes algo que crece sin limite o que necesita actualizarse por separado todo el tiempo, el diseño empieza a volverse incomodo.

## 6. Referencias

Referenciar significa guardar el identificador de otro documento en lugar de embederlo completo.

Ejemplo:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "productIds": ["p10", "p11"]
}
```

### Cuando suele convenir

Referenciar suele ser mejor cuando:

- el dato relacionado tiene vida propia clara
- se reutiliza desde muchos documentos
- cambia seguido y quieres evitar duplicacion excesiva
- el tamaño embebido podria crecer demasiado

### Por que conviene

Porque evita copiar informacion grande o cambiante en muchos lugares.

Un `product` suele ser reutilizado por muchos pedidos. Si cada cambio de nombre o categoria obligara a reescribir miles de ordenes, hay que pensar con cuidado que parte conviene duplicar y que parte no.

### Aclaracion importante para backend

Referenciar no significa esconder todos los datos operativos dentro de subdocumentos dificiles de consultar.

Si luego tu API necesita listar historial por usuario o filtrar por fecha, conviene que campos como `userId`, `status` y `createdAt` sigan estando accesibles de forma directa en `orders`.

## 7. Embedded vs referenced: el trade-off real

La decision no se toma por gusto. Se toma por trade-offs.

### Embebidos

Ventajas:

- lecturas mas directas
- menos necesidad de reconstruir respuestas
- modelo alineado con un agregado de negocio

Costos:

- mas duplicacion en algunos casos
- mas riesgo de documentos demasiado grandes
- mas friccion si el subdato cambia de forma independiente

### Referencias

Ventajas:

- menos duplicacion del dato central
- mejor separacion cuando cada entidad tiene ciclo de vida propio
- mas flexibilidad si la relacion crece mucho

Costos:

- lecturas mas costosas a nivel de backend
- mas pasos para reconstruir una respuesta
- mas dependencia entre colecciones

### Criterio practico

Si priorizas lecturas rapidas y frecuentes de un agregado estable, suele inclinarse hacia embebidos.

Si priorizas reutilizacion, cambios independientes o relaciones muy grandes, suele inclinarse hacia referencias.

## 8. Relaciones uno a uno en MongoDB

Una relacion uno a uno muchas veces puede embederse.

Ejemplo comun:

- usuario
- preferencias
- direccion principal

```json
{
  "_id": "u1001",
  "name": "Ana Perez",
  "preferences": {
    "language": "es",
    "notifications": true
  }
}
```

### Por que suele funcionar bien

Porque preferencias y usuario casi siempre se leen juntos y rara vez tienen sentido por separado.

### Cuando referenciar igual

Si la parte relacionada:

- cambia con otro ritmo
- tiene permisos o gestion separada
- necesita consultarse sola muy seguido

entonces puede tener sentido separarla.

## 9. Relaciones uno a muchos en MongoDB

Aqui no hay una sola respuesta. Depende mucho del tamaño y del patron de acceso.

### Caso donde conviene embeder

Un pedido con sus items:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "status": "PAID",
  "items": [
    {
      "productId": "p10",
      "name": "Mouse Bluetooth",
      "quantity": 1,
      "price": 25000
    },
    {
      "productId": "p11",
      "name": "Teclado Mecanico",
      "quantity": 1,
      "price": 78000
    }
  ]
}
```

Esto suele funcionar porque los items pertenecen al pedido y el backend normalmente los necesita junto con la orden.

### Caso donde conviene referenciar

Un usuario con millones de eventos, logs o notificaciones historicas.

En ese escenario, embeder una lista sin limite seria una mala idea. Conviene separar en otra coleccion y relacionar por `userId`.

### Regla util

Uno a muchos pequeño y acotado suele tolerar embebido.

Uno a muchos grande, cambiante o potencialmente infinito suele empujar hacia referencias.

## 10. Relaciones muchos a muchos en MongoDB

Las relaciones muchos a muchos existen tambien en MongoDB, pero no siempre se modelan como una tabla intermedia identica a SQL.

Depende del caso.

### Ejemplo

Productos y categorias.

Si un producto puede pertenecer a varias categorias y una categoria agrupa muchos productos, una opcion simple puede ser guardar en `products`:

```json
{
  "_id": "p10",
  "name": "Mouse Bluetooth",
  "categoryIds": ["cat-accessories", "cat-office"]
}
```

### Por que puede alcanzar

Porque muchas veces la consulta principal parte desde `products` y necesita saber a que categorias pertenece.

### Cuando se complica

Si la relacion tiene atributos propios importantes, por ejemplo:

- fecha de asignacion
- prioridad
- reglas de visibilidad

puede aparecer una coleccion intermedia modelada como entidad propia.

La idea no es eliminar relaciones complejas. La idea es no copiar automaticamente la estructura relacional si el backend no la necesita asi.

## 11. Estrategias de diseño segun patron de acceso

MongoDB obliga a pensar en acceso.

### Caso read-heavy

Si una API lee mucho mas de lo que escribe, suele convenir optimizar la lectura principal.

Ejemplo:

- pagina de detalle de pedido
- respuesta de perfil de usuario
- detalle de producto con atributos listos para mostrar

En estos casos, puede convenir duplicar algunos datos estables para evitar reconstruir todo en cada lectura.

### Caso write-heavy

Si una parte del sistema escribe o actualiza datos muy seguido, hay que tener mas cuidado con la duplicacion.

Ejemplo:

- stock de producto que cambia seguido
- estado operacional muy dinamico
- eventos o actividad de usuario

Si duplicas campos que cambian continuamente en demasiados documentos, luego sostener la consistencia se vuelve mas dificil.

### Concluson practica

No existe un "mejor modelo" universal. Existe un modelo mejor para un patron de lectura y escritura concreto.

## 12. Denormalizacion con criterio

Denormalizar en MongoDB no es un accidente. Muchas veces es una decision consciente.

Ejemplo tipico en `orders`:

```json
{
  "_id": "ord-9001",
  "userId": "u1001",
  "userEmail": "ana@example.com",
  "items": [
    {
      "productId": "p10",
      "productName": "Mouse Bluetooth",
      "price": 25000,
      "quantity": 1
    }
  ],
  "total": 25000
}
```

### Por que duplicar `userEmail` o `productName`

Porque la orden suele necesitar conservar una foto historica del momento de compra.

Si despues cambia el nombre del producto en `products`, eso no deberia reescribir el pasado ni romper el sentido de la orden.

### Idea importante

Duplicar no siempre es un error.

Es un error cuando duplicas sin criterio.

Es una buena decision cuando la duplicacion simplifica lecturas importantes o preserva contexto de negocio.

## 13. Escenario real: ecommerce

MongoDB encaja bien en muchos backends de ecommerce porque varias lecturas frecuentes tienen forma de agregado.

### `orders`

Suele convenir que una orden guarde:

- `userId`
- `createdAt`
- `status`
- snapshot basico del usuario necesario para la compra
- items comprados
- direccion de entrega
- total

### Por que

Porque la lectura principal de una orden casi siempre necesita todo eso junto.

Y porque esos campos operativos tambien ayudan a resolver consultas reales como:

- historial de ordenes por `userId`
- listados por `status`
- ordenamiento por fecha
- indices compuestos como `{ userId: 1, createdAt: -1 }`

### `products`

Suele convenir mantener `products` como coleccion propia porque:

- el catalogo tiene vida propia
- el stock y el precio pueden cambiar
- se reutiliza en muchas compras

### `users`

El perfil principal puede embeder datos pequenos y estables como:

- preferencias
- direcciones favoritas
- configuraciones de cuenta

pero no deberia embeder historiales gigantes de actividad solo porque "pertenecen al usuario".

## 14. Escenario real: APIs que necesitan lecturas rapidas

Muchas APIs backend viven de lecturas repetidas:

- detalle de pedido
- perfil de usuario
- listado de productos con estructura consistente

Si esos endpoints son centrales, el modelo deberia ayudarlos.

Eso no significa ignorar la escritura. Significa aceptar que el diseño tiene que responder al uso real del sistema.

Cuando un endpoint critico necesita casi siempre la misma combinacion de datos, tener esos datos preagrupados suele ser una ventaja clara.

## 15. Errores comunes de modelado

### Sobre-normalizar

Error:

Separar todo en demasiadas colecciones solo porque en SQL se haria asi.

Por que es un problema:

La API termina armando respuestas con demasiado trabajo para consultas que eran simples.

### Sobre-embeder

Error:

Meter demasiada informacion relacionada dentro de un solo documento sin pensar en tamaño, crecimiento o actualizaciones independientes.

Por que es un problema:

El documento se vuelve dificil de mantener y puede crecer mas de lo razonable.

### Arreglos sin limite

Error:

Guardar listas que pueden crecer indefinidamente dentro del mismo documento.

Ejemplos:

- historial infinito de notificaciones
- eventos de usuario de varios años
- comentarios masivos

Por que es un problema:

Lo que al principio parece comodo termina siendo una estructura dificil de leer, escribir y mantener.

### Estructura pobre del documento

Error:

Guardar documentos poco claros, con campos ambiguos o mezclando conceptos sin una forma consistente.

Por que es un problema:

El backend termina lleno de casos especiales, validaciones extra y consultas menos legibles.

## 16. Preguntas guia para diseñar mejor

Antes de cerrar un modelo, conviene pasar por estas preguntas:

1. Que endpoint o consulta quiero simplificar
2. Que datos se leen juntos casi siempre
3. Que datos cambian juntos
4. Que parte tiene vida propia clara
5. Que listas pueden crecer sin limite
6. Que duplicacion aporta valor y cual solo agrega mantenimiento

Como verificacion final, conviene sumar una pregunta mas:

7. que campos voy a necesitar dejar faciles de consultar e indexar despues

Si puedes responder eso, ya estas pensando MongoDB con criterio de backend y no solo copiando estructuras conocidas.
