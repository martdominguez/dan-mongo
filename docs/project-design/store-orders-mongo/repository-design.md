# Repository Design

## Repository split

The project uses two repository styles:

1. a standard repository based on `MongoRepository`
2. a custom repository implemented with `MongoTemplate`

This split keeps simple operations simple while still demonstrating flexible queries and aggregation pipelines.

## Standard repository

Recommended interface:

`OrderRepository extends MongoRepository<Order, String>, OrderCustomRepository`

The standard part should contain operations that Spring Data can express clearly through derived queries or small custom method signatures.

### Standard repository methods

- `Optional<Order> findByOrderNumber(String orderNumber)`
- `List<Order> findByStatus(OrderStatus status)`
- `List<Order> findByCustomerId(String customerId)`
- `List<Order> findByCreatedAtBetween(Instant from, Instant to)`
- `List<Order> findByTagsContaining(String tag)`
- `boolean existsByOrderNumber(String orderNumber)`

### Why these belong in `MongoRepository`

These methods:

- are simple and explicit
- map naturally to field-based queries
- do not need dynamic query construction
- improve readability for common use cases

This is a good teaching example of where repository derivation works well.

## Custom repository

Recommended custom contract:

`OrderCustomRepository`

Implementation:

`OrderCustomRepositoryImpl`

The custom repository should be responsible for queries that need one or more of:

- optional criteria
- nested array logic
- more expressive MongoDB operators
- aggregation pipelines

## Custom query operations

### Dynamic search with optional filters

Suggested method:

`List<Order> search(OrderSearchCriteria criteria)`

Optional filters:

- `status`
- `city`
- `paid`
- `createdFrom`
- `createdTo`
- `minimumAmount`

This should use `Criteria` and `Query` with only the conditions that are present in the request.

### Orders that contain items from a given category

Suggested method:

`List<Order> findOrdersByItemCategory(String category)`

This should query nested array fields such as `items.category`.

### Orders with at least one item above a quantity threshold

Suggested method:

`List<Order> findOrdersWithItemQuantityGreaterThan(int quantity)`

This can use `elemMatch` for clarity.

## Aggregation operations

Aggregation methods also belong in the custom repository because:

- they are too complex for derived queries
- they require pipeline stage composition
- the project explicitly requires programmatic implementations

Suggested methods:

- `List<OrdersByStatusProjection> countOrdersByStatus()`
- `List<SalesByDayProjection> totalSalesByDay(Instant from, Instant to)`
- `List<SalesByCategoryProjection> totalSalesByCategory()`
- `List<TopSoldProductProjection> topSoldProducts(int limit)`
- `AverageOrderAmountProjection averageOrderAmount(Instant from, Instant to)`
- `List<TopCustomerProjection> topCustomersByPurchasedAmount(int limit)`

## Why `MongoTemplate` is the right tool here

`MongoTemplate` is appropriate when the code needs:

- dynamic query construction
- nested criteria with optional filters
- array matching logic
- aggregation pipelines with `match`, `unwind`, `group`, `project`, `sort`, and `limit`

Using `MongoTemplate` here keeps complex persistence logic in one place and demonstrates practical Spring Data MongoDB usage beyond basic CRUD.

## Responsibility boundaries

Important rule:

repositories should only express data access logic.

They should not:

- validate business workflows
- decide whether a status transition is allowed
- calculate domain rules unrelated to persistence
- shape HTTP responses

Those responsibilities belong in services or controllers.

## Educational value of the split

This project intentionally shows both styles because that is common in real applications:

- use `MongoRepository` when the query is simple
- use `MongoTemplate` when the query or aggregation is more flexible

That distinction is one of the key learning outcomes of the project.
