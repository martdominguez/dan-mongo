package com.example.storeordersmongo.controller;

import com.example.storeordersmongo.dto.AverageOrderAmountResponse;
import com.example.storeordersmongo.dto.CreateOrderRequest;
import com.example.storeordersmongo.dto.OrderResponse;
import com.example.storeordersmongo.dto.OrderSearchCriteria;
import com.example.storeordersmongo.dto.OrdersByStatusResponse;
import com.example.storeordersmongo.dto.SalesByCategoryResponse;
import com.example.storeordersmongo.dto.SalesByDayResponse;
import com.example.storeordersmongo.dto.TopCustomerResponse;
import com.example.storeordersmongo.dto.TopSoldProductResponse;
import com.example.storeordersmongo.dto.UpdateOrderRequest;
import com.example.storeordersmongo.dto.UpdateOrderStatusRequest;
import com.example.storeordersmongo.model.OrderStatus;
import com.example.storeordersmongo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations for managing store orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Creates a new order and recalculates the total amount from items.")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public OrderResponse getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/order-number/{orderNumber}")
    @Operation(summary = "Get order by business order number")
    public OrderResponse getOrderByOrderNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByOrderNumber(orderNumber);
    }

    @GetMapping
    @Operation(summary = "List orders",
            description = "Lists orders using simple standard repository filters. When multiple filters are provided, priority is status, customerId, date range, then tag.")
    public List<OrderResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(required = false) String tag) {
        return orderService.getOrders(status, customerId, createdFrom, createdTo, tag);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "List orders by status")
    public List<OrderResponse> getByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrders(status, null, null, null, null);
    }

    @GetMapping("/by-customer/{customerId}")
    @Operation(summary = "List orders by customer id")
    public List<OrderResponse> getByCustomer(@PathVariable String customerId) {
        return orderService.getOrders(null, customerId, null, null, null);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order")
    public OrderResponse updateOrder(@PathVariable String id, @Valid @RequestBody UpdateOrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public OrderResponse updateOrderStatus(@PathVariable String id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete order")
    public void deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Dynamic search", description = "Searches orders with optional filters using MongoTemplate.")
    public List<OrderResponse> search(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(required = false) java.math.BigDecimal minimumAmount) {
        return orderService.searchOrders(new OrderSearchCriteria(status, city, paid, createdFrom, createdTo, minimumAmount));
    }

    @GetMapping("/search/by-item-category")
    @Operation(summary = "Find orders that contain items from a given category")
    public List<OrderResponse> findByItemCategory(@RequestParam String category) {
        return orderService.findOrdersByItemCategory(category);
    }

    @GetMapping("/search/by-min-item-quantity")
    @Operation(summary = "Find orders with at least one item above a quantity threshold")
    public List<OrderResponse> findByMinimumItemQuantity(@RequestParam int quantity) {
        return orderService.findOrdersWithItemQuantityGreaterThan(quantity);
    }

    @GetMapping("/reports/count-by-status")
    @Operation(summary = "Count orders by status")
    public List<OrdersByStatusResponse> countByStatus() {
        return orderService.countOrdersByStatus();
    }

    @GetMapping("/reports/sales-by-day")
    @Operation(summary = "Calculate total sales by day")
    public List<SalesByDayResponse> salesByDay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return orderService.totalSalesByDay(from, to);
    }

    @GetMapping("/reports/sales-by-category")
    @Operation(summary = "Calculate total sales by product category")
    public List<SalesByCategoryResponse> salesByCategory() {
        return orderService.totalSalesByCategory();
    }

    @GetMapping("/reports/top-products")
    @Operation(summary = "Get top sold products")
    public List<TopSoldProductResponse> topProducts(@RequestParam(defaultValue = "5") int limit) {
        return orderService.topSoldProducts(limit);
    }

    @GetMapping("/reports/average-order-amount")
    @Operation(summary = "Calculate average order amount in a date range")
    public AverageOrderAmountResponse averageOrderAmount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return orderService.averageOrderAmount(from, to);
    }

    @GetMapping("/reports/top-customers")
    @Operation(summary = "Get top customers by total purchased amount")
    public List<TopCustomerResponse> topCustomers(@RequestParam(defaultValue = "5") int limit) {
        return orderService.topCustomersByPurchasedAmount(limit);
    }
}
