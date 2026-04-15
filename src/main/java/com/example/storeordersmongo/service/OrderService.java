package com.example.storeordersmongo.service;

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

import java.time.Instant;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(String id);

    OrderResponse getOrderByOrderNumber(String orderNumber);

    List<OrderResponse> getOrders(OrderStatus status, String customerId, Instant createdFrom, Instant createdTo, String tag);

    OrderResponse updateOrder(String id, UpdateOrderRequest request);

    OrderResponse updateOrderStatus(String id, UpdateOrderStatusRequest request);

    void deleteOrder(String id);

    List<OrderResponse> searchOrders(OrderSearchCriteria criteria);

    List<OrderResponse> findOrdersByItemCategory(String category);

    List<OrderResponse> findOrdersWithItemQuantityGreaterThan(int quantity);

    List<OrdersByStatusResponse> countOrdersByStatus();

    List<SalesByDayResponse> totalSalesByDay(Instant from, Instant to);

    List<SalesByCategoryResponse> totalSalesByCategory();

    List<TopSoldProductResponse> topSoldProducts(int limit);

    AverageOrderAmountResponse averageOrderAmount(Instant from, Instant to);

    List<TopCustomerResponse> topCustomersByPurchasedAmount(int limit);
}
