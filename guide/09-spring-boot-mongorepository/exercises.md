# Ejercicios

## 1. Crear una clase documento para usuarios

### Consigna

Define una clase `UserAccount` para una coleccion `user_accounts` usando Spring Data MongoDB.

Debe incluir al menos:

- `id`
- `email`
- `fullName`
- `status`
- `createdAt`

Indica que anotaciones usarias y por que.

## 2. Mapear un nombre de campo distinto

### Consigna

En la clase `UserAccount`, el backend usa la propiedad `phoneNumber`, pero en MongoDB el campo ya existe como `phone_number`.

Escribe la parte del codigo necesaria para mapear correctamente ese campo y explica en que escenario real esta decision puede ser util.

## 3. Definir un repositorio basico

### Consigna

Crea una interfaz `UserAccountRepository` que extienda `MongoRepository`.

La interfaz debe incluir:

- el tipo de entidad correcto
- el tipo de id correcto
- un metodo derivado para buscar usuarios por `status`

## 4. Implementar CRUD desde un servicio

### Consigna

Diseña un servicio `UserAccountService` que implemente estas operaciones:

- crear usuario
- buscar usuario por id
- listar usuarios
- borrar usuario por id

No hace falta escribir una aplicacion completa, pero si la estructura del servicio y que metodo del repositorio usarias en cada caso.

## 5. Agregar una consulta derivada introductoria

### Consigna

Tienes una coleccion `products` con estos campos:

- `name`
- `category`
- `active`

Define al menos tres metodos derivados utiles para un backend de catalogo.

Al menos uno debe filtrar por booleano y otro debe combinar dos campos.

## 6. Escenario REST: controlador de tickets

### Consigna

Diseña los endpoints principales para una API de tickets de soporte que use `MongoRepository`.

Incluye al menos:

- crear ticket
- obtener ticket por id
- listar tickets
- borrar ticket por id

Para cada endpoint, indica:

- verbo HTTP
- ruta
- que metodo del servicio deberia invocar

## 7. Escenario REST: filtro por estado

### Consigna

Un endpoint `GET /api/orders?status=PAID` debe devolver pedidos filtrados por estado.

Explica:

- que metodo derivado pondrias en el repositorio
- que responsabilidad tendria el servicio
- por que el controlador no deberia llamar al repositorio directamente

## 8. Decidir si `MongoRepository` es suficiente

### Consigna

Evalua estos casos e indica si `MongoRepository` es suficiente como herramienta principal o si el problema sugiere otra alternativa. Justifica.

1. guardar y listar productos
2. buscar tickets por `status`
3. buscar productos con 6 filtros opcionales combinables
4. construir un reporte agrupado por categoria y mes
5. borrar un documento por id

## 9. Separar dominio, DTO y persistencia

### Consigna

Una API recibe este cuerpo para crear un ticket:

```json
{
  "customerEmail": "ana@example.com",
  "subject": "No puedo ingresar",
  "priority": "HIGH"
}
```

Explica como separarias:

- request DTO
- entidad persistida
- respuesta HTTP

No escribas una solucion completa, pero si describe que datos vivirian en cada capa y por que.

## 10. Detectar un mal uso del repositorio

### Consigna

Un equipo empieza a agregar metodos como estos:

- `findByStatusAndPriorityAndAssignedAgentAndCreatedAtAfter`
- `findByStatusAndPriorityAndAssignedAgentAndCreatedAtAfterAndCustomerEmail`
- `findByStatusAndPriorityAndAssignedAgentAndCreatedAtAfterAndCustomerEmailAndSubjectContaining`

Explica por que esto puede ser una señal de que `MongoRepository` esta dejando de ser la herramienta mas adecuada para ese caso.

## 11. Configuracion del proyecto

### Consigna

Escribe una configuracion minima en `application.yml` o `application.properties` para conectar una aplicacion Spring Boot a una base local llamada `catalog_db`.

Luego explica:

- que representa cada parte de la URI
- que supuesto de desarrollo local estas haciendo

## 12. Preparar la transicion al siguiente modulo

### Consigna

Resume en tus palabras por que un backend podria empezar con `MongoRepository` y luego necesitar `MongoTemplate`.

Tu respuesta debe mencionar:

- simplicidad inicial
- consultas dinamicas
- agregaciones complejas
- criterio para elegir herramienta
