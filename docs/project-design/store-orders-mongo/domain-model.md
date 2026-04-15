# Domain Model

## Main document

The main collection is `orders`. Each document represents a complete order aggregate and contains:

- order metadata
- customer snapshot fields
- embedded order items
- embedded delivery information
- embedded payment information
- optional tags and notes

This follows a natural MongoDB modeling approach where data that is created, read, and updated together is stored together.

## Order document structure

Proposed fields:

- `id`: MongoDB `_id`
- `orderNumber`: business identifier exposed to clients
- `customerId`: external customer reference
- `customerName`: customer display name snapshot
- `status`: order status enum
- `createdAt`: order creation timestamp
- `updatedAt`: last update timestamp
- `totalAmount`: total order amount
- `items`: embedded array of order items
- `delivery`: embedded delivery information
- `payment`: embedded payment information
- `tags`: optional array of strings
- `notes`: optional free-text note

## Embedded documents

### Order item

Each order item is embedded because:

- items have meaning only inside an order
- items are usually retrieved together with the order
- the order should keep a historical product snapshot

Fields:

- `productId`
- `productName`
- `category`
- `quantity`
- `unitPrice`
- `subtotal`

### Delivery information

Delivery is embedded because it belongs to the order and is read with the order.

Fields:

- `recipientName`
- `phone`
- `addressLine`
- `city`
- `postalCode`
- `country`
- `deliveryInstructions`

### Payment information

Payment is embedded because it is a bounded part of the order state.

Fields:

- `paid`
- `paymentMethod`
- `transactionReference`
- `paidAt`

## Status model

Suggested enum:

- `CREATED`
- `CONFIRMED`
- `PREPARING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

These statuses are sufficient for a small commerce workflow and allow useful filtering and reporting.

## Modeling decisions and justification

### 1. Single aggregate per order

Orders are modeled as a single document because the application usually reads and writes the order as one unit. This avoids joins and keeps the aggregate simple.

### 2. Embedded items instead of separate collection

Items are not modeled as a separate collection because:

- they do not need an independent lifecycle
- the order needs a stable purchase-time snapshot
- the item list is typically limited and manageable in document size

### 3. Customer and product snapshots

The order stores `customerName`, `productName`, and `category` as snapshots rather than references only. This is useful for historical reporting and protects past orders from later changes in external systems.

### 4. Persisted `totalAmount`

The total is stored in the document for efficient reads and reporting. In the service layer, the value should be recalculated from item subtotals when an order is created or updated, rather than trusted from the client blindly.

### 5. Flexible optional metadata

`tags` and `notes` are included to show how MongoDB supports optional and semi-structured fields without introducing extra tables or unnecessary complexity.

## Example JSON document

```json
{
  "_id": "661d2f86e12c1d4e3a2b9001",
  "orderNumber": "ORD-2026-000123",
  "customerId": "CUST-1001",
  "customerName": "Ana Perez",
  "status": "CONFIRMED",
  "createdAt": "2026-04-10T14:20:00Z",
  "updatedAt": "2026-04-10T15:05:00Z",
  "totalAmount": 185.50,
  "items": [
    {
      "productId": "PROD-10",
      "productName": "Wireless Mouse",
      "category": "ACCESSORIES",
      "quantity": 2,
      "unitPrice": 25.00,
      "subtotal": 50.00
    },
    {
      "productId": "PROD-21",
      "productName": "Mechanical Keyboard",
      "category": "PERIPHERALS",
      "quantity": 1,
      "unitPrice": 135.50,
      "subtotal": 135.50
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
    "paid": true,
    "paymentMethod": "CARD",
    "transactionReference": "TXN-889122",
    "paidAt": "2026-04-10T14:25:00Z"
  },
  "tags": ["priority", "web"],
  "notes": "Customer requested invoice copy by email"
}
```

## Indexing suggestions

For a learning project, the code can remain simple, but these indexes are worth noting:

- unique index on `orderNumber`
- index on `status`
- index on `customerId`
- index on `createdAt`
- index on `tags`
- index on `delivery.city`
- multikey support naturally applies to `items.category`

These support the most common query and reporting patterns in the project.
