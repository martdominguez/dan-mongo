package com.example.storeordersmongo.repository.impl;

import com.example.storeordersmongo.dto.AverageOrderAmountResponse;
import com.example.storeordersmongo.dto.OrderSearchCriteria;
import com.example.storeordersmongo.dto.OrdersByStatusResponse;
import com.example.storeordersmongo.dto.SalesByCategoryResponse;
import com.example.storeordersmongo.dto.SalesByDayResponse;
import com.example.storeordersmongo.dto.TopCustomerResponse;
import com.example.storeordersmongo.dto.TopSoldProductResponse;
import com.example.storeordersmongo.model.Order;
import com.example.storeordersmongo.repository.custom.OrderCustomRepository;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private static final String COLLECTION = "orders";

    private final MongoTemplate mongoTemplate;

    public OrderCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Order> search(OrderSearchCriteria criteria) {
        List<Criteria> filters = new ArrayList<>();

        if (criteria.status() != null) {
            filters.add(Criteria.where("status").is(criteria.status()));
        }
        if (criteria.city() != null && !criteria.city().isBlank()) {
            filters.add(Criteria.where("delivery.city").is(criteria.city()));
        }
        if (criteria.paid() != null) {
            filters.add(Criteria.where("payment.paid").is(criteria.paid()));
        }
        if (criteria.createdFrom() != null) {
            filters.add(Criteria.where("createdAt").gte(criteria.createdFrom()));
        }
        if (criteria.createdTo() != null) {
            filters.add(Criteria.where("createdAt").lte(criteria.createdTo()));
        }
        if (criteria.minimumAmount() != null) {
            filters.add(Criteria.where("totalAmount").gte(criteria.minimumAmount()));
        }

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(Criteria[]::new)));
        }

        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public List<Order> findOrdersByItemCategory(String category) {
        Query query = new Query()
                .addCriteria(Criteria.where("items").elemMatch(Criteria.where("category").is(category)))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public List<Order> findOrdersWithItemQuantityGreaterThan(int quantity) {
        Query query = new Query()
                .addCriteria(Criteria.where("items").elemMatch(Criteria.where("quantity").gt(quantity)))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Order.class);
    }

    @Override
    public List<OrdersByStatusResponse> countOrdersByStatus() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("status").count().as("count"),
                Aggregation.project("count").and("_id").as("status"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "status"))
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION, Document.class)
                .getMappedResults()
                .stream()
                .map(document -> new OrdersByStatusResponse(
                        document.getString("status"),
                        getLong(document, "count")
                ))
                .toList();
    }

    @Override
    public List<SalesByDayResponse> totalSalesByDay(Instant from, Instant to) {
        AggregationOperationsBuilder builder = new AggregationOperationsBuilder();
        if (from != null || to != null) {
            Criteria criteria = Criteria.where("createdAt");
            if (from != null) {
                criteria = criteria.gte(from);
            }
            if (to != null) {
                criteria = criteria.lte(to);
            }
            builder.add(Aggregation.match(criteria));
        }

        builder.add(Aggregation.project("totalAmount")
                .andExpression("dateToString('%Y-%m-%d', createdAt)").as("day"));
        builder.add(Aggregation.group("day")
                .sum("totalAmount").as("totalSales")
                .count().as("orderCount"));
        builder.add(Aggregation.project("totalSales", "orderCount").and("_id").as("day"));
        builder.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, "day")));

        Aggregation aggregation = Aggregation.newAggregation(builder.toArray());
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, COLLECTION, Document.class);

        return results.getMappedResults().stream()
                .map(document -> new SalesByDayResponse(
                        document.getString("day"),
                        getBigDecimal(document, "totalSales"),
                        getLong(document, "orderCount")
                ))
                .toList();
    }

    @Override
    public List<SalesByCategoryResponse> totalSalesByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("items"),
                Aggregation.group("items.category")
                        .sum("items.subtotal").as("totalSales")
                        .sum("items.quantity").as("unitsSold"),
                Aggregation.project("totalSales", "unitsSold").and("_id").as("category"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalSales"))
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION, Document.class)
                .getMappedResults()
                .stream()
                .map(document -> new SalesByCategoryResponse(
                        document.getString("category"),
                        getBigDecimal(document, "totalSales"),
                        getLong(document, "unitsSold")
                ))
                .toList();
    }

    @Override
    public List<TopSoldProductResponse> topSoldProducts(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("items"),
                Aggregation.group("items.productId", "items.productName", "items.category")
                        .sum("items.quantity").as("unitsSold")
                        .sum("items.subtotal").as("totalSales"),
                Aggregation.project("unitsSold", "totalSales")
                        .and("_id.productId").as("productId")
                        .and("_id.productName").as("productName")
                        .and("_id.category").as("category"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "unitsSold")
                        .and(Sort.by(Sort.Direction.DESC, "totalSales"))),
                Aggregation.limit(limit)
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION, Document.class)
                .getMappedResults()
                .stream()
                .map(document -> new TopSoldProductResponse(
                        document.getString("productId"),
                        document.getString("productName"),
                        document.getString("category"),
                        getLong(document, "unitsSold"),
                        getBigDecimal(document, "totalSales")
                ))
                .toList();
    }

    @Override
    public AverageOrderAmountResponse averageOrderAmount(Instant from, Instant to) {
        AggregationOperationsBuilder builder = new AggregationOperationsBuilder();
        if (from != null || to != null) {
            Criteria criteria = Criteria.where("createdAt");
            if (from != null) {
                criteria = criteria.gte(from);
            }
            if (to != null) {
                criteria = criteria.lte(to);
            }
            builder.add(Aggregation.match(criteria));
        }

        builder.add(Aggregation.group()
                .avg("totalAmount").as("averageAmount")
                .count().as("orderCount"));

        Aggregation aggregation = Aggregation.newAggregation(builder.toArray());
        Document document = mongoTemplate.aggregate(aggregation, COLLECTION, Document.class).getUniqueMappedResult();

        if (document == null) {
            return new AverageOrderAmountResponse(from, to, BigDecimal.ZERO, 0L);
        }

        return new AverageOrderAmountResponse(
                from,
                to,
                getBigDecimal(document, "averageAmount"),
                getLong(document, "orderCount")
        );
    }

    @Override
    public List<TopCustomerResponse> topCustomersByPurchasedAmount(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("customerId", "customerName")
                        .sum("totalAmount").as("totalPurchasedAmount")
                        .count().as("orderCount"),
                Aggregation.project("totalPurchasedAmount", "orderCount")
                        .and("_id.customerId").as("customerId")
                        .and("_id.customerName").as("customerName"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalPurchasedAmount")),
                Aggregation.limit(limit)
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION, Document.class)
                .getMappedResults()
                .stream()
                .map(document -> new TopCustomerResponse(
                        document.getString("customerId"),
                        document.getString("customerName"),
                        getBigDecimal(document, "totalPurchasedAmount"),
                        getLong(document, "orderCount")
                ))
                .toList();
    }

    private static long getLong(Document document, String key) {
        Object value = document.get(key);
        return value == null ? 0L : ((Number) value).longValue();
    }

    private static BigDecimal getBigDecimal(Document document, String key) {
        Object value = document.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof org.bson.types.Decimal128 decimal128) {
            return decimal128.bigDecimalValue();
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private static final class AggregationOperationsBuilder {
        private final List<AggregationOperation> operations = new ArrayList<>();

        void add(AggregationOperation operation) {
            operations.add(operation);
        }

        AggregationOperation[] toArray() {
            return operations.toArray(AggregationOperation[]::new);
        }
    }
}
