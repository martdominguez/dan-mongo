# Teoria

## 1. Que problema resuelve este modulo

En los modulos anteriores trabajamos MongoDB desde la perspectiva de la base de datos. Eso sirve para entender modelado, consultas e indices, pero un backend real necesita una capa adicional: integrar esa base dentro de una aplicacion.

En un proyecto Spring Boot aparecen preguntas concretas:

- como se abre la conexion a MongoDB sin escribir demasiado codigo repetitivo
- como se representa un documento como objeto Java
- como se guarda o recupera informacion sin armar cada consulta desde cero
- como se organiza el flujo entre controlador, servicio y persistencia

Spring Data MongoDB resuelve gran parte de ese trabajo repetitivo.

La idea de este modulo es mostrar esa integracion desde una perspectiva practica:

- configuracion minima
- mapeo basico
- CRUD simple
- consultas derivadas introductorias
- limites de la abstraccion

No vamos a profundizar todavia en consultas dinamicas complejas ni en agregaciones programaticas. Esos temas pertenecen mas naturalmente al siguiente modulo con `MongoTemplate`.

## 2. Spring Data MongoDB

## 2.1. Que es

Spring Data MongoDB es el modulo del ecosistema Spring que integra aplicaciones Java con MongoDB usando convenciones, mapeo de documentos y abstracciones de acceso a datos.

Su objetivo principal es reducir codigo de infraestructura para que el desarrollador se concentre mas en:

- el modelo de dominio
- las operaciones que realmente necesita el backend
- la organizacion de responsabilidades dentro de la aplicacion

## 2.2. Que proporciona

### Concepto

Spring Data MongoDB aporta un conjunto de capacidades que simplifican el acceso a datos.

### Capacidades introductorias mas relevantes

- configuracion integrada con Spring Boot
- conversion entre documentos MongoDB y objetos Java
- soporte para anotaciones como `@Document`, `@Id` y `@Field`
- repositorios con operaciones CRUD listas para usar
- derivacion de consultas a partir del nombre de metodos
- integracion natural con inyeccion de dependencias y capas de servicio

### Intencion del desarrollador

La intencion no es ocultar MongoDB por completo. La intencion es eliminar trabajo mecanico y dejar una capa de persistencia mas legible para casos comunes.

## 2.3. Cuando `MongoRepository` es una buena opcion

`MongoRepository` funciona bien cuando el backend necesita operaciones simples y repetibles sobre una coleccion.

### Casos tipicos

- alta, baja y modificacion de productos
- consulta por id de un usuario
- listado de tickets
- filtros sencillos como buscar pedidos por `status`
- consultas por combinaciones simples de campos conocidos

### Beneficios

- menos codigo boilerplate
- interfaz declarativa
- aprendizaje rapido para equipos Spring
- integracion directa con el ciclo habitual `controller -> service -> repository`

## 2.4. Beneficios y limites de la abstraccion

### Beneficios

#### `save`

Permite persistir una entidad sin escribir manualmente un `insertOne` o `replaceOne`.

#### `findById`

Permite recuperar un documento por identificador con una API clara y tipada.

#### `findAll`

Sirve para listados introductorios o escenarios internos donde el volumen sea acotado.

#### `deleteById`

Resuelve un borrado puntual sin escribir la consulta manualmente.

#### Metodos derivados

Permiten declarar consultas comunes directamente en la interfaz del repositorio.

### Limites

#### Consultas dinamicas

Si los filtros cambian mucho segun parametros opcionales, el repositorio derivado se vuelve menos expresivo y mas incomodo de mantener.

#### Agregaciones complejas

`MongoRepository` no es la via principal para pipelines complejos. Puede existir algun soporte adicional en Spring Data, pero no es la herramienta introductoria correcta para ese tipo de problema.

#### Control fino de consultas

Cuando necesitas construir criterios programaticamente, proyectar de forma muy especifica o combinar logica mas flexible, la abstraccion empieza a quedar corta.

### Idea clave

La abstraccion ayuda cuando simplifica el trabajo. Deja de ayudar cuando obliga a forzar el diseño del codigo alrededor de sus limites.

