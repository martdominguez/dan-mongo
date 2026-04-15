# Aggregation Use Cases

## Purpose

The project includes a small set of reporting endpoints backed by MongoDB aggregation pipelines. These use cases are realistic for an operations or analytics dashboard and help demonstrate how MongoDB can answer business questions directly from order documents.

## 1. Count orders by status

### Business question

How many orders are currently in each status?

### Expected result shape

Each result row contains:

- `status`
- `count`

Example:

```json
[
  { "status": "CREATED", "count": 8 },
  { "status": "CONFIRMED", "count": 12 },
  { "status": "SHIPPED", "count": 5 }
]
```

### Likely MongoDB stages

- `group` by `status`
- `project`
- `sort`

## 2. Total sales by day

### Business question

What was the total amount sold per day in a date range?

### Expected result shape

- `day`
- `totalSales`
- `orderCount`

Example:

```json
[
  { "day": "2026-04-10", "totalSales": 1200.50, "orderCount": 9 },
  { "day": "2026-04-11", "totalSales": 980.00, "orderCount": 7 }
]
```

### Likely MongoDB stages

- `match` by `createdAt` range
- `project` a formatted day from `createdAt`
- `group` by day
- `sort`

## 3. Total sales by product category

### Business question

Which categories generate the most revenue?

### Expected result shape

- `category`
- `totalSales`
- `unitsSold`

Example:

```json
[
  { "category": "PERIPHERALS", "totalSales": 5500.00, "unitsSold": 42 },
  { "category": "ACCESSORIES", "totalSales": 2100.00, "unitsSold": 85 }
]
```

### Likely MongoDB stages

- `unwind` items
- `group` by `items.category`
- `sum` item `subtotal`
- `sum` item `quantity`
- `sort`

## 4. Top 5 sold products

### Business question

Which products sell the most across all orders?

### Expected result shape

- `productId`
- `productName`
- `category`
- `unitsSold`
- `totalSales`

Example:

```json
[
  {
    "productId": "PROD-21",
    "productName": "Mechanical Keyboard",
    "category": "PERIPHERALS",
    "unitsSold": 19,
    "totalSales": 2574.50
  }
]
```

### Likely MongoDB stages

- `unwind` items
- `group` by product fields
- `sort` by `unitsSold` descending
- `limit` 5
- `project`

## 5. Average order amount in a date range

### Business question

What is the average order value for a selected period?

### Expected result shape

- `from`
- `to`
- `averageAmount`
- `orderCount`

Example:

```json
{
  "from": "2026-04-01T00:00:00Z",
  "to": "2026-04-30T23:59:59Z",
  "averageAmount": 148.35,
  "orderCount": 31
}
```

### Likely MongoDB stages

- `match` by `createdAt` range
- `group` with `avg` on `totalAmount` and `count`
- `project`

## 6. Top customers by total purchased amount

### Business question

Which customers have spent the most?

### Expected result shape

- `customerId`
- `customerName`
- `totalPurchasedAmount`
- `orderCount`

Example:

```json
[
  {
    "customerId": "CUST-1001",
    "customerName": "Ana Perez",
    "totalPurchasedAmount": 2380.75,
    "orderCount": 11
  }
]
```

### Likely MongoDB stages

- `group` by `customerId` and `customerName`
- `sum` `totalAmount`
- `count`
- `sort`
- `limit`

## Notes on practical implementation

For this project:

- aggregations should be implemented in the custom repository with `MongoTemplate`
- result mapping can use small projection DTOs or records
- date-range filters should be optional where practical, but not required for every report
- the project can include defaults such as `top 5` for product and customer rankings

## Why these use cases are a good fit

These reporting cases are intentionally small but effective because they show:

- grouping by top-level fields
- grouping by nested array fields
- `unwind` usage
- ranking with `sort` and `limit`
- date-based reporting
- aggregation results mapped into API responses

Together they provide a strong introduction to MongoDB’s aggregation framework in a Spring Boot backend.
