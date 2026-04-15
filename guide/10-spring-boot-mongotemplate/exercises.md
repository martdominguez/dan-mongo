# Ejercicios

## 1. Escribir una consulta dinamica de productos

### Consigna

Una API administrativa de catalogo debe buscar productos segun estos parametros opcionales:

- `category`
- `brand`
- `active`
- `minPrice`
- `maxPrice`

Diseña un metodo con `MongoTemplate` que construya la consulta de forma dinamica con `Query` y `Criteria`.

La solucion debe dejar claro:

- que filtros se agregan solo si vienen informados
- como evitarias duplicar metodos para cada combinacion posible

## 2. Agregar sorting y paginacion

### Consigna

Extiende el ejercicio anterior para que el endpoint tambien acepte:

- `page`
- `size`
- orden por `createdAt desc`

Explica:

- donde aplicarias el ordenamiento
- donde aplicarias `skip` y `limit`
- por que conviene definir un orden estable cuando paginas resultados

## 3. Implementar un filtro dinamico de tickets

### Consigna

Tienes una coleccion `support_tickets` con campos:

- `status`
- `priority`
- `assignedAgent`
- `createdAt`

Escribe la estructura de una clase `TicketSearchCriteria` y un repositorio custom que permita filtrar tickets por cualquier combinacion de esos campos.

No hace falta implementar toda la API REST, pero si la capa de persistencia y el criterio de diseno.

## 4. Implementar una actualizacion parcial

### Consigna

Un backend debe marcar un ticket como `CLOSED` y guardar la fecha `closedAt` sin reemplazar el documento completo.

Implementa un metodo con `MongoTemplate` que use:

- `Query`
- `Update`
- `updateFirst`

Indica ademas:

- por que esta operacion es una actualizacion parcial
- que ventaja tiene frente a reescribir todo el documento

## 5. Actualizar varios documentos en lote

### Consigna

Un job nocturno debe desactivar todos los productos cuya fecha `expiresAt` sea menor a la fecha actual.

Diseña una operacion con `updateMulti` que:

- filtre correctamente los documentos
- marque `active = false`
- actualice `updatedAt`

Explica que riesgo tendria ejecutar esta operacion con un filtro demasiado amplio.

## 6. Construir una agregacion programatica simple

### Consigna

Construye una agregacion con `Aggregation` para obtener cuantas ordenes pagadas hay por `channel`.

La solucion debe incluir:

- `match`
- `group`
- `sort`

Explica tambien como este ejercicio se conecta con lo visto en el modulo 06.

## 7. Elegir entre `MongoRepository` y `MongoTemplate`

### Consigna

Evalua estos casos y decide cual herramienta seria la principal en cada uno. Justifica.

1. crear, buscar por id y borrar cupones promocionales
2. buscar usuarios con 7 filtros opcionales combinables
3. obtener ventas agrupadas por categoria y mes
4. actualizar solo el ultimo login de un usuario
5. listar tickets por `status` con una consulta estable y muy simple

## 8. Escenario real de backend: busqueda de ordenes en backoffice

### Consigna

Un backoffice expone `GET /admin/orders` y acepta estos parametros:

- `status`
- `customerId`
- `from`
- `to`
- `minTotal`
- `channel`
- `page`
- `size`

Disena:

- el DTO de entrada para filtros
- la firma del servicio
- la clase de persistencia que encapsularia la query

Explica por que esta logica no deberia quedar en el controlador.

## 9. Escenario real de backend: reasignacion de tickets

### Consigna

Un supervisor necesita reasignar todos los tickets `PENDING` de un agente a otro desde un endpoint interno.

Disena:

- el metodo de servicio
- la operacion de persistencia con `updateMulti`
- una forma simple de reportar cuantos tickets fueron modificados

Aclara ademas:

- que validarias antes de ejecutar la operacion
- por que este caso pide mas cuidado que una actualizacion por id

## 10. Diseñar un custom repository

### Consigna

Propon una estructura para un modulo de pedidos que combine:

- `MongoRepository` para CRUD simple
- una interfaz custom para consultas dinamicas
- una implementacion basada en `MongoTemplate`
- un servicio que coordine ambas piezas

No hace falta escribir todas las clases completas, pero si mostrar la relacion entre ellas y que responsabilidad tendria cada una.

## 11. Relacionar consultas con indices

### Consigna

Tienes una busqueda frecuente que filtra por:

- `status`
- `createdAt`

y siempre ordena por `createdAt desc`.

Explica:

- que indice evaluarias crear
- por que la consulta de `MongoTemplate` deberia pensarse junto con el indice
- que problema podria aparecer si la query crece sin revisar rendimiento

## 12. Pensar testing de una capa con `MongoTemplate`

### Consigna

Describe una estrategia breve de testing para una clase que:

- arma filtros dinamicos
- hace un `updateFirst`
- ejecuta una agregacion programatica

Tu respuesta debe mencionar:

- que casos de integracion probarias
- por que no alcanza con verificar solo que el metodo fue invocado
- como se relaciona este criterio con la confiabilidad del backend