## 3. Configuracion del proyecto

## 3.1. Dependencia principal

### Concepto

Para usar Spring Data MongoDB en Spring Boot, la dependencia central suele ser el starter oficial.

### Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Gradle

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

### Que resuelve

Esta dependencia incorpora la integracion necesaria para:

- crear el cliente Mongo
- leer configuracion desde propiedades
- registrar conversiones y mapeo
- habilitar el soporte de repositorios

## 3.2. Configuracion basica

Spring Boot permite configurar MongoDB con `application.yml` o `application.properties`.

### Variacion 1: `application.yml`

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/support_db
```

### Variacion 2: `application.properties`

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/support_db
```

### Intencion

La intencion de esta configuracion es centralizar en un solo punto la conexion del backend contra la base de datos.

## 3.3. Connection string basica

### Sintaxis general

```text
mongodb://host:puerto/base_de_datos
```

### Ejemplo local

```text
mongodb://localhost:27017/support_db
```

### Componentes

- `mongodb://`: protocolo de conexion
- `localhost`: host donde corre MongoDB
- `27017`: puerto por defecto habitual
- `support_db`: nombre de la base de datos

### Variacion con credenciales

```text
mongodb://app_user:app_password@localhost:27017/support_db
```

### Advertencia

En desarrollo local suele verse una URI simple. En entornos reales conviene manejar credenciales y configuracion sensible fuera del codigo fuente, por ejemplo con variables de entorno o configuracion externa.

## 3.4. Supuestos de desarrollo local

En este modulo vamos a asumir un entorno introductorio:

- MongoDB corriendo localmente
- una sola instancia
- una base como `support_db` o `catalog_db`
- credenciales simples o incluso sin autenticacion en un entorno controlado de aprendizaje

### Por que explicitar este supuesto

Ayuda a no mezclar desde el comienzo temas de infraestructura, replica sets, secretos y despliegues, que no son el foco del modulo.

## 4. Mapeo de documentos a Java

## 4.1. `@Document`

### Concepto

`@Document` marca una clase como representacion de una coleccion MongoDB.

### Sintaxis

```java
@Document("support_tickets")
public class SupportTicket {
}
```

### Variacion comun

```java
@Document(collection = "support_tickets")
public class SupportTicket {
}
```

### Intencion del desarrollador

La intencion es declarar con claridad que la clase participa en persistencia documental y a que coleccion se asocia.

## 4.2. `@Id`

### Concepto

`@Id` identifica el campo que Spring Data va a tratar como identificador del documento.

### Sintaxis

```java
@Id
private String id;
```

### Consideracion practica

En ejemplos introductorios suele usarse `String` para simplificar lectura y transporte en APIs. Spring Data tambien puede trabajar con `ObjectId`, pero para una primera integracion un `String` suele hacer el flujo mas claro para el estudiante.

## 4.3. `@Field`

### Concepto

`@Field` permite mapear un atributo Java con un nombre de campo distinto en MongoDB.

### Sintaxis

```java
@Field("customer_email")
private String customerEmail;
```

### Cuando usarlo

- cuando el nombre persistido ya existe y no quieres cambiarlo
- cuando el modelo Java necesita una convencion distinta
- cuando quieres hacer explicita una diferencia entre dominio y almacenamiento

### Advertencia

Si no existe una necesidad real, conviene no complicar el mapeo. En una primera etapa es mejor mantener nombres claros y consistentes entre clase y documento.

## 4.4. Diseño simple de entidad

### Principio

Una entidad introductoria para MongoDB en Spring Boot debe ser:

- facil de leer
- coherente con el documento real
- acotada a los datos que esa coleccion necesita persistir

### Recomendaciones practicas

- no mezclar demasiadas responsabilidades en la misma clase
- usar nombres de campos que reflejen el dominio
- representar listas o subdocumentos solo cuando mejoran claridad del caso
- evitar meter logica de negocio compleja dentro de la entidad persistida

### Ejemplo conceptual

Una clase `Product` puede incluir:

