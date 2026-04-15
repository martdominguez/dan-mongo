# Teoria

## 1. Que problema resuelve este modulo

MongoDB permite trabajar con documentos flexibles. Esa flexibilidad es una ventaja, pero tambien introduce riesgos si el backend no define limites claros.

Problemas frecuentes en proyectos reales:

- entran documentos con campos faltantes o con tipos inconsistentes
- varios documentos deben actualizarse juntos y el flujo queda a mitad de camino
- la aplicacion se conecta con un usuario que puede leer, modificar o borrar demasiado
- una operacion administrativa mal filtrada termina afectando datos de produccion

Este modulo trabaja sobre esos riesgos desde tres frentes distintos:

1. validacion para proteger calidad del dato
2. transacciones para coordinar cambios relacionados
3. seguridad basica para limitar accesos y reducir daño potencial

La idea no es convertir MongoDB en una base relacional ni tratar cada caso como un problema de infraestructura avanzada. La idea es incorporar criterio de backend para que la base de datos sea mas confiable.

## 2. Puente con los modulos anteriores

Los modulos anteriores construyeron la base para llegar a este punto:

- el modulo 04 mostro que modelar bien reduce friccion operativa
- el modulo 05 explico que cada decision tiene costo de escritura, lectura y mantenimiento
- el modulo 06 mostro como consultar y transformar datos
- el modulo 07 explico que la consistencia depende mucho de como representamos relaciones y duplicaciones

Este modulo continua esa linea.

### Idea clave

Cuando el modelo esta bien pensado:

- menos datos quedan repartidos innecesariamente
- menos operaciones necesitan tocar varias colecciones
- menos veces hace falta una transaccion

Por eso conviene entender desde el comienzo que una transaccion no corrige un mal modelado. En muchos casos solo lo vuelve mas caro.

## 3. Validacion

## 3.1. Que problema resuelve

En MongoDB no todos los documentos de una coleccion tienen que compartir exactamente la misma estructura. Eso da flexibilidad, pero tambien puede degradar la calidad del dato si no existe ningun control.

Ejemplos de problemas habituales:

- un pedido llega sin `status`
- `total` se guarda como string en unos documentos y como numero en otros
- un item del pedido tiene `quantity: -3`
- un backend nuevo empieza a escribir un formato distinto sin querer

La validacion a nivel de coleccion ayuda a detectar esos casos antes de que queden persistidos.

## 3.2. Idea general

La validacion define reglas minimas que un documento debe cumplir para poder insertarse o actualizarse en una coleccion.

En este nivel introductorio conviene pensarla como una red de seguridad para estructura y calidad basica:

- campos obligatorios
- tipos esperados
- reglas simples sobre rangos o valores permitidos

No conviene cargarla desde el principio con toda la logica de negocio.

## 3.3. Por que importa aunque la aplicacion ya valide

Un error muy comun es confiar solo en la validacion del backend.

Eso parece suficiente al principio, pero en la practica aparecen varios puntos de entrada:

- otro servicio escribe en la misma coleccion
- una tarea batch inserta documentos
- un script de mantenimiento modifica datos
- un desarrollador ejecuta comandos manuales en `mongosh`

Si la unica barrera vive en la aplicacion, la base sigue expuesta a documentos invalidos desde cualquier otro canal.

### Idea clave

La validacion de aplicacion y la validacion de coleccion no compiten.

Se complementan:

- la aplicacion valida contexto de negocio y experiencia de uso
- la base valida estructura minima y consistencia basica del documento

## 3.4. Validacion de esquema a nivel de coleccion

### Concepto

MongoDB permite asociar un validador a una coleccion.

En un nivel introductorio, la forma mas comun es usar `validator` con reglas basadas en `$jsonSchema`.

### Sintaxis general

```js
db.createCollection("orders", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["userId", "status", "items", "total"],
      properties: {
        userId: { bsonType: "string" },
        status: { enum: ["PENDING", "PAID", "CANCELLED"] },
        items: { bsonType: "array" },
        total: { bsonType: "number", minimum: 0 }
      }
    }
  }
})
```

### Intencion del desarrollador

La intencion no es describir todos los detalles posibles. La intencion es dejar explicito que la coleccion espera documentos con una forma minima razonable.

Eso protege:

- a la aplicacion actual
- a futuras integraciones
- a scripts manuales
- a nuevos integrantes del equipo

