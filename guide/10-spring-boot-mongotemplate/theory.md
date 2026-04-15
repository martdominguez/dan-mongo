# Teoria

## 1. Que problema resuelve este modulo

En el modulo 09 vimos una forma muy productiva de integrar MongoDB en Spring Boot mediante `MongoRepository`.

Ese enfoque funciona muy bien cuando el backend necesita:

- CRUD simple
- consultas previsibles
- metodos derivados faciles de leer
- poca variacion en filtros y ordenamientos

Pero en proyectos reales aparecen casos donde esa abstraccion ya no alcanza con comodidad.

Ejemplos frecuentes:

- un endpoint acepta varios filtros opcionales
- un backoffice necesita actualizaciones parciales sobre muchos documentos
- un servicio administrativo requiere una consulta con criterios armados en tiempo de ejecucion
- un reporte necesita un pipeline de agregacion programatico
- la capa de persistencia empieza a llenarse de metodos de repositorio cada vez mas largos

`MongoTemplate` existe para resolver ese tipo de situaciones con mas control.

La idea de este modulo es mostrarlo como una herramienta profesional de uso puntual y bien justificado, no como reemplazo obligatorio de `MongoRepository`.

## 2. Que es `MongoTemplate`

## 2.1. Definicion

`MongoTemplate` es una abstraccion de Spring Data MongoDB que permite ejecutar operaciones sobre MongoDB desde codigo Java con mas control explicito que un repositorio derivado.

Con `MongoTemplate` puedes:

- construir consultas con `Query` y `Criteria`
- definir ordenamiento y paginacion
- ejecutar actualizaciones parciales
- lanzar agregaciones programaticas
- controlar mejor operaciones de bajo nivel dentro de la capa de persistencia

## 2.2. Por que existe

Spring Data MongoDB ofrece distintas capas de abstraccion porque no todos los problemas de persistencia tienen la misma forma.

`MongoRepository` resuelve muy bien el acceso a datos cuando:

- la operacion es frecuente y conocida
- el filtro es estable
- la firma del metodo comunica bien la intencion

`MongoTemplate` aparece cuando la necesidad principal ya no es solo "persistir una entidad" sino "construir una operacion MongoDB con mas criterio y flexibilidad".

### Idea clave

No existe porque `MongoRepository` sea malo.

Existe porque algunos problemas de backend necesitan una API mas expresiva que una interfaz con metodos derivados.

## 2.3. Flexibilidad vs abstraccion

### Mas abstraccion

Con `MongoRepository` escribes menos codigo de infraestructura.

Eso mejora:

- velocidad de desarrollo
- legibilidad en casos simples
- curva de aprendizaje inicial

### Mas flexibilidad

Con `MongoTemplate` escribes mas detalle tecnico, pero ganas control sobre:

- como se arma el filtro
- cuando se agregan criterios
- que campos se actualizan
- como se construye un pipeline
- que operacion Mongo resulta mas adecuada para el caso

### Trade-off

La pregunta correcta no es "cual es mejor".

La pregunta correcta es "cual deja mas claro este caso de uso con el menor costo de mantenimiento".

## 2.4. Cuando `MongoTemplate` suele ser mejor opcion

### Escenarios tipicos

- busquedas con varios parametros opcionales
- filtros condicionales por rango de fechas, estado, categoria o texto
- endpoints administrativos con ordenamiento configurable
- actualizaciones parciales que no deben reemplazar el documento completo
- operaciones masivas sobre varios documentos
- agregaciones que se construyen en codigo
- capas custom de persistencia donde conviene encapsular logica Mongo mas expresiva

### Senal practica

Si el repositorio empieza a acumular metodos como estos:

- `findByStatusAndPriorityAndAssignedAgentAndCreatedAtAfter`
- `findByStatusAndPriorityAndCategoryAndRegionAndCreatedAtAfter`

entonces probablemente el problema real ya no sea "definir otro metodo derivado", sino "armar la consulta dinamicamente".

