package com.omniflow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantityInStock = 0;

    @Column(nullable = false)
    private Integer reorderThreshold = 10;

    @Column(nullable = false)
    private Integer safetyStock = 5;

    @Column(nullable = false)
    private Double dailyVelocity = 1.5; // Average items sold/used per day

    @Column(length = 30)
    private String unitOfMeasure = "PCS";

    @Column(length = 100)
    private String warehouseLocation = "Zone A-1";

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, DISCONTINUED, OUT_OF_STOCK

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return quantityInStock != null && reorderThreshold != null && quantityInStock <= reorderThreshold;
    }

    public boolean isOutOfStock() {
        return quantityInStock == null || quantityInStock <= 0;
    }

    public Double getDaysUntilStockout() {
        if (dailyVelocity == null || dailyVelocity <= 0) {
            return 999.0;
        }
        if (quantityInStock == null || quantityInStock <= 0) {
            return 0.0;
        }
        return Math.round((quantityInStock / dailyVelocity) * 10.0) / 10.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
    }

    public Double getDailyVelocity() {
        return dailyVelocity;
    }

    public void setDailyVelocity(Double dailyVelocity) {
        this.dailyVelocity = dailyVelocity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

