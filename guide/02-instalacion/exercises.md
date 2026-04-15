# Ejercicios

## 1. Verificar herramientas del entorno

### Consigna

Escribe que comando usarias para comprobar:

- si `mongosh` esta instalado
- si puedes conectarte a una instancia local

### Objetivo

Distinguir entre tener instalada la shell y tener disponible el servidor.

## 2. Separar roles de las herramientas

### Consigna

Explica con tus palabras que rol cumple cada una:

- servidor de MongoDB
- `mongosh`
- MongoDB Compass

### Pista

Piensa cual guarda datos, cual ejecuta comandos y cual ayuda a inspeccionar.

## 3. Crear una base de trabajo

### Consigna

Escribe la secuencia de comandos en `mongosh` para:

1. cambiar a una base llamada `courses_dev`
2. mostrar la base actual
3. crear una coleccion llamada `students`

## 4. Insertar un documento simple

### Consigna

Sobre la coleccion `students`, inserta un documento con estos campos:

- `name`
- `email`
- `track`

Despues escribe el comando para listar el contenido.

### Restriccion

Usa solo comandos simples de `mongosh`.

## 5. Preparar un entorno parecido a un backend real

### Consigna

Imagina una API de cursos. Crea una base `academy_dev` y dos colecciones:

- `users`
- `courses`

Escribe los comandos para crearlas y agrega un documento simple en cada una.

## 6. Detectar un error comun

### Consigna

Un compañero dice:

"MongoDB no funciona, porque Spring Boot no conecta."

Explica que verificaciones harías primero por fuera de la aplicacion.

### Objetivo

Reforzar el criterio de diagnostico por capas.

## 7. Uso opcional de Compass

### Consigna

Describe una situacion concreta en la que Compass te ayudaria y otra en la que `mongosh` seria mejor opcion.

### Pista

Piensa en inspeccion visual contra diagnostico rapido.
