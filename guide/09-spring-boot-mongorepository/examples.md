# Ejemplos

## 1. Escenario: API simple de tickets de soporte

### Problema

Un backend de soporte necesita:

- crear tickets
- obtener un ticket por id
- listar tickets
- borrar tickets cerrados por id
- filtrar tickets por estado o prioridad

Es un caso adecuado para `MongoRepository` porque las operaciones son directas y previsibles.

## 2. Documento mapeado a Java

### Clase `SupportTicket`

```java
package com.example.support.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "support_tickets")
public class SupportTicket {

    @Id
    private String id;

    private String customerEmail;
    private String subject;
    private String status;
    private String priority;

    @Field("assigned_agent")
    private String assignedAgent;

    public SupportTicket() {
    }

    public SupportTicket(String id, String customerEmail, String subject, String status, String priority, String assignedAgent) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.assignedAgent = assignedAgent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }
}
```

### Que enseña este ejemplo

- `@Document` asocia la clase con la coleccion `support_tickets`
- `@Id` marca el identificador persistido
- `@Field` muestra un caso donde el nombre del atributo Java difiere del nombre del documento

### Intencion del diseño

La clase se mantiene simple para que sea evidente que el objetivo principal es mapear un documento y persistirlo. No estamos agregando logica compleja dentro de la entidad.

## 3. Repositorio con CRUD y consultas derivadas

### Interfaz `SupportTicketRepository`

```java
package com.example.support.repository;

import com.example.support.domain.SupportTicket;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SupportTicketRepository extends MongoRepository<SupportTicket, String> {

    List<SupportTicket> findByStatus(String status);

    List<SupportTicket> findByPriority(String priority);

    List<SupportTicket> findByPriorityAndStatus(String priority, String status);
}
```

### Que enseña este ejemplo

- extender `MongoRepository<SupportTicket, String>` habilita CRUD basico
- los metodos derivados permiten agregar filtros simples sin escribir una consulta manual

### Cuando sigue siendo un buen diseño

Mientras el backend necesite consultas como:

- tickets abiertos
- tickets de prioridad alta
- tickets de prioridad alta y estado pendiente

esta interfaz sigue siendo clara y suficiente.

## 4. Servicio con responsabilidades simples

### DTO de entrada para crear tickets

```java
package com.example.support.api;

public class CreateTicketRequest {

    private String customerEmail;
    private String subject;
    private String priority;

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
```

### Servicio `SupportTicketService`

```java
package com.example.support.service;

import com.example.support.api.CreateTicketRequest;
import com.example.support.domain.SupportTicket;
import com.example.support.repository.SupportTicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public SupportTicket createTicket(CreateTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setSubject(request.getSubject());
        ticket.setPriority(request.getPriority());
        ticket.setStatus("OPEN");

        return supportTicketRepository.save(ticket);
    }

    public SupportTicket findById(String id) {
        return supportTicketRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado: " + id));
    }

    public List<SupportTicket> findAll() {
        return supportTicketRepository.findAll();
    }

    public List<SupportTicket> findByStatus(String status) {
        return supportTicketRepository.findByStatus(status);
    }

    public List<SupportTicket> findByPriorityAndStatus(String priority, String status) {
        return supportTicketRepository.findByPriorityAndStatus(priority, status);
    }

    public void deleteById(String id) {
        supportTicketRepository.deleteById(id);
    }
}
```

### Que enseña este ejemplo

- el servicio recibe el request DTO y construye la entidad persistida
- el repositorio no decide reglas de aplicacion
- la capa de servicio centraliza un flujo claro y facil de probar

### Intencion del desarrollador

La intencion no es crear una arquitectura pesada. La intencion es evitar que el controlador quede lleno de detalles de persistencia.

## 5. Controlador REST simple

### Controlador `SupportTicketController`

```java
package com.example.support.api;

import com.example.support.domain.SupportTicket;
import com.example.support.service.SupportTicketService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public SupportTicket create(@RequestBody CreateTicketRequest request) {
        return supportTicketService.createTicket(request);
    }

    @GetMapping("/{id}")
    public SupportTicket findById(@PathVariable String id) {
        return supportTicketService.findById(id);
    }

    @GetMapping
    public List<SupportTicket> findAll(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return supportTicketService.findByStatus(status);
        }

        return supportTicketService.findAll();
    }

    @GetMapping("/search")
    public List<SupportTicket> searchByPriorityAndStatus(
        @RequestParam String priority,
        @RequestParam String status
    ) {
        return supportTicketService.findByPriorityAndStatus(priority, status);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        supportTicketService.deleteById(id);
    }
}
```

### Que enseña este ejemplo

- el controlador se concentra en HTTP
- el servicio concentra la coordinacion
- el repositorio queda oculto detras de la capa de servicio

### Advertencia practica

En una API real probablemente quieras:

- devolver codigos HTTP mas expresivos
- mapear excepciones a `404` o `400`
- no exponer siempre la entidad tal cual

Pero para una primera integracion este ejemplo deja claro el flujo general.

## 6. Configuracion minima del proyecto

### `application.yml`

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/support_db
```

### Que enseña este ejemplo

- el backend se conecta a una base local llamada `support_db`
- no hace falta crear manualmente un cliente Mongo en este nivel introductorio

## 7. Segundo escenario breve: catalogo de productos

### Problema

Un backend de catalogo necesita:

- guardar productos
- listar productos activos
- filtrar por categoria

### Entidad `Product`

```java
package com.example.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String category;
    private Double price;
    private boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
```

### Repositorio `ProductRepository`

```java
package com.example.catalog.repository;

import com.example.catalog.domain.Product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findByActiveTrue();
}
```

### Que enseña este ejemplo

- `findByCategory` representa un filtro por igualdad muy comun
- `findByActiveTrue` muestra una variacion expresiva para booleanos

### Por que `MongoRepository` alcanza aqui

El caso sigue siendo CRUD con filtros directos. No hace falta una herramienta mas flexible si el problema no la pide.

## 8. Limite practico del enfoque

Imagina ahora un endpoint de productos con filtros opcionales:

- categoria
- rango de precio
- texto parcial
- activo
- proveedor

Si cada combinacion se intenta resolver con metodos derivados, el repositorio se degrada rapido.

### Mensaje clave

Estos ejemplos muestran el punto fuerte de `MongoRepository`: operaciones simples y previsibles.

Cuando la consulta deja de ser simple, el problema ya esta pidiendo otra herramienta. Ese sera el puente hacia `MongoTemplate`.
