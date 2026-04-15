# Implementation Plan

## Goal

Implement the project in a gradual order so the code remains understandable and testable at each step.

## Step 1. Create the base Spring Boot project

Set up:

- Maven project structure
- `pom.xml`
- Spring Boot starter dependencies
- Spring Data MongoDB
- validation starter
- springdoc OpenAPI starter
- application configuration

At this step, the project should build successfully even before business logic is added.

## Step 2. Implement the domain model

Create:

- `Order`
- `OrderItem`
- `DeliveryInfo`
- `PaymentInfo`
- `OrderStatus`

Also add:

- MongoDB collection annotation
- indexes where helpful, especially on `orderNumber`

This creates the persistence model foundation first.

## Step 3. Add standard repository support

Create:

- `OrderRepository`

Add simple repository methods for:

- find by id
- find by order number
- find by status
- find by customerId
- find by createdAt between
- find by tags containing

This gives the project minimal CRUD and simple listing capabilities.

## Step 4. Add DTOs and mapping strategy

Create request and response DTOs for:

- create order
- update order
- update status
- order response
- report responses
- search criteria

Keep mapping explicit in the service layer or in a simple mapper utility if needed. Avoid introducing a mapping framework for such a small project.

## Step 5. Implement the service layer for CRUD

Create:

- `OrderService`
- `OrderServiceImpl`

Service responsibilities:

- create order
- fetch order by id or order number
- update order
- update status
- delete order
- calculate item subtotals and total amount
- set `createdAt` and `updatedAt`
- check duplicate `orderNumber`

At this point the application should already support the main order lifecycle.

## Step 6. Implement the REST controller

Create:

- `OrderController`

Expose:

- create
- get by id
- get by order number
- update
- update status
- delete
- standard filter endpoints

Add OpenAPI annotations from the start so the generated docs remain aligned with the API.

## Step 7. Implement custom repository queries with `MongoTemplate`

Create:

- `repository.custom.OrderCustomRepository`
- `repository.impl.OrderCustomRepositoryImpl`

Implement:

- dynamic search with optional filters
- orders by item category
- orders by minimum item quantity

This is the first step where `MongoTemplate` is introduced.

## Step 8. Extend service and controller for search endpoints

Wire custom repository methods into the service layer and expose them through the controller.

Ensure the controller remains thin and the service coordinates use cases.

## Step 9. Implement aggregation pipelines

Add aggregation methods for:

- count orders by status
- total sales by day
- total sales by category
- top 5 sold products
- average order amount in date range
- top customers by total purchased amount

Expose them through read-only reporting endpoints.

## Step 10. Add validation and global exception handling

Create:

- custom exceptions such as `OrderNotFoundException` and `DuplicateOrderNumberException`
- `GlobalExceptionHandler`
- a reusable API error response model

Add validation annotations to request DTOs for required fields and positive amounts or quantities.

## Step 11. Swagger / OpenAPI polish

Add:

- API metadata configuration
- endpoint summaries and descriptions
- schema descriptions where useful
- grouped tags if helpful

This makes the project easier to explore in Swagger UI.

## Step 12. Final refinement

Perform a final cleanup pass:

- verify naming consistency
- remove unnecessary complexity
- keep constructor injection everywhere
- check package organization
- keep comments minimal and meaningful

## Recommended implementation order summary

1. base project
2. domain model
3. standard repository
4. DTOs
5. service CRUD
6. controller CRUD
7. custom repository queries
8. search endpoints
9. aggregations
10. validation and exception handling
11. Swagger polish
12. final cleanup

This order keeps the project easy to follow and ensures each feature builds on a stable base.