## 2.5. Cuando no conviene usarlo como primera opcion

No hace falta pasar todo a `MongoTemplate`.

Para casos como estos, `MongoRepository` sigue siendo una opcion excelente:

- crear una entidad
- buscar por id
- borrar por id
- listar un conjunto simple y acotado
- consultas derivadas faciles de nombrar y entender

### Buena practica

Empieza simple.

Introduce `MongoTemplate` cuando el problema lo pida con claridad.

## 3. Configuracion minima en Spring Boot

## 3.1. Dependencia principal

La dependencia base sigue siendo la misma que en el modulo anterior:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

Esa dependencia ya registra el soporte necesario para `MongoRepository` y tambien para `MongoTemplate`.

## 3.2. Inyeccion del template

En la practica, Spring Boot expone `MongoTemplate` como bean y permite inyectarlo en componentes de persistencia.

```java
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderTemplateRepository {

    private final MongoTemplate mongoTemplate;

    public OrderTemplateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
```

### Intencion del desarrollador

La intencion no es usar `MongoTemplate` desde cualquier capa.

Conviene ubicarlo en componentes orientados a persistencia para que la logica Mongo no termine mezclada con controladores o reglas de negocio.

## 4. Query building con `Query` y `Criteria`

## 4.1. Que problema resuelve

Cuando un endpoint recibe varios parametros opcionales, un metodo derivado suele quedarse corto o volverse dificil de mantener.

En esos casos conviene construir la consulta paso a paso.

## 4.2. `Query`

### Concepto

`Query` representa la consulta que se enviara a MongoDB.

Sobre ella puedes definir:

- criterios de filtro
- ordenamiento
- paginacion
- limites
- proyecciones

### Sintaxis base

```java
Query query = new Query();
```

Tambien puedes crearla desde un criterio inicial:

```java
Query query = Query.query(Criteria.where("status").is("PAID"));
```

## 4.3. `Criteria`

### Concepto

`Criteria` representa una condicion sobre uno o varios campos.

### Variaciones frecuentes

#### Igualdad

```java
Criteria.where("status").is("PAID")
```

#### Comparacion numerica

```java
Criteria.where("total").gte(1000).lte(5000)
```

#### Inclusion en lista

```java
Criteria.where("category").in(List.of("BOOKS", "TECH"))
```

#### Fecha mayor a un valor

```java
Criteria.where("createdAt").gte(fromDate)
```

#### Texto parcial con regex

```java
Criteria.where("customerName").regex(namePart, "i")
```

### Advertencia

Aunque `regex` puede ser util, no siempre aprovecha indices de la misma forma que una igualdad o un prefijo bien elegido. Conviene relacionar este tipo de consulta con lo visto en el modulo 05.

## 4.4. Agregar criterios a una consulta

La forma mas comun es ir acumulando condiciones solo si el request las trae.

```java
Query query = new Query();

if (status != null) {
    query.addCriteria(Criteria.where("status").is(status));
}

if (minTotal != null) {
    query.addCriteria(Criteria.where("total").gte(minTotal));
}
```

### Intencion

Esto permite construir un endpoint flexible sin inventar un metodo distinto por cada combinacion posible.

## 4.5. Ordenamiento

### Concepto

Ordenar no es un detalle cosmetico. En backend suele afectar:

- experiencia de uso
- consistencia de paginacion
- costo de consulta

### Sintaxis

```java
query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
```

### Variacion con varios campos

```java
query.with(
    Sort.by(Sort.Order.desc("priority"), Sort.Order.asc("createdAt"))
);
```

### Buena practica

Si un endpoint ordena frecuentemente por un campo, revisa si ese patron necesita soporte de indice. Esto conecta directamente con el modulo 05.

## 4.6. Paginacion introductoria

### Concepto

La paginacion evita traer conjuntos demasiado grandes y permite responder APIs de manera mas estable.

En un nivel introductorio, lo importante es entender:

- `skip` indica cuantos resultados omitir
- `limit` indica cuantos traer
- un orden estable ayuda a que las paginas no cambien de forma impredecible

### Sintaxis

```java
query.skip((long) page * size);
query.limit(size);
```

### Idea clave

No alcanza con paginar.

Conviene paginar sobre un criterio de ordenamiento claro, por ejemplo `createdAt desc`, para que el resultado sea mas predecible.

### Advertencia

En volumenes muy grandes pueden aparecer estrategias mejores que `skip` y `limit`, pero eso no es el foco de este modulo.

## 4.7. Filtros dinamicos

### Definicion

Un filtro dinamico es una consulta cuya forma final depende de parametros que pueden o no venir en el request.

### Ejemplo de problema real

Un endpoint administrativo de pedidos acepta:

- `status`
- `customerId`
- `fromDate`
- `toDate`
- `minTotal`
- `category`

Con `MongoTemplate` puedes construir una sola consulta que incorpore solo los filtros presentes.

### Beneficio

Esto reduce:

- explosion de metodos en repositorios
- duplicacion de logica
- codigo condicional en controladores

## 4.8. Proyecciones introductorias

Aunque el foco principal del modulo no esta en proyecciones, vale la pena saber que `Query` tambien permite pedir menos campos.

```java
query.fields()
    .include("id")
    .include("status")
    .include("total");
```

### Cuando aporta valor

- respuestas administrativas livianas
- listados donde no hace falta cargar todo el documento
- consultas que deben evitar traer campos pesados o embebidos

## 5. Operaciones de actualizacion

## 5.1. Por que no siempre conviene reescribir todo el documento

Un backend muchas veces necesita modificar solo una parte del estado.

Ejemplos:

- cambiar el `status` de un pedido
- asignar un agente a varios tickets
- actualizar `lastLoginAt` de un usuario
- marcar productos como inactivos sin tocar el resto del documento

Reemplazar el documento completo en esos casos puede ser innecesario o riesgoso si solo quieres afectar campos puntuales.

## 5.2. `Update`

### Concepto

`Update` representa las modificaciones a aplicar.

### Variaciones frecuentes

#### Setear un campo

```java
Update update = new Update().set("status", "SHIPPED");
```

#### Setear varios campos

```java
Update update = new Update()
    .set("status", "ASSIGNED")
    .set("assignedAgent", "soporte-01");
```

#### Incrementar un contador

```java
Update update = new Update().inc("retryCount", 1);
```

#### Actualizar fecha

```java
Update update = new Update().set("updatedAt", Instant.now());
```

## 5.3. `updateFirst`

### Concepto

Actualiza el primer documento que coincide con la consulta.

### Sintaxis base

```java
mongoTemplate.updateFirst(query, update, Order.class);
```

### Cuando conviene

- operaciones sobre un documento esperado
- cambios puntuales por id o por una condicion que deberia devolver un unico match

### Advertencia

Si la consulta puede devolver varios documentos y tu intencion era afectar a todos, `updateFirst` no alcanza.

## 5.4. `updateMulti`

### Concepto

Actualiza todos los documentos que coinciden con la consulta.

### Sintaxis base

```java
mongoTemplate.updateMulti(query, update, SupportTicket.class);
```

### Cuando conviene

- marcar tickets vencidos
- desactivar productos fuera de catalogo
- reasignar documentos a otro agente o equipo

### Criterio de backend

Antes de usar `updateMulti`, conviene validar que el filtro sea claro y que el impacto sea esperado. En operaciones administrativas, un filtro mal definido puede afectar demasiados documentos.

## 5.5. Actualizaciones parciales

### Definicion

Una actualizacion parcial modifica uno o varios campos concretos sin reemplazar el documento entero.

### Beneficios

- menos trafico de datos
- menos riesgo de pisar campos que no querias tocar
- codigo mas explicito sobre que parte del estado cambia

### Ejemplo de intencion

