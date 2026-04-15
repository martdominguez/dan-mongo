# Ejercicios

## 1. Identificar el agregado principal

### Consigna

Tienes una API de pedidos. Cada pedido devuelve:

- datos basicos del cliente
- items comprados
- direccion de entrega
- estado actual

Explica en 3 o 4 lineas por que este caso puede encajar bien en MongoDB.

### Pista

Piensa que partes del dato se leen juntas en casi todas las respuestas.

## 2. Diferenciar documento y coleccion

### Consigna

Para un backend de soporte tecnico, nombra:

- una base de datos posible
- dos colecciones
- un ejemplo de documento para una de esas colecciones

Usa nombres realistas como `tickets`, `users` o `comments`.

## 3. Detectar variabilidad de estructura

### Consigna

Imagina un catalogo con notebooks, monitores y sillas. Escribe dos documentos JSON de productos que muestren por que un modelo documental puede ser util cuando los atributos cambian segun la categoria.

### Pista

Puedes usar un campo `attributes`.

## 4. Comparar con modelo relacional

### Consigna

Toma el caso de un usuario con direcciones y preferencias.

Responde:

1. Como se podria pensar ese caso en una base de datos relacional.
2. Que ventaja practica podria tener un documento en MongoDB para un endpoint de lectura.

## 5. BSON y tipos de datos

### Consigna

Escribe un documento JSON de un pedido que incluya:

- un identificador
- un total numerico
- una fecha de creacion
- una lista de items

Despues explica por que guardar la fecha como tipo fecha es mejor que guardarla como texto.

## 6. Decidir cuando no usar MongoDB

### Consigna

Lee este escenario:

"Una empresa quiere desarrollar un sistema contable con muchas reglas de integridad, relaciones estrictas entre entidades y operaciones transaccionales complejas."

Explica por que MongoDB podria no ser la primera opcion en este caso.

## 7. Pensar como backend developer

### Consigna

Elige uno de estos dominios:

- ecommerce
- plataforma de cursos
- sistema de tickets

Describe:

1. una coleccion principal
2. que campos incluirias en un documento
3. que endpoint de API leeria ese documento casi completo

### Objetivo

Empezar a conectar modelo de datos con necesidades reales del backend.
