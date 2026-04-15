# Ejemplos

## 1. Escenario: backend de pedidos con consultas dinamicas y reportes

### Problema

Un backend de ecommerce ya usa `MongoRepository` para CRUD simple de pedidos.

Ahora aparecen necesidades nuevas:

- filtrar pedidos por varios parametros opcionales
- ordenar y paginar resultados administrativos
- actualizar parcialmente el estado de un pedido
- reasignar en lote pedidos pendientes
- obtener un resumen de ventas por cliente

Este es un escenario natural para sumar `MongoTemplate` sin abandonar el repositorio simple para operaciones basicas.

## 2. Documento mapeado a Java

### Clase `Order`

```java
package com.example.orders.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
@CompoundIndex(name = "status_created_at_idx", def = "{'status': 1, 'createdAt': -1}")
public class Order {

    @Id
    private String id;

    private String customerId;
    private String status;
    private String channel;
    private BigDecimal total;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> productCategories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<String> productCategories) {
        this.productCategories = productCategories;
    }
}
```

### Que enseña este ejemplo

- el documento sigue siendo una entidad Spring Data comun
- `MongoTemplate` trabaja sobre la misma clase de dominio
- el indice compuesto refleja un patron habitual de filtro y orden por `status` y `createdAt`

### Conexion con modulos anteriores

El ejemplo recuerda dos ideas importantes:

- el modelado del modulo 04 influye en que filtros son naturales
- los indices del modulo 05 deben acompanar los patrones de consulta reales

## 3. Repositorio simple para CRUD previsible

### Interfaz `OrderRepository`

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
```

### Criterio

Seguimos usando `MongoRepository` donde aporta claridad.

Buscar por id, guardar y borrar por id no necesitan complejidad extra.

## 4. DTO para filtros dinamicos

### Clase `OrderSearchCriteria`

```java
package com.example.orders.service;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderSearchCriteria {

    private String status;
    private String customerId;
    private String channel;
    private String category;
    private BigDecimal minTotal;
    private BigDecimal maxTotal;
    private Instant createdFrom;
    private Instant createdTo;
    private int page = 0;
    private int size = 20;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getMinTotal() {
        return minTotal;
    }

    public void setMinTotal(BigDecimal minTotal) {
        this.minTotal = minTotal;
    }

    public BigDecimal getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(BigDecimal maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Instant getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Instant createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Instant getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(Instant createdTo) {
        this.createdTo = createdTo;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
```

### Intencion del diseno

Agrupar los filtros en un DTO evita pasar una lista larga de parametros y deja mas clara la construccion de la consulta.

## 5. Query + Criteria con ordenamiento y paginacion

### Repositorio custom `OrderQueryRepository`

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import com.example.orders.service.OrderSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrderQueryRepository {

    private final MongoTemplate mongoTemplate;

    public OrderQueryRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Order> search(OrderSearchCriteria searchCriteria) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (searchCriteria.getStatus() != null) {
            filters.add(Criteria.where("status").is(searchCriteria.getStatus()));
        }

        if (searchCriteria.getCustomerId() != null) {
            filters.add(Criteria.where("customerId").is(searchCriteria.getCustomerId()));
        }

        if (searchCriteria.getChannel() != null) {
            filters.add(Criteria.where("channel").is(searchCriteria.getChannel()));
        }

        if (searchCriteria.getCategory() != null) {
            filters.add(Criteria.where("productCategories").in(searchCriteria.getCategory()));
        }

        if (searchCriteria.getMinTotal() != null) {
            filters.add(Criteria.where("total").gte(searchCriteria.getMinTotal()));
        }

        if (searchCriteria.getMaxTotal() != null) {
            filters.add(Criteria.where("total").lte(searchCriteria.getMaxTotal()));
        }

        if (searchCriteria.getCreatedFrom() != null) {
            filters.add(Criteria.where("createdAt").gte(searchCriteria.getCreatedFrom()));
        }

        if (searchCriteria.getCreatedTo() != null) {
            filters.add(Criteria.where("createdAt").lte(searchCriteria.getCreatedTo()));
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.skip((long) searchCriteria.getPage() * searchCriteria.getSize());
        query.limit(searchCriteria.getSize());

        return mongoTemplate.find(query, Order.class);
    }
}
```

### Que enseña este ejemplo

- `Query` representa la consulta completa
- `Criteria` permite componer filtros paso a paso
- el ordenamiento se define de manera explicita
- la paginacion introductoria se resuelve con `skip` y `limit`
- la consulta final se mantiene en una clase dedicada a persistencia

### Por que esta estructura ayuda

En lugar de crear muchos metodos derivados, concentramos la logica de consulta en un solo punto.

Eso hace mas facil:

- agregar nuevos filtros
- revisar que campos necesitan indices
- probar el comportamiento de la query

## 6. Ejemplo especifico de filtrado dinamico

### Servicio `OrderService`

```java
package com.example.orders.service;

import com.example.orders.domain.Order;
import com.example.orders.repository.OrderQueryRepository;
import com.example.orders.repository.OrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    public OrderService(OrderRepository orderRepository, OrderQueryRepository orderQueryRepository) {
        this.orderRepository = orderRepository;
        this.orderQueryRepository = orderQueryRepository;
    }

    public Order findById(String id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + id));
    }

    public List<Order> search(OrderSearchCriteria searchCriteria) {
        return orderQueryRepository.search(searchCriteria);
    }
}
```

### Criterio arquitectonico

El servicio decide cuando usar el repositorio simple y cuando usar la capa custom con `MongoTemplate`.

El controlador no necesita saber nada sobre `Query`, `Criteria` o `Aggregation`.

## 7. Actualizacion parcial con `updateFirst`

### Problema

Un operador logistico necesita marcar una orden como despachada sin reescribir el documento completo.

### Metodo `markAsShipped`

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import java.time.Instant;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class OrderWriteRepository {

    private final MongoTemplate mongoTemplate;

    public OrderWriteRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean markAsShipped(String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));
        Update update = new Update()
            .set("status", "SHIPPED")
            .set("updatedAt", Instant.now());

        return mongoTemplate.updateFirst(query, update, Order.class)
            .getModifiedCount() > 0;
    }
}
```

### Que enseña este ejemplo

- `updateFirst` expresa que esperamos un documento puntual
- el update es parcial y explicito
- la operacion comunica intencion de negocio con buen nivel de detalle tecnico

## 8. Actualizacion multiple con `updateMulti`

### Problema

Un job administrativo debe reasignar todos los tickets pendientes de un agente inactivo a un nuevo agente.

### Documento `SupportTicket`

```java
package com.example.support.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "support_tickets")
public class SupportTicket {