Si un pedido cambia de `PAID` a `SHIPPED`, la intencion del backend no es "reescribir la orden".

La intencion real es "cambiar el estado y registrar la fecha de actualizacion".

`MongoTemplate` permite expresar esa intencion con claridad.

## 5.6. Resultado de actualizacion

Los metodos de update suelen devolver un `UpdateResult`.

Ese resultado permite revisar datos utiles como:

- cuantos documentos coincidieron
- cuantos se modificaron realmente

### Por que importa

En servicios backend esto ayuda a:

- detectar si el documento no existia
- decidir si devolver error o respuesta vacia
- auditar si una operacion masiva tuvo efecto

## 6. Agregaciones programaticas

## 6.1. Por que son utiles

En el modulo 06 vimos Aggregation Framework desde la perspectiva de MongoDB.

En una aplicacion Spring Boot aparece una necesidad nueva:

traducir ese pipeline a una API Java legible, componible e integrable con servicios.

Las agregaciones programaticas son utiles cuando:

- el backend necesita reportes o resumentes
- el pipeline depende de parametros de entrada
- conviene mantener la construccion del pipeline en codigo tipado
- quieres integrar la salida con DTOs del backend

## 6.2. `Aggregation`

### Concepto

`Aggregation` representa un pipeline programatico construido con stages de Spring Data MongoDB.

### Sintaxis base

```java
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("status").is("PAID")),
    Aggregation.group("customerId").sum("total").as("totalSpent"),
    Aggregation.sort(Sort.Direction.DESC, "totalSpent")
);
```

### Relacion con el modulo 06

La logica conceptual es la misma:

- primero filtras
- luego transformas o agrupas
- despues ordenas o proyectas

Lo que cambia es la forma de expresarlo en Java.

## 6.3. Ejecucion

```java
AggregationResults<CustomerSpendView> results =
    mongoTemplate.aggregate(aggregation, "orders", CustomerSpendView.class);
```

### Componentes

- `aggregation`: el pipeline
- `"orders"`: la coleccion
- `CustomerSpendView.class`: clase de salida para mapear el resultado

## 6.4. Cuando preferir agregaciones programaticas

- cuando el pipeline se arma segun parametros
- cuando quieres centralizar persistencia compleja en una clase custom
- cuando un reporte necesita varias etapas y conviene mantenerlas cerca del codigo de negocio

### No es el foco

Este modulo no busca cubrir toda la API de agregacion de Spring Data ni todas las variantes de stages.

El objetivo es que puedas construir pipelines introductorios con criterio y conectar lo aprendido con el modulo 06.

## 7. Estilo custom repository

## 7.1. Problema que resuelve

Un error comun en APIs Spring Boot es dejar que la logica de persistencia compleja quede repartida entre:

- controladores
- servicios
- metodos derivados dificiles de leer

Cuando las consultas se vuelven mas expresivas, conviene encapsularlas en una capa orientada a persistencia custom.

## 7.2. Estructura recomendada

Una organizacion frecuente es:

- `MongoRepository` para CRUD simple
- repositorio custom con `MongoTemplate` para consultas complejas
- servicio que coordina reglas de negocio y decide que operacion usar

### Ejemplo conceptual

- `OrderRepository extends MongoRepository<Order, String>`
- `OrderQueryRepository` para filtros dinamicos y agregaciones
- `OrderService` para exponer operaciones al resto de la aplicacion

## 7.3. Por que beneficia a un codebase real

### Beneficios

- mantiene controladores livianos
- evita duplicar consultas complejas
- deja una frontera clara entre negocio y persistencia
- hace mas facil testear la capa que construye consultas

### Idea clave

No es arquitectura por moda.

Es separacion de responsabilidades para que el acceso a datos siga siendo mantenible cuando el proyecto crece.

## 7.4. Que no conviene hacer

No conviene:

- inyectar `MongoTemplate` directamente en controladores
- construir queries complejas dentro del endpoint
- mezclar parseo HTTP con decisiones de persistencia

