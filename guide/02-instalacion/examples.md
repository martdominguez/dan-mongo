# Ejemplos

## 1. Verificar que `mongosh` esta disponible

### Que problema resuelve

Antes de probar cualquier comando de MongoDB, necesitas confirmar que la shell existe en tu entorno.

### Comando

```bash
mongosh --version
```

### Resultado esperado

Deberias ver la version instalada. Si el comando no existe, todavia no tienes lista la herramienta principal para validar la instancia.

### Por que importa en backend

Si no puedes abrir `mongosh`, vas a depender de la aplicacion para diagnosticar todo. Eso hace mas lenta cualquier depuracion.

## 2. Conectarte a una instancia local

### Que problema resuelve

Necesitas saber si el servidor de MongoDB esta realmente levantado y aceptando conexiones.

### Comando

```bash
mongosh "mongodb://localhost:27017"
```

### Resultado esperado

Si la conexion funciona, entras a la shell interactiva. Desde ahi puedes ejecutar comandos contra la instancia local.

### Por que importa en backend

Si esta conexion falla, todavia no tiene sentido revisar `application.yml` o repositorios de Spring Data.

## 3. Crear una base de trabajo para desarrollo

### Que problema resuelve

Conviene separar una base local de pruebas simples para no mezclar datos ni experimentar sobre algo ambiguo.

### Comandos en `mongosh`

```js
use app_dev
db
```

### Resultado esperado

El comando `use app_dev` cambia el contexto actual. El comando `db` devuelve el nombre de la base activa.

### Idea clave

En MongoDB, cambiar a una base es inmediato. La base queda persistida al guardar datos.

## 4. Crear una coleccion inicial

### Que problema resuelve

Necesitas una estructura minima para empezar a trabajar con documentos reales.

### Comando en `mongosh`

```js
db.createCollection("users")
```

### Verificacion

```js
show collections
```

### Resultado esperado

Deberias ver la coleccion `users`.

### Por que importa en backend

Esto te permite verificar nombres y convenciones antes de empezar a escribir codigo de acceso a datos.

## 5. Insertar un documento minimo para validar el entorno

### Que problema resuelve

No alcanza con crear la coleccion. Necesitas comprobar que puedes escribir un dato real.

### Comando en `mongosh`

```js
db.users.insertOne({
  name: "Ana Perez",
  email: "ana@example.com",
  role: "student"
})
```

### Resultado esperado

MongoDB devuelve un resultado con `acknowledged: true` y un `_id` generado.

### Advertencia

Todavia no estamos estudiando CRUD en detalle. Este paso solo valida escritura basica del entorno.

## 6. Leer el primer documento

### Que problema resuelve

Necesitas comprobar que el dato quedo guardado y que puedes inspeccionarlo.

### Comando en `mongosh`

```js
db.users.find()
```

### Variante mas comoda

```js
db.users.find().pretty()
```

### Resultado esperado

Deberias ver el documento insertado con su `_id`.

### Por que importa en backend

Esta es la forma mas rapida de validar si tu dato de prueba existe antes de culpar a la capa de aplicacion.

## 7. Crear otra coleccion con sentido de negocio

### Que problema resuelve

Queremos una estructura un poco mas cercana a un backend real, sin entrar todavia en consultas complejas.

### Comandos en `mongosh`

```js
db.createCollection("courses")

db.courses.insertOne({
  title: "MongoDB desde cero",
  level: "introductorio",
  published: true
})
```

### Verificacion

```js
show collections
db.courses.find().pretty()
```

### Aprendizaje

Ya tienes una base con dos colecciones simples y datos de ejemplo. Eso es suficiente para practicar navegacion basica.

## 8. Ver las bases disponibles

### Que problema resuelve

Necesitas confirmar que tu base aparece en la instancia.

### Comando en `mongosh`

```js
show dbs
```

### Advertencia

Una base suele aparecer en `show dbs` cuando ya tiene datos persistidos. Solo cambiar con `use` no siempre basta.

### Por que importa en backend

Evita confusiones comunes entre "cambie de base" y "la base realmente existe con datos".

## 9. Flujo completo minimo y ejecutable

### Escenario

Quieres validar un entorno nuevo en menos de dos minutos.

### Secuencia

```bash
mongosh "mongodb://localhost:27017"
```

```js
use app_dev
db.createCollection("users")
db.users.insertOne({
  name: "Martin Gomez",
  email: "martin@example.com",
  role: "developer"
})
db.users.find().pretty()
show collections
show dbs
```

### Resultado esperado

Si todos esos comandos funcionan, tu entorno ya esta listo para seguir con modulos posteriores.

## 10. Uso opcional de Compass

### Que problema resuelve

A veces quieres validar visualmente lo mismo que ya probaste en shell.

### Paso practico

Abre Compass y usa la misma URI:

```text
mongodb://localhost:27017
```

Despues navega a:

- base `app_dev`
- coleccion `users`

### Que deberias ver

El documento insertado desde `mongosh`.

### Idea clave

Compass confirma visualmente el estado de la base, pero la interaccion principal del curso sigue siendo con comandos.