- `id`
- `name`
- `category`
- `price`
- `active`

Eso es suficiente para explicar CRUD y consultas derivadas sin agregar ruido innecesario.

## 5. `MongoRepository`

## 5.1. Que es

`MongoRepository` es una interfaz de Spring Data que expone operaciones CRUD y capacidades de consulta sobre una entidad MongoDB.

### Sintaxis general

```java
public interface ProductRepository extends MongoRepository<Product, String> {
}
```

### Parametros genericos

- `Product`: tipo de entidad
- `String`: tipo del identificador

### Que aporta de inmediato

- `save`
- `findById`
- `findAll`
- `deleteById`
- otros metodos comunes de repositorio

## 5.2. `save`

### Concepto

Persiste una entidad.

### Uso introductorio

```java
productRepository.save(product);
```

### Intencion

Se usa tanto para alta inicial como para guardar cambios sobre una entidad ya conocida.

### Advertencia introductoria

Aunque el metodo se vea simple, la claridad del flujo depende de que el servicio tenga bien definida su responsabilidad. No conviene llamar a `save` desde cualquier capa sin criterio.

## 5.3. `findById`

### Concepto

Busca un documento por su identificador.

### Uso introductorio

```java
Optional<Product> product = productRepository.findById(id);
```

### Por que devuelve `Optional`

Porque el documento puede no existir. Eso obliga al servicio a decidir como responder:

- lanzar una excepcion controlada
- devolver un DTO vacio no suele ser buena idea
- transformar ausencia en `404` desde la capa HTTP

## 5.4. `findAll`

### Concepto

Recupera todos los documentos de la coleccion mapeada.

### Uso introductorio

```java
List<Product> products = productRepository.findAll();
```

### Advertencia

`findAll` es util para ejemplos, pruebas o colecciones chicas. En sistemas reales no conviene asumir que siempre sera una consulta segura si el volumen crece.

## 5.5. `deleteById`

### Concepto

Elimina un documento por identificador.

### Uso introductorio

```java
productRepository.deleteById(id);
```

### Intencion del desarrollador

Resolver un borrado puntual sin escribir una consulta manual.

### Consideracion de backend

Antes de borrar, el servicio suele necesitar validar si la operacion tiene sentido funcional, por ejemplo si el recurso existe o si el negocio prefiere una baja logica en lugar de una eliminacion fisica.

## 5.6. Metodos derivados

### Concepto

Spring Data puede construir consultas sencillas a partir del nombre del metodo declarado en el repositorio.

### Ejemplos introductorios

```java
List<Product> findByCategory(String category);
List<Product> findByActiveTrue();
List<SupportTicket> findByStatus(String status);
List<SupportTicket> findByPriorityAndStatus(String priority, String status);
```

### Intencion

La idea es expresar consultas previsibles de manera declarativa, sin bajar todavia a una API mas flexible.

### Variaciones utiles en un nivel introductorio

- buscar por igualdad de un campo
- buscar por booleanos
- combinar dos campos simples
- ordenar despues en servicio solo si el caso es muy sencillo

### Advertencia

Si empiezas a necesitar muchos metodos casi iguales o nombres demasiado largos, probablemente el caso pide otra estrategia.

## 6. Flujo backend practico

## 6.1. `controller -> service -> repository`

### Concepto

Una organizacion simple y entendible en Spring Boot suele separar:

- controlador: entrada HTTP
- servicio: decisiones de aplicacion y coordinacion
- repositorio: acceso a datos

### Intencion de la separacion

Cada capa debe responder una pregunta distinta:

- el controlador recibe y devuelve HTTP
- el servicio decide que operacion del negocio ejecutar
- el repositorio persiste o consulta

### Beneficio

Esta separacion facilita:

- pruebas mas simples
- codigo mas legible
- cambios localizados
- menor acoplamiento entre web y persistencia

## 6.2. Dominio, DTO y persistencia

En un modulo introductorio no conviene sobrecargar al estudiante con demasiadas capas artificiales, pero si conviene marcar una idea importante:

- la entidad persistida no siempre debe exponerse directamente como contrato HTTP