El controlador deberia recibir datos del request, delegar al servicio y devolver la respuesta.

## 8. Transacciones y testing en contexto Spring

## 8.1. Transacciones: ubicacion correcta del tema

En el modulo 08 vimos que las transacciones existen, pero no deben usarse como solucion universal.

Eso sigue siendo cierto en Spring Boot.

### Cuando pueden aparecer con `MongoTemplate`

- operaciones que actualizan varias colecciones relacionadas
- flujos donde varios cambios deben confirmarse o fallar juntos
- casos donde el modelo no permite resolver la consistencia solo con un documento

### Criterio practico

Si el caso de uso cabe naturalmente en un solo documento bien modelado, muchas veces no hace falta transaccion.

Por eso este tema sigue conectado con:

- modelado del modulo 04
- consistencia del modulo 07
- transacciones del modulo 08

## 8.2. Testing: que conviene validar

En una capa basada en `MongoTemplate` no alcanza con saber que el codigo compila.

Conviene probar al menos:

- que la query construida responde lo esperado
- que los filtros opcionales realmente se aplican
- que un update parcial cambia solo lo necesario
- que una agregacion devuelve la forma esperada

## 8.3. Estrategias practicas

### Pruebas de integracion

Suelen ser las mas utiles para esta capa, porque validan el comportamiento real contra MongoDB.

Permiten comprobar:

- mapeo
- consultas
- ordenamiento
- updates
- agregaciones

### Pruebas orientadas a servicio

Tambien sirven para verificar que:

- el servicio elige bien entre repositorio simple y repositorio custom
- el controlador no necesita conocer detalles de persistencia

### Idea realista

No hace falta convertir este modulo en un tratado de testing.

Lo importante es entender que una consulta dinamica o una agregacion programatica deben validarse con casos concretos de datos, no solo con mocks.

## 9. Buenas practicas

## 9.1. Usa `MongoRepository` para CRUD simple

Si una operacion es directa y estable, no agregues complejidad innecesaria.

## 9.2. Usa `MongoTemplate` para consultas dinamicas y operaciones avanzadas

Especialmente cuando el problema pide:

- filtros opcionales
- updates parciales
- agregaciones
- persistencia custom

## 9.3. Manten la query legible

Una query flexible no deberia convertirse en una acumulacion desordenada de `if`.

Conviene:

- usar nombres claros
- agrupar criterios relacionados
- separar construccion de query y uso de la query cuando eso mejore lectura

## 9.4. Alinea las consultas con indices

Si un endpoint filtra u ordena siempre por ciertos campos, revisa si ese patron necesita indices adecuados.

Una buena API Java no compensa una consulta mal soportada por la base.

## 9.5. Alinea las operaciones con el modelado

Si necesitas muchas actualizaciones multi-documento o joins logicos permanentes, revisa el modelado.

`MongoTemplate` da flexibilidad, pero no corrige un modelo desacoplado de los casos de uso.

## 9.6. No mezcles persistencia compleja en controladores

Los controladores deben quedar orientados a HTTP.

La logica Mongo conviene mantenerla en servicios y repositorios custom.

## 9.7. Prefiere intencion sobre trucos

Cuando escribas persistencia con `MongoTemplate`, intenta que el codigo comunique:

- que estas filtrando
- por que actualizas esos campos
- por que esa agregacion responde una necesidad de backend

La meta no es solo que funcione.

La meta es que el equipo pueda leerla y mantenerla.

## 10. Resumen de criterio

`MongoTemplate` no reemplaza el valor de `MongoRepository`.

Lo complementa.

### Regla de trabajo recomendada

1. usa repositorio simple para lo rutinario
2. introduce template cuando el problema necesita mas control
3. encapsula la persistencia compleja en una capa clara
4. valida que la consulta tenga sentido respecto de indices, modelado y consistencia

Si puedes sostener ese criterio, ya estas usando `MongoTemplate` como una herramienta profesional y no solo como una API mas.
