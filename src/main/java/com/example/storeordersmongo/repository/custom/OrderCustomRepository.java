package com.example.storeordersmongo.repository.custom;

import com.example.storeordersmongo.dto.AverageOrderAmountResponse;
import com.example.storeordersmongo.dto.OrderSearchCriteria;
import com.example.storeordersmongo.dto.OrdersByStatusResponse;
import com.example.storeordersmongo.dto.SalesByCategoryResponse;
import com.example.storeordersmongo.dto.SalesByDayResponse;
import com.example.storeordersmongo.dto.TopCustomerResponse;
import com.example.storeordersmongo.dto.TopSoldProductResponse;
import com.example.storeordersmongo.model.Order;

import java.time.Instant;
import java.util.List;

public interface OrderCustomRepository {

    List<Order> search(OrderSearchCriteria criteria);

    List<Order> findOrdersByItemCategory(String category);

    List<Order> findOrdersWithItemQuantityGreaterThan(int quantity);

    List<OrdersByStatusResponse> countOrdersByStatus();

    List<SalesByDayResponse> totalSalesByDay(Instant from, Instant to);

    List<SalesByCategoryResponse> totalSalesByCategory();

    List<TopSoldProductResponse> topSoldProducts(int limit);

    AverageOrderAmountResponse averageOrderAmount(Instant from, Instant to);

    List<TopCustomerResponse> topCustomersByPurchasedAmount(int limit);
}
