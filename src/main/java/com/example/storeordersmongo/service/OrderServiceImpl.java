package com.example.storeordersmongo.service;

import com.example.storeordersmongo.dto.AverageOrderAmountResponse;
import com.example.storeordersmongo.dto.CreateOrderRequest;
import com.example.storeordersmongo.dto.DeliveryInfoRequest;
import com.example.storeordersmongo.dto.DeliveryInfoResponse;
import com.example.storeordersmongo.dto.OrderItemRequest;
import com.example.storeordersmongo.dto.OrderItemResponse;
import com.example.storeordersmongo.dto.OrderResponse;
import com.example.storeordersmongo.dto.OrderSearchCriteria;
import com.example.storeordersmongo.dto.OrdersByStatusResponse;
import com.example.storeordersmongo.dto.PaymentInfoRequest;
import com.example.storeordersmongo.dto.PaymentInfoResponse;
import com.example.storeordersmongo.dto.SalesByCategoryResponse;
import com.example.storeordersmongo.dto.SalesByDayResponse;
import com.example.storeordersmongo.dto.TopCustomerResponse;
import com.example.storeordersmongo.dto.TopSoldProductResponse;
import com.example.storeordersmongo.dto.UpdateOrderRequest;
import com.example.storeordersmongo.dto.UpdateOrderStatusRequest;
import com.example.storeordersmongo.exception.DuplicateOrderNumberException;
import com.example.storeordersmongo.exception.OrderNotFoundException;
import com.example.storeordersmongo.model.DeliveryInfo;
import com.example.storeordersmongo.model.Order;
import com.example.storeordersmongo.model.OrderItem;
import com.example.storeordersmongo.model.OrderStatus;
import com.example.storeordersmongo.model.PaymentInfo;
import com.example.storeordersmongo.repository.OrderRepository;
import com.example.storeordersmongo.repository.custom.OrderCustomRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderCustomRepository orderCustomRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderCustomRepository orderCustomRepository) {
        this.orderRepository = orderRepository;
        this.orderCustomRepository = orderCustomRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (orderRepository.existsByOrderNumber(request.orderNumber())) {
            throw new DuplicateOrderNumberException(request.orderNumber());
        }

        Instant now = Instant.now();
        Order order = new Order();
        applyOrderData(order, request.orderNumber(), request.customerId(), request.customerName(), request.status(),
                request.items(), request.delivery(), request.payment(), request.tags(), request.notes());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(String id) {
        return toResponse(getOrderEntity(id));
    }

    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        return toResponse(orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order with orderNumber '%s' not found".formatted(orderNumber))));
    }

    @Override
    public List<OrderResponse> getOrders(OrderStatus status, String customerId, Instant createdFrom, Instant createdTo, String tag) {
        List<Order> orders;

        if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else if (customerId != null && !customerId.isBlank()) {
            orders = orderRepository.findByCustomerId(customerId);
        } else if (createdFrom != null && createdTo != null) {
            orders = orderRepository.findByCreatedAtBetween(createdFrom, createdTo);
        } else if (tag != null && !tag.isBlank()) {
            orders = orderRepository.findByTagsContaining(tag);
        } else {
            orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse updateOrder(String id, UpdateOrderRequest request) {
        Order order = getOrderEntity(id);

        if (!order.getOrderNumber().equals(request.orderNumber())
                && orderRepository.existsByOrderNumber(request.orderNumber())) {
            throw new DuplicateOrderNumberException(request.orderNumber());
        }

        applyOrderData(order, request.orderNumber(), request.customerId(), request.customerName(), request.status(),
                request.items(), request.delivery(), request.payment(), request.tags(), request.notes());
        order.setUpdatedAt(Instant.now());

        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateOrderStatus(String id, UpdateOrderStatusRequest request) {
        Order order = getOrderEntity(id);
        order.setStatus(request.status());
        order.setUpdatedAt(Instant.now());
        return toResponse(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(String id) {
        Order order = getOrderEntity(id);
        orderRepository.delete(order);
    }

    @Override
    public List<OrderResponse> searchOrders(OrderSearchCriteria criteria) {
        return orderCustomRepository.search(criteria).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> findOrdersByItemCategory(String category) {
        return orderCustomRepository.findOrdersByItemCategory(category).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> findOrdersWithItemQuantityGreaterThan(int quantity) {
        return orderCustomRepository.findOrdersWithItemQuantityGreaterThan(quantity).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<OrdersByStatusResponse> countOrdersByStatus() {
        return orderCustomRepository.countOrdersByStatus();
    }

    @Override
    public List<SalesByDayResponse> totalSalesByDay(Instant from, Instant to) {
        return orderCustomRepository.totalSalesByDay(from, to);
    }

    @Override
    public List<SalesByCategoryResponse> totalSalesByCategory() {
        return orderCustomRepository.totalSalesByCategory();
    }

    @Override
    public List<TopSoldProductResponse> topSoldProducts(int limit) {
        return orderCustomRepository.topSoldProducts(limit);
    }

    @Override
    public AverageOrderAmountResponse averageOrderAmount(Instant from, Instant to) {
        return orderCustomRepository.averageOrderAmount(from, to);
    }

    @Override
    public List<TopCustomerResponse> topCustomersByPurchasedAmount(int limit) {
        return orderCustomRepository.topCustomersByPurchasedAmount(limit);
    }

    private Order getOrderEntity(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id '%s' not found".formatted(id)));
    }

    private void applyOrderData(Order order,
                                String orderNumber,
                                String customerId,
                                String customerName,
                                OrderStatus status,
                                List<OrderItemRequest> itemRequests,
                                DeliveryInfoRequest deliveryRequest,
                                PaymentInfoRequest paymentRequest,
                                List<String> tags,
                                String notes) {
        List<OrderItem> items = itemRequests.stream()
                .map(this::toOrderItem)
                .toList();

        order.setOrderNumber(orderNumber);
        order.setCustomerId(customerId);
        order.setCustomerName(customerName);
        order.setStatus(status);
        order.setItems(items);
        order.setDelivery(toDelivery(deliveryRequest));
        order.setPayment(toPayment(paymentRequest));
        order.setTags(tags == null ? Collections.emptyList() : tags);
        order.setNotes(notes);
        order.setTotalAmount(calculateTotalAmount(items));
    }

    private OrderItem toOrderItem(OrderItemRequest request) {
        BigDecimal subtotal = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        return new OrderItem(
                request.productId(),
                request.productName(),
                request.category(),
                request.quantity(),
                request.unitPrice(),
                subtotal
        );
    }

    private DeliveryInfo toDelivery(DeliveryInfoRequest request) {
        return new DeliveryInfo(
                request.recipientName(),
                request.phone(),
                request.addressLine(),
                request.city(),
                request.postalCode(),
                request.country(),
                request.deliveryInstructions()
        );
    }

    private PaymentInfo toPayment(PaymentInfoRequest request) {
        return new PaymentInfo(
                request.paid(),
                request.paymentMethod(),
                request.transactionReference(),
                request.paidAt()
        );
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getCategory(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal()
                        ))
                        .toList(),
                new DeliveryInfoResponse(
                        order.getDelivery().getRecipientName(),
                        order.getDelivery().getPhone(),
                        order.getDelivery().getAddressLine(),
                        order.getDelivery().getCity(),
                        order.getDelivery().getPostalCode(),
                        order.getDelivery().getCountry(),
                        order.getDelivery().getDeliveryInstructions()
                ),
                new PaymentInfoResponse(
                        order.getPayment().getPaid(),
                        order.getPayment().getPaymentMethod(),
                        order.getPayment().getTransactionReference(),
                        order.getPayment().getPaidAt()
                ),
                order.getTags(),
                order.getNotes()
        );
    }
}