    @Id
    private String id;

    private String status;
    private String assignedAgent;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

### Metodo `reassignPendingTickets`

```java
package com.example.support.repository;

import com.example.support.domain.SupportTicket;
import java.time.Instant;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SupportTicketWriteRepository {

    private final MongoTemplate mongoTemplate;

    public SupportTicketWriteRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public long reassignPendingTickets(String oldAgent, String newAgent) {
        Query query = Query.query(
            Criteria.where("assignedAgent").is(oldAgent)
                .and("status").is("PENDING")
        );

        Update update = new Update()
            .set("assignedAgent", newAgent)
            .set("updatedAt", Instant.now());

        return mongoTemplate.updateMulti(query, update, SupportTicket.class)
            .getModifiedCount();
    }
}
```

### Que enseña este ejemplo

- `updateMulti` afecta todos los documentos que cumplan la condicion
- el filtro debe ser preciso porque el impacto es mas amplio
- este tipo de operacion suele requerir revisar bien indices y alcance del cambio

## 9. Aggregation programatica

### Problema

El area comercial necesita un reporte de gasto total por cliente para pedidos pagados.

Este es un caso alineado con el modulo 06, pero ahora resuelto desde Spring Boot.

### DTO de salida

```java
package com.example.orders.service;

import java.math.BigDecimal;

public class CustomerSpendView {

    private String customerId;
    private long ordersCount;
    private BigDecimal totalSpent;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(long ordersCount) {
        this.ordersCount = ordersCount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
}
```

### Metodo `buildCustomerSpendReport`

```java
package com.example.orders.repository;

import com.example.orders.service.CustomerSpendView;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class OrderReportRepository {

    private final MongoTemplate mongoTemplate;

    public OrderReportRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CustomerSpendView> buildCustomerSpendReport() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").is("PAID")),
            Aggregation.group("customerId")
                .count().as("ordersCount")
                .sum("total").as("totalSpent"),
            Aggregation.project("ordersCount", "totalSpent")
                .and("_id").as("customerId"),
            Aggregation.sort(Sort.Direction.DESC, "totalSpent")
        );

        AggregationResults<CustomerSpendView> results =
            mongoTemplate.aggregate(aggregation, "orders", CustomerSpendView.class);

        return results.getMappedResults();
    }
}
```

### Que enseña este ejemplo

- la misma idea de `$match`, `$group`, `$project` y `$sort` ahora vive en la API Java
- el resultado se mapea a un DTO orientado a respuesta o reporte
- la agregacion queda encapsulada en una capa especifica en lugar de dispersarse por el servicio

### Conexion con modulos anteriores

Este ejemplo depende de entender:

- agregaciones del modulo 06
- elecciones de modelado del modulo 04
- soporte de indices para filtros frecuentes del modulo 05

## 10. Enfoque de custom repository completo

### Estructura sugerida

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import com.example.orders.service.OrderSearchCriteria;
import java.util.List;

public interface OrderCustomRepository {

    List<Order> search(OrderSearchCriteria criteria);

    boolean markAsShipped(String orderId);
}
```

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import com.example.orders.service.OrderSearchCriteria;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final MongoTemplate mongoTemplate;

    public OrderCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Order> search(OrderSearchCriteria criteria) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria.getStatus() != null) {
            filters.add(Criteria.where("status").is(criteria.getStatus()));
        }

        if (criteria.getCustomerId() != null) {
            filters.add(Criteria.where("customerId").is(criteria.getCustomerId()));
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public boolean markAsShipped(String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));
        Update update = new Update()
            .set("status", "SHIPPED")
            .set("updatedAt", Instant.now());

        return mongoTemplate.updateFirst(query, update, Order.class)
            .getModifiedCount() > 0;
    }
}
```

```java
package com.example.orders.repository;

import com.example.orders.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String>, OrderCustomRepository {
}
```

### Que enseña este enfoque

- el codebase conserva la comodidad de `MongoRepository`
- las capacidades custom se suman sin ensuciar controladores
- `MongoTemplate` queda encapsulado donde realmente corresponde

## 11. Discusion breve sobre testing

### Que conviene probar en estos ejemplos

- que una busqueda con filtros opcionales devuelve solo los pedidos esperados
- que el ordenamiento por `createdAt` responde de forma estable
- que `markAsShipped` modifica solo `status` y `updatedAt`
- que la agregacion de clientes pagadores devuelve conteos y montos correctos

### Estrategia practica

En esta clase de persistencia, una prueba de integracion con datos de ejemplo suele dar mas valor que un test puramente mockeado.

La razon es simple:

lo importante no es solo que se invoque un metodo, sino que la consulta construida realmente haga lo correcto sobre MongoDB.