## 3.5. Componentes basicos que conviene dominar

## 3.5.1. `bsonType`

### Concepto

Indica el tipo BSON esperado para el documento o para un campo.

### Variaciones comunes

- `"object"` para el documento raiz o subdocumentos
- `"string"` para textos y codigos
- `"number"` para aceptar enteros o decimales
- `"int"`, `"long"`, `"decimal"` cuando el caso necesita mas precision
- `"array"` para listas
- `"bool"` para booleanos
- `"date"` para fechas

### Cuando usar una variante mas amplia o mas estricta

Si solo necesitas validar que `total` sea numerico, `number` suele ser suficiente en un curso introductorio.

Si el sistema necesita precision monetaria fuerte, mas adelante puede tener sentido usar `decimal`. Pero esa decision ya pertenece a un nivel de detalle mayor.

## 3.5.2. `required`

### Concepto

Lista los campos que deben existir en el documento.

### Ejemplo

```js
required: ["userId", "status", "items", "total"]
```

### Que protege

Evita documentos parcialmente incompletos que obliguen a la aplicacion a adivinar estados o valores faltantes.

### Advertencia

`required` exige presencia. No resuelve por si solo si el valor tiene sentido.

Por ejemplo:

- un campo puede existir pero venir vacio
- un numero puede estar presente pero ser negativo

Por eso suele combinarse con restricciones adicionales.

## 3.5.3. `properties`

### Concepto

Permite definir reglas por campo.

### Ejemplo

```js
properties: {
  status: {
    bsonType: "string",
    enum: ["PENDING", "PAID", "CANCELLED"]
  }
}
```

### Intencion

Hace visible la forma del documento de manera declarativa y reusable como referencia tecnica del equipo.

## 3.5.4. `enum`

### Concepto

Restringe un campo a un conjunto acotado de valores.

### Cuando aporta mucho

Campos como:

- `status`
- `role`
- `paymentMethod`
- `environment`

### Beneficio practico

Reduce estados improvisados como:

- `"paid"`
- `"Paid"`
- `"pagado"`
- `"PAYED"`

Todos esos desalineamientos suelen romper filtros, reportes o logica de backend.

## 3.5.5. Reglas basicas como `minimum`

### Concepto

Permiten expresar restricciones numericas simples.

### Ejemplos de uso razonable

- `quantity` no puede ser menor que 1
- `total` no puede ser negativo
- `discountPercent` no puede ser menor que 0

### Advertencia

Estas reglas sirven para proteger invariantes simples. No reemplazan por completo reglas compuestas del dominio.

## 3.6. Ejemplo guiado: validacion de ordenes

Supongamos una coleccion `orders` con este uso esperado:

- el pedido siempre tiene usuario
- debe existir un estado
- debe haber al menos un item
- el total no puede ser negativo

Una validacion introductoria podria verse asi:

```js
db.createCollection("orders", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["userId", "status", "items", "total", "createdAt"],
      properties: {
        userId: {
          bsonType: "string",
          description: "Identificador del usuario que genera la orden"
        },
        status: {
          bsonType: "string",
          enum: ["PENDING", "PAID", "CANCELLED"]
        },
        items: {
          bsonType: "array"
        },
        total: {
          bsonType: "number",
          minimum: 0
        },
        createdAt: {
          bsonType: "date"
        }
      }
    }
  }
})
```

### Que resuelve

- evita pedidos sin campos esenciales
- evita `total` negativo
- evita estados inesperados
- deja mas clara la estructura oficial de la coleccion

### Que no resuelve todavia

- que la suma de los items coincida con `total`
- que haya stock suficiente
- que el usuario exista
- que el pedido respete reglas de negocio por pais, canal o promocion

Esas decisiones suelen pertenecer a la capa de aplicacion o a validaciones mas avanzadas.

## 3.7. Validacion como proteccion de calidad de datos

La validacion no vuelve perfecto al sistema. Pero si sube mucho el piso de calidad.

### Beneficios practicos

- reduce documentos imposibles de interpretar
- evita errores silenciosos en reportes y APIs
- acota la cantidad de formatos que el backend debe soportar
- ayuda a mantener compatibilidad entre servicios

### Buena practica

Usar validacion para proteger estructura y sanidad minima, no para meter cada regla de negocio posible.

## 3.8. Limite importante: validacion no es toda la logica de negocio

