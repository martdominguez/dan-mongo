# Teoria

## 1. Que problema resuelve este modulo

Antes de escribir una linea de integracion con Spring Boot, necesitas un entorno que puedas levantar, inspeccionar y probar rapido.

Si esa base falla, no sabes si el problema esta en:

- MongoDB
- tu configuracion local
- el driver
- la aplicacion backend

Por eso este modulo importa. Un desarrollador backend necesita poder validar el entorno por fuera de la aplicacion.

## 2. Las piezas basicas del entorno

En un entorno local simple aparecen tres piezas:

- el servidor de MongoDB
- `mongosh`
- una herramienta visual opcional como MongoDB Compass

### Servidor de MongoDB

Es el proceso que guarda los datos y escucha conexiones. Sin esto no hay base de datos real funcionando.

Para un backend developer importa porque tu aplicacion no habla con "MongoDB" como idea abstracta. Habla con una instancia concreta, normalmente accesible en `mongodb://localhost:27017`.

### `mongosh`

Es la shell oficial para conectarte a MongoDB y ejecutar comandos.

Para un backend developer importa porque permite:

- comprobar si la instancia responde
- crear una base o una coleccion rapido
- probar consultas sin tocar codigo Java
- aislar errores del entorno antes de culpar al backend

Si tu aplicacion no conecta, `mongosh` suele ser la forma mas rapida de verificar si el problema es de red, autenticacion o configuracion.

### MongoDB Compass

Es una interfaz grafica para explorar bases, colecciones y documentos.

Es opcional, pero puede ayudar cuando necesitas:

- inspeccionar documentos con comodidad
- revisar visualmente que una coleccion existe
- validar datos sin depender solo de la terminal

Para un backend developer no reemplaza a `mongosh`. Sirve como apoyo visual, no como herramienta principal de diagnostico.

## 3. Instalacion de MongoDB

La forma exacta depende del sistema operativo, pero la idea general es siempre la misma:

1. instalar MongoDB Community Edition
2. iniciar el servicio o proceso del servidor
3. verificar que escucha en el puerto esperado
4. conectarte con `mongosh`

### Opcion recomendada para aprender

Trabajar primero con una instancia local.

Eso reduce variables al comenzar:

- no dependes de red externa
- no dependes de permisos de un servicio cloud
- puedes romper y recrear el entorno con menos costo

### Que conviene verificar despues de instalar

- que el servicio de MongoDB esta iniciado
- que el puerto usual `27017` esta disponible
- que `mongosh` puede abrir una conexion

Si instalaste MongoDB pero no puedes conectarte, el problema practico mas comun no es el driver del backend. Suele ser que el servidor no esta corriendo.

## 4. Instalacion de `mongosh`

En algunos entornos viene por separado del servidor. En otros ya queda disponible al instalar MongoDB.

La verificacion minima es simple:

```bash
mongosh --version
```

Si el comando existe, ya tienes la shell instalada.

Para un desarrollador backend esto importa porque `mongosh` es la herramienta mas directa para responder preguntas como:

- la base esta levantada
- la URI funciona
- el usuario tiene acceso
- la coleccion realmente existe

## 5. Compass: cuando usarlo y cuando no

Compass puede ser util al principio porque baja la friccion visual. Ves colecciones, bases y documentos sin tener que recordar tantos comandos.

Pero conviene usarlo con criterio:

- para explorar, muy bien
- para validar rapido un documento, muy bien
- para reemplazar el entendimiento de los comandos, no

En backend, cuando un sistema falla en un entorno remoto o en CI, casi siempre necesitas pensamiento basado en comandos y logs. Por eso `mongosh` sigue siendo mas importante.

## 6. Estructura basica del entorno

Cuando trabajas localmente, conviene pensar el entorno en capas:

### Capa 1: servidor

MongoDB corriendo localmente.

Ejemplo de URI comun:

```text
mongodb://localhost:27017
```

### Capa 2: cliente manual

`mongosh` o Compass.

Estas herramientas te permiten inspeccionar y probar la instancia sin pasar por la aplicacion.

### Capa 3: aplicacion backend

Tu servicio Spring Boot, que despues usara una URI y una base concreta.

La buena practica es validar primero la capa 1 y 2. Recién despues tiene sentido depurar la capa 3.

## 7. Primera conexion con `mongosh`

Si el servidor esta corriendo localmente, una conexion tipica se ve asi:

```bash
mongosh "mongodb://localhost:27017"
```

Una vez dentro, ya puedes ejecutar comandos.

Esto es importante porque marca un punto de control muy concreto:

- si `mongosh` entra, el servidor al menos responde
- si `mongosh` no entra, todavia no conviene depurar Spring Boot

## 8. Primeras interacciones con la base

Las primeras interacciones no deberian ser complejas. Al principio alcanza con:

- listar bases
- cambiar de base
- crear una coleccion
- insertar un documento simple
- leer ese documento

Esto no es "hacer CRUD en profundidad". Es validar que el entorno funciona y entender el flujo minimo.

### Cambiar o crear una base

En `mongosh`:

```js
use app_dev
```

Si la base no existia, MongoDB la materializa cuando guardas el primer dato.

### Ver en que base estas

```js
db
```

### Crear una coleccion

```js
db.createCollection("users")
```

### Ver colecciones

```js
show collections
```

## 9. Por que esto importa para un backend developer

En un proyecto real, estas acciones simples resuelven problemas muy concretos:

- confirmar que la base usada por desarrollo existe
- probar nombres de colecciones antes de escribir repositorios
- verificar que una URI local esta bien formada
- inspeccionar datos de prueba sin cambiar codigo

Dicho simple: dominar este arranque te ahorra tiempo cada vez que algo falla en integracion o configuracion.

## 10. Error comun: depender solo de la aplicacion

Un error muy frecuente es empezar por la capa mas compleja:

1. levantar Spring Boot
2. ver un error de conexion
3. tocar configuracion al azar

La forma mas segura es:

1. comprobar que MongoDB corre
2. entrar con `mongosh`
3. validar base, colecciones y datos minimos
4. recien despues conectar la aplicacion

Eso reduce mucho el tiempo de diagnostico.

## 11. Buena practica para este curso

Usa una base local de trabajo, por ejemplo `app_dev` o `courses_dev`, y crea datos pequeños y descartables.

No necesitas un entorno sofisticado para aprender las bases. Necesitas uno que responda, sea facil de inspeccionar y te deje practicar comandos reales.

## 12. Cierre

Instalar MongoDB no es solo cumplir un paso tecnico. Es preparar una base confiable para el resto del curso.

Si al final de este modulo puedes conectarte con `mongosh`, crear una base, crear una coleccion y comprobar un documento simple, ya tienes el entorno minimo necesario para avanzar con seguridad.
