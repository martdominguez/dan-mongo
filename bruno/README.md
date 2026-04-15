# Bruno Requests

This folder contains a small Bruno workspace for testing the main order endpoints.

## Structure

- `bruno/environments/local.bru`
  - local variables such as `baseUrl`, `orderId`, and `orderNumber`
- `bruno/collections/orders`
  - CRUD, search, and reporting requests

## Suggested flow

1. Run the application locally or with Docker Compose.
2. Open the `bruno` folder in Bruno.
3. Select the `local` environment.
4. Run `Create Order`.
5. Copy the generated `id` into the `orderId` variable.
6. Run the update, reporting, and delete requests as needed.

If you use Docker Compose, the default `baseUrl` remains `http://localhost:8080`.