Este punto es central.

Confundir validacion con negocio lleva a dos extremos:

1. no validar nada en la base
2. intentar meter toda la logica del dominio dentro del esquema de coleccion

Ninguno de los dos extremos suele ser sano.

### Ejemplos de reglas que suelen quedar en aplicacion

- un usuario solo puede cancelar una orden dentro de cierta ventana temporal
- un descuento depende del tipo de cliente y de la campaña activa
- un pago solo puede marcarse como aprobado si existe respuesta positiva del proveedor externo

### Resumen practico

- estructura minima y tipos: muy buen lugar para validacion de coleccion
- decisiones contextuales y de negocio: normalmente mejor en la aplicacion

## 4. Transacciones

## 4.1. Que es una transaccion

Una transaccion es una unidad de trabajo donde varias operaciones se confirman juntas o se descartan juntas.

La idea general es simple:

- si todo sale bien, los cambios quedan aplicados
- si algo falla, los cambios no deben quedar a mitad de camino

En MongoDB esto resulta especialmente relevante cuando una operacion necesita modificar varios documentos o varias colecciones y el resultado parcial seria incorrecto para el negocio.

## 4.2. Cuando resultan utiles en MongoDB

Conviene pensar primero en el problema concreto.

Una transaccion es razonable cuando:

- varias escrituras deben verse como una sola unidad logica
- un fallo intermedio deja el sistema en un estado inconsistente
- no puedes resolver el caso con un modelo mejor o con una unica escritura atomica

### Escenarios tipicos

- registrar un pago y actualizar la orden asociada
- mover saldo entre dos cuentas si ambas operaciones deben quedar coordinadas
- crear una orden y descontar stock en otra coleccion cuando el sistema exige coordinacion fuerte

## 4.3. Cuando son innecesarias

Muchos equipos empiezan a usar transacciones por reflejo porque vienen del mundo relacional. En MongoDB eso suele ser una senal para revisar primero el modelo.

Suelen ser innecesarias cuando:

- todo el cambio entra en un mismo documento
- el modelo ya agrupa los datos que cambian juntos
- una inconsistencia temporal pequena es aceptable
- el caso puede resolverse con operaciones idempotentes y recuperacion posterior

### Ejemplo importante

Si una orden guarda sus `items`, `total` y `status` dentro del mismo documento, muchas actualizaciones de esa orden ya son atomicas por documento.

En ese caso abrir una transaccion no agrega valor real.

## 4.4. Recordatorio clave: un buen modelo reduce la necesidad de transacciones

MongoDB garantiza atomicidad a nivel de documento individual.

Eso significa que, si puedes diseñar el dato para que lo que cambia junto viva junto, muchas operaciones complejas desaparecen como problema transaccional.

### Idea de backend

Antes de preguntar "como meto esto en una transaccion", conviene preguntar:

"por que necesito tocar tantos documentos para un solo caso de uso"

Esa pregunta muchas veces lleva a un mejor diseño.

## 4.5. Sesiones

### Concepto

Las transacciones en MongoDB se ejecutan dentro de una sesion.

La sesion funciona como el contexto que agrupa las operaciones relacionadas.

### Idea introductoria

No hace falta dominar detalles internos para entender lo esencial:

- la sesion abre un contexto de trabajo
- dentro de ese contexto se ejecutan operaciones
- si la transaccion se confirma, los cambios se aplican
- si se aborta, los cambios de esa transaccion no deben quedar persistidos

### Sintaxis conceptual en `mongosh`

```js
const session = db.getMongo().startSession()

session.startTransaction()

try {
  const orders = session.getDatabase("shop").orders
  const payments = session.getDatabase("shop").payments

  payments.insertOne(
    {
      _id: "pay-9001",
      orderId: "ord-9001",
      amount: 125000,
      status: "APPROVED"
    }
  )

  orders.updateOne(
    { _id: "ord-9001" },
    { $set: { status: "PAID" } }
  )

  session.commitTransaction()
} catch (error) {
  session.abortTransaction()
  throw error
} finally {
  session.endSession()
}
```

### Intencion del ejemplo

No busca enseñar infraestructura. Busca mostrar la idea de que varias operaciones quedan coordinadas bajo un mismo contexto.

## 4.6. Limitaciones y costos a nivel introductorio

Una transaccion no es gratis.

