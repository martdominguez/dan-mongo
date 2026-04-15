package com.example.storeordersmongo.repository;

import com.example.storeordersmongo.model.Order;
import com.example.storeordersmongo.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerId(String customerId);

    List<Order> findByCreatedAtBetween(Instant from, Instant to);

    List<Order> findByTagsContaining(String tag);

    boolean existsByOrderNumber(String orderNumber);
}