### Distincion introductoria

- dominio o entidad: representa lo que se guarda
- DTO de entrada: representa lo que llega desde la API
- DTO de salida: representa lo que quieres devolver

### Cuando mantenerlo simple

Si el caso es pequeño, puedes usar una estrategia minima:

- request DTO para crear o actualizar
- entidad persistida en la capa de repositorio
- response DTO o respuesta simple desde el controlador

### Por que importa

Evita que decisiones de persistencia condicionen por completo la API publica.

## 6.3. Ejemplo de intencion en una API simple

Supongamos una API de tickets:

- el controlador recibe una solicitud de creacion
- el servicio construye el `SupportTicket`
- el repositorio hace `save`
- luego el controlador devuelve la representacion adecuada

Ese flujo deja claro donde vive cada responsabilidad.

## 7. Buenas practicas

## 7.1. Inyeccion por constructor

### Concepto

La inyeccion por constructor hace explicitas las dependencias necesarias de una clase.

### Ejemplo conceptual

```java
public TicketService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
}
```

### Beneficios

- dependencias obligatorias visibles
- clases mas faciles de probar
- menor ambiguedad que con inyeccion por campo

## 7.2. Limites claros en servicios

### Recomendacion

El servicio no deberia convertirse en un contenedor de codigo desordenado.

Conviene que cada servicio:

- coordine un conjunto coherente de casos de uso
- aplique validaciones de aplicacion razonables
- delegue persistencia al repositorio

## 7.3. Mantener simple el uso del repositorio

### Idea clave

Si el caso es simple, deja que el repositorio siga siendo simple.

No hace falta forzar:

- nombres de metodos gigantes
- logica de negocio dentro del repositorio
- repositorios llenos de variantes marginales

## 7.4. Usar `MongoRepository` para operaciones directas

`MongoRepository` es muy bueno cuando resuelve:

- CRUD basico
- filtros por igualdad
- consultas previsibles

No es buena señal si intentas usarlo como unica herramienta para absolutamente todo lo relacionado con MongoDB.

## 8. Limites de `MongoRepository`

## 8.1. Consultas dinamicas

### Problema

Imagina un endpoint de busqueda de productos con filtros opcionales:

- categoria
- precio minimo
- precio maximo
- activo
- texto parcial del nombre

Si intentas resolver todas las combinaciones solo con metodos derivados, la interfaz del repositorio empieza a escalar mal.

### Conclusion practica

Cuando la consulta depende de muchas combinaciones dinamicas, conviene una herramienta mas flexible.

## 8.2. Agregaciones complejas

Si el caso necesita:

- `pipeline`
- agrupaciones
- proyecciones transformadas
- joins documentales mas elaborados

entonces `MongoRepository` deja de ser el foco natural.

### Idea clave

No significa que el repositorio sea inutil. Significa que el problema ya no es CRUD introductorio.

## 8.3. Puente hacia el siguiente modulo

El siguiente modulo trabajara con `MongoTemplate`.

### Por que

Porque `MongoTemplate` permite:

- construir consultas con mas control
- manejar criterios dinamicos
- trabajar agregaciones de forma programatica
- bajar un nivel cuando la abstraccion del repositorio ya no alcanza

### Mensaje pedagogico importante

No hay que elegir una herramienta por moda. Hay que elegirla segun el tipo de problema.

En esta etapa, la prioridad es aprender a reconocer cuando una abstraccion simple ayuda de verdad.

## 9. Resumen operativo

Si estas integrando MongoDB en una aplicacion Spring Boot y el caso de uso es simple, una secuencia razonable es esta:

1. agregar `spring-boot-starter-data-mongodb`
2. configurar la URI de conexion
3. mapear el documento con `@Document` y `@Id`
4. crear un repositorio que extienda `MongoRepository`
5. encapsular el acceso desde un servicio
6. exponer endpoints REST claros
7. frenar y reevaluar cuando las consultas se vuelvan demasiado dinamicas o complejas

Ese criterio es mas importante que memorizar solo anotaciones o metodos.