Agregar transacciones introduce costo conceptual y operativo:

- mas complejidad en el codigo
- mayor necesidad de manejo de errores
- mas tiempo de retencion de recursos mientras la transaccion sigue abierta
- posible impacto en rendimiento comparado con escrituras simples

### Implicacion practica

Si el mismo caso puede resolverse con:

- una sola actualizacion atomica de documento
- mejor modelado
- o una estrategia eventual aceptable

entonces suele ser mejor evitar la transaccion.

### Regla sana para este nivel

Usar transacciones cuando el beneficio de consistencia fuerte supera claramente el costo y la complejidad.

## 4.7. Escenarios introductorios donde si tiene sentido

## 4.7.1. Registrar pago y cerrar orden

Si el negocio no tolera un pago aprobado sin orden marcada como pagada, ni una orden marcada como pagada sin pago registrado, una transaccion puede ser una buena herramienta.

## 4.7.2. Transferencia entre dos cuentas o wallets

Si el sistema debita una cuenta y acredita otra en colecciones o documentos distintos, el resultado parcial suele ser inaceptable.

## 4.8. Escenarios donde suele ser overkill

## 4.8.1. Actualizar estado e items de una sola orden

Si todo vive dentro del documento `orders`, la operacion ya puede ser atomica sin transaccion multi-documento.

## 4.8.2. Escribir logs o auditoria secundaria

Si un log puede reintentarse luego o tolera consistencia eventual, meterlo dentro de una transaccion de negocio puede aumentar costo sin necesidad.

## 4.8.3. Sincronizar campos descriptivos duplicados

Si estas propagando cambios de `categoryName` o `productLabel` en varios documentos, una transaccion global rara vez es la mejor respuesta. Muchas veces conviene aceptar sincronizacion eventual o rediseñar el dato.

## 5. Seguridad basica

## 5.1. Que problema resuelve

Una base de datos no solo debe guardar datos correctos. Tambien debe limitar quien puede acceder y que operaciones puede ejecutar.

En un backend real, los errores de seguridad mas comunes no suelen venir de tecnicas exoticas. Vienen de decisiones simples mal tomadas:

- usar el mismo usuario administrador para todo
- compartir credenciales entre ambientes
- dar permisos de borrado a servicios que solo leen
- permitir acceso manual a produccion sin controles claros

## 5.2. Autenticacion y autorizacion

### Autenticacion

Responde a esta pregunta:

"quien eres"

En este contexto significa que MongoDB verifica la identidad del usuario o servicio que intenta conectarse.

### Autorizacion

Responde a esta pregunta:

"que puedes hacer"

Una vez autenticado, el sistema define si ese usuario puede:

- leer
- insertar
- actualizar
- borrar
- administrar estructura

### Idea clave

Autenticarse no implica tener permiso para todo.

## 5.3. Principio de menor privilegio

### Concepto

Cada aplicacion o servicio deberia tener solo los permisos estrictamente necesarios para cumplir su funcion.

### Ejemplos practicos

- un servicio de reporting puede necesitar solo lectura
- una API publica puede necesitar leer y escribir en ciertas colecciones, pero no borrar colecciones completas
- una tarea administrativa puede requerir mas permisos, pero no deberia compartir credenciales con el backend principal

### Beneficio

Si una credencial se filtra o si una parte del sistema falla, el daño potencial queda mas acotado.

## 5.4. Por que no usar usuarios sobre-privilegiados en aplicaciones

Es un error muy comun conectar la aplicacion con un usuario que puede hacer de todo porque "asi evitamos problemas de permisos".

Eso simplifica hoy, pero empeora mucho el riesgo.

### Riesgos concretos

- un bug puede ejecutar un borrado masivo accidental
- una vulnerabilidad de la aplicacion gana acceso innecesario
- cualquier script desplegado con esa credencial puede tocar colecciones ajenas
- se vuelve mas dificil auditar que servicio deberia poder hacer cada cosa

### Regla simple

Si la aplicacion solo necesita operar sobre `orders` y `payments`, no deberia conectarse con un usuario capaz de administrar toda la instancia.

## 5.5. Proteger datos y accesos en produccion

Sin entrar en internals avanzados, un backend developer deberia asumir estas practicas basicas:

- credenciales separadas por ambiente
- permisos acotados por servicio
- evitar compartir usuarios entre aplicaciones distintas
- no dejar credenciales embebidas en codigo fuente
- tratar operaciones destructivas con filtros claros y revisiones previas

