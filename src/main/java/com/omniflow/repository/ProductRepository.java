package com.omniflow.repository;

import com.omniflow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.quantityInStock <= p.reorderThreshold ORDER BY p.quantityInStock ASC")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.quantityInStock <= 0")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantityInStock <= p.reorderThreshold")
    long countLowStockProducts();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(p.warehouseLocation) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Product> searchProducts(@Param("kw") String keyword);

    @Query("SELECT COALESCE(SUM(p.quantityInStock * p.unitCost), 0) FROM Product p")
    Double calculateTotalInventoryValue();
}

