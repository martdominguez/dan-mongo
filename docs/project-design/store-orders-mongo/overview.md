# Store Orders Mongo Project Overview

## Project goal

This project is a small but realistic Spring Boot + MongoDB backend for managing store orders in a commerce business. It is designed as a learning project that demonstrates backend architecture, REST API design, MongoDB document modeling, repository patterns, and reporting with aggregations.

The application focuses on one domain only: `orders`. This keeps the codebase approachable while still covering the kinds of decisions that appear in real backend projects.

## Scope

The project includes:

- Spring Boot with Maven
- MongoDB and Spring Data MongoDB
- REST API for order management
- CRUD operations for orders
- search endpoints with optional filters
- standard repositories with `MongoRepository`
- custom repositories implemented with `MongoTemplate`
- aggregation pipelines for reporting
- service layer and repository layer separation
- request/response DTOs
- global exception handling
- Swagger / OpenAPI documentation

The project intentionally does not include:

- authentication and authorization
- inventory management
- product catalog management
- multi-tenant support
- event-driven architecture
- full payment gateway integration

These are valid extensions for a larger system, but excluding them keeps the project educational and manageable.

## Main use cases

The backend supports practical use cases such as:

1. create an order with embedded items, delivery data, and payment data
2. retrieve an order by id or order number
3. update editable fields such as status, notes, tags, payment state, or delivery details
4. list orders by simple filters like status, customer, or date range
5. run dynamic searches with optional filters without creating many repository methods
6. identify orders containing items from a specific category
7. identify orders with high item quantities
8. generate operational and reporting metrics through aggregation pipelines

## Why MongoDB is a good fit

MongoDB fits this project well because an order is naturally a document-oriented aggregate.

Reasons:

- an order has a clear lifecycle and is usually loaded as a whole
- order items are a natural embedded list inside the order document
- delivery and payment information are also natural embedded subdocuments
- optional fields such as tags and notes are easy to represent without schema friction
- search features can combine document fields, arrays, and nested fields
- reporting requirements benefit from MongoDB aggregation pipelines

For a small commerce backend, storing the full order snapshot in one document is a practical and common design choice. It reduces join complexity and matches how orders are typically consumed in backend APIs.

## Architectural approach

The project follows a conventional layered style:

- controller layer: exposes REST endpoints and OpenAPI metadata
- service layer: coordinates use cases and business rules
- repository layer: handles persistence concerns only
- model layer: MongoDB document classes and embedded value objects
- dto layer: request/response contracts for the API

This separation helps demonstrate where responsibilities belong:

- controllers handle HTTP concerns
- services handle application behavior
- repositories handle query construction and data access

## Practical assumptions

To keep the scope focused, the project will assume:

- customer data is stored as a snapshot in the order document through `customerId` and `customerName`
- product data is stored as a snapshot in order items through `productId`, `productName`, and `category`
- payment information stores whether the order is paid, payment method, transaction reference, and payment timestamp
- delivery information stores recipient data and address fields, including city
- total amount is persisted in the document for fast reads, even though it can be derived from items

These assumptions are realistic for order management systems because historical orders should preserve the values used at purchase time.