### Advertencia importante

Una base de datos de produccion no es un espacio para probar comandos a ciegas.

Operaciones como estas requieren especial cuidado:

- `deleteMany({})`
- `updateMany({})` sin filtro real
- cambios estructurales ejecutados desde el usuario de la aplicacion

## 6. Preocupaciones practicas de backend

## 6.1. Validar datos de ordenes

En un backend de ecommerce o pagos, las ordenes suelen ser el punto donde mas conviene proteger estructura minima:

- `userId`
- `status`
- `items`
- `total`
- `createdAt`

### Por que

Una orden mal formada impacta:

- checkout
- pagos
- reportes
- soporte
- conciliaciones

La validacion de coleccion ayuda a evitar que un servicio secundario o un script manual deje datos imposibles de procesar.

## 6.2. Escenarios de actualizacion multi-documento

No toda escritura en varias colecciones necesita transaccion. Conviene separar casos.

### Caso A: coordinacion fuerte necesaria

- insertar `payment`
- actualizar `orders.status` a `PAID`

Si una parte sin la otra rompe el negocio, la transaccion puede tener sentido.

### Caso B: coordinacion debil o eventual

- actualizar `orders.status`
- escribir un documento en `audit_logs`

Si el log puede reintentarse luego, no siempre conviene meter ambos pasos en la misma transaccion.

## 6.3. Acceso seguro desde servicios backend

Una aplicacion Spring Boot suele hablar con MongoDB a traves de una URI o configuracion centralizada.

Desde el punto de vista conceptual, el criterio sano es:

- cada servicio con su propio usuario cuando sea razonable
- permisos acotados al conjunto de colecciones que realmente usa
- no reutilizar una credencial administrativa como solucion facil

## 6.4. Evitar operaciones destructivas accidentales

Aunque la autenticacion sea correcta, un sistema sigue siendo riesgoso si cualquier flujo puede lanzar operaciones destructivas sin barreras.

### Buenas practicas simples

- exigir filtros explicitos en borrados masivos
- separar usuarios de lectura, escritura y administracion
- evitar que el usuario de la aplicacion pueda dropear colecciones
- revisar muy bien scripts de mantenimiento antes de ejecutarlos en produccion

## 7. Errores comunes

## 7.1. Confiar solo en la validacion de aplicacion

### Problema

La base queda expuesta a entradas invalidas desde otros canales.

### Resultado

La calidad del dato depende de que todos los consumidores sean perfectos, algo poco realista.

## 7.2. Usar transacciones para todo

### Problema

Se agrega complejidad y costo donde una operacion atomica por documento ya alcanzaba.

### Senal de alerta

Si casi todo requiere transacciones, probablemente convenga revisar modelado y limites de cada flujo.

## 7.3. Dar permisos excesivos

### Problema

La aplicacion puede leer, borrar o administrar mucho mas de lo necesario.

### Riesgo

Un bug funcional se convierte en incidente serio de datos.

## 7.4. Confundir validacion con reglas de negocio completas

### Problema

Se intenta meter todo el dominio dentro del esquema de validacion.

### Resultado

El sistema se vuelve mas dificil de mantener y aun asi no resuelve reglas contextuales complejas.

## 8. Resumen operativo

Para un backend developer que trabaja con MongoDB, este modulo deja estas ideas como referencia:

- valida en la aplicacion y tambien protege la coleccion con reglas minimas
- usa validacion para estructura, tipos y restricciones basicas
- recuerda que la atomicidad por documento ya resuelve muchos casos
- usa transacciones cuando de verdad coordinan una unidad de trabajo multi-documento
- no conectes aplicaciones con usuarios mas poderosos de lo necesario
- protege produccion reduciendo permisos y tratando con cuidado las operaciones destructivas

## 9. Puente hacia Spring Boot

Este modulo prepara el terreno para los siguientes pasos del curso.

Cuando entremos en integracion con Spring Boot, estas ideas pasan a ser decisiones concretas de implementacion:

- que valida el DTO o la capa de servicio
- que protege MongoDB a nivel de coleccion
- cuando una operacion necesita soporte transaccional real
- con que usuario y con que permisos se conecta cada servicio

La integracion tecnica cambia segun la herramienta, pero el criterio de decision deberia mantenerse.
