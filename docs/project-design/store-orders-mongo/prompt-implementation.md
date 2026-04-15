# Prompt For Later Implementation

Use the following prompt when asking Codex to implement the project:

---

Implement a small but realistic Spring Boot + MongoDB backend with Maven for managing store orders.

Use the following documentation files as the source of truth:

- `docs/project-design/store-orders-mongo/overview.md`
- `docs/project-design/store-orders-mongo/domain-model.md`
- `docs/project-design/store-orders-mongo/api-design.md`
- `docs/project-design/store-orders-mongo/repository-design.md`
- `docs/project-design/store-orders-mongo/aggregation-use-cases.md`
- `docs/project-design/store-orders-mongo/implementation-plan.md`

Implementation requirements:

- Use Spring Boot
- Use MongoDB
- Use Spring Data MongoDB
- Use REST controllers
- Use a service layer
- Use a repository layer
- Include both `MongoRepository` and a custom repository implemented with `MongoTemplate`
- Implement custom queries and aggregation pipelines programmatically
- Use DTOs where appropriate
- Add global exception handling with `@ControllerAdvice`
- Add Swagger / OpenAPI documentation
- Use constructor injection
- Use Java records for DTOs where appropriate
- Keep the codebase small, clean, and educational

Functional scope:

- Manage orders only
- Include order fields:
  - id
  - orderNumber
  - customerId
  - customerName
  - status
  - createdAt
  - updatedAt
  - totalAmount
  - items
  - delivery information
  - payment information
  - optional tags
  - optional notes
- Include order item fields:
  - productId
  - productName
  - category
  - quantity
  - unitPrice
  - subtotal
- Supported statuses:
  - CREATED
  - CONFIRMED
  - PREPARING
  - SHIPPED
  - DELIVERED
  - CANCELLED

Required package areas:

- `controller`
- `service`
- `repository`
- `repository.custom`
- `repository.impl`
- `dto`
- `model`
- `exception`
- `config`

Required repository operations:

Standard repository:

- find by id
- find by status
- find by customerId
- find by createdAt between dates
- find by tags containing a value

Custom repository:

- dynamic search with optional filters:
  - status
  - city
  - paid
  - createdFrom
  - createdTo
  - minimumAmount
- orders that contain items from a given category
- orders with at least one item above a quantity threshold

Aggregation use cases:

- count orders by status
- total sales by day
- total sales by product category
- top 5 sold products
- average order amount in a date range
- top customers by total purchased amount

Implementation guidance:

- Start with the minimal CRUD flow
- Then add dynamic search
- Then add aggregation endpoints
- Then add validation and error handling
- Then polish Swagger/OpenAPI metadata
- Recalculate item subtotals and total amount in the service layer
- Keep repositories free of business rules
- Keep controllers thin
- Avoid overengineering

Expected deliverables:

- full Maven project structure
- `pom.xml`
- Spring Boot application entry point
- application configuration
- domain model classes
- DTOs
- repositories
- custom repository implementation
- services
- REST controller
- exception handling
- OpenAPI configuration

Also provide a concise summary of:

- the document model
- the endpoint design
- the repository split
- the aggregation features

---

Notes:

- If something is ambiguous, choose practical defaults and document them briefly in code or comments only when necessary.
- Keep the project ready for local execution with MongoDB.
