# API Design

## API style

The API is backend-oriented and RESTful. The main resource is `orders`, with dedicated reporting endpoints for analytics use cases.

Base path:

`/api/orders`

Reporting base path:

`/api/orders/reports`

## Main endpoints

### CRUD endpoints

- `POST /api/orders`
  - create a new order
- `GET /api/orders/{id}`
  - get order by MongoDB id
- `GET /api/orders/order-number/{orderNumber}`
  - get order by business order number
- `PUT /api/orders/{id}`
  - update an existing order
- `PATCH /api/orders/{id}/status`
  - update only the status
- `DELETE /api/orders/{id}`
  - delete an order

### Standard query endpoints

- `GET /api/orders`
  - list orders with simple optional parameters such as `status`, `customerId`, `createdFrom`, `createdTo`, `tag`
- `GET /api/orders/by-status/{status}`
  - list by status
- `GET /api/orders/by-customer/{customerId}`
  - list by customer

### Custom search endpoints

- `GET /api/orders/search`
  - dynamic search with optional filters:
    - `status`
    - `city`
    - `paid`
    - `createdFrom`
    - `createdTo`
    - `minimumAmount`
- `GET /api/orders/search/by-item-category`
  - query param: `category`
- `GET /api/orders/search/by-min-item-quantity`
  - query param: `quantity`

### Reporting endpoints

- `GET /api/orders/reports/count-by-status`
- `GET /api/orders/reports/sales-by-day`
- `GET /api/orders/reports/sales-by-category`
- `GET /api/orders/reports/top-products`
- `GET /api/orders/reports/average-order-amount`
- `GET /api/orders/reports/top-customers`

## Request DTOs

Recommended request records:

- `CreateOrderRequest`
- `UpdateOrderRequest`
- `UpdateOrderStatusRequest`
- `OrderItemRequest`
- `DeliveryInfoRequest`
- `PaymentInfoRequest`

Responsibilities:

- request DTOs represent HTTP input only
- service layer maps DTOs into domain objects
- validation annotations can be added to request DTOs later

## Response DTOs

Recommended response records:

- `OrderResponse`
- `OrderItemResponse`
- `DeliveryInfoResponse`
- `PaymentInfoResponse`
- `OrderSummaryResponse`

Reporting response records:

- `OrdersByStatusResponse`
- `SalesByDayResponse`
- `SalesByCategoryResponse`
- `TopSoldProductResponse`
- `AverageOrderAmountResponse`
- `TopCustomerResponse`

Search request model:

- `OrderSearchCriteria`

This can be used as a DTO passed from controller to service for optional filter handling.

## CRUD endpoints vs reporting endpoints

### CRUD-oriented endpoints

These manage individual order resources or filtered listings:

- `POST /api/orders`
- `GET /api/orders/{id}`
- `GET /api/orders/order-number/{orderNumber}`
- `PUT /api/orders/{id}`
- `PATCH /api/orders/{id}/status`
- `DELETE /api/orders/{id}`
- `GET /api/orders`
- `GET /api/orders/by-status/{status}`
- `GET /api/orders/by-customer/{customerId}`
- `GET /api/orders/search`
- `GET /api/orders/search/by-item-category`
- `GET /api/orders/search/by-min-item-quantity`

### Reporting endpoints

These are read-only endpoints backed by aggregation pipelines:

- `GET /api/orders/reports/count-by-status`
- `GET /api/orders/reports/sales-by-day`
- `GET /api/orders/reports/sales-by-category`
- `GET /api/orders/reports/top-products`
- `GET /api/orders/reports/average-order-amount`
- `GET /api/orders/reports/top-customers`

## Example create request

```json
{
  "orderNumber": "ORD-2026-000123",
  "customerId": "CUST-1001",
  "customerName": "Ana Perez",
  "status": "CREATED",
  "items": [
    {
      "productId": "PROD-10",
      "productName": "Wireless Mouse",
      "category": "ACCESSORIES",
      "quantity": 2,
      "unitPrice": 25.0
    }
  ],
  "delivery": {
    "recipientName": "Ana Perez",
    "phone": "+54-351-555-1200",
    "addressLine": "San Martin 1234",
    "city": "Cordoba",
    "postalCode": "5000",
    "country": "Argentina",
    "deliveryInstructions": "Leave at reception"
  },
  "payment": {
    "paid": false,
    "paymentMethod": "CARD",
    "transactionReference": null,
    "paidAt": null
  },
  "tags": ["web"],
  "notes": "First order"
}
```

## Example response shape

```json
{
  "id": "661d2f86e12c1d4e3a2b9001",
  "orderNumber": "ORD-2026-000123",
  "customerId": "CUST-1001",
  "customerName": "Ana Perez",
  "status": "CREATED",
  "createdAt": "2026-04-10T14:20:00Z",
  "updatedAt": "2026-04-10T14:20:00Z",
  "totalAmount": 50.0,
  "items": [
    {
      "productId": "PROD-10",
      "productName": "Wireless Mouse",
      "category": "ACCESSORIES",
      "quantity": 2,
      "unitPrice": 25.0,
      "subtotal": 50.0
    }
  ],
  "delivery": {
    "recipientName": "Ana Perez",
    "city": "Cordoba",
    "country": "Argentina"
  },
  "payment": {
    "paid": false,
    "paymentMethod": "CARD"
  },
  "tags": ["web"],
  "notes": "First order"
}
```

## Error handling

The API should return consistent error responses via global exception handling. A simple error shape is enough:

- `timestamp`
- `status`
- `error`
- `message`
- `path`

Typical cases:

- order not found
- duplicate order number
- invalid request payload
- invalid enum value
- unexpected server error
