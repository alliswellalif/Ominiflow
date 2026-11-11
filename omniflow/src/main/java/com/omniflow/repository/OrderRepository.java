package com.omniflow.repository;

import com.omniflow.model.Order;
import com.omniflow.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByVerificationHash(String verificationHash);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findTop10ByOrderByOrderDateDesc();

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal calculateTotalGrossRevenue();

    @Query("SELECT o FROM Order o WHERE LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :kw, '%')) ORDER BY o.orderDate DESC")
    List<Order> searchOrders(@Param("kw") String keyword);
}

