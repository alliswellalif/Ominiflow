package com.omniflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 30)
    private String movementType; // INBOUND_PURCHASE, OUTBOUND_SALE, ADJUSTMENT_DAMAGE, ADJUSTMENT_AUDIT, RETURN_RESTOCK

    @Column(nullable = false)
    private Integer quantity; // positive for addition, negative for reduction

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column(length = 50)
    private String referenceNumber; // Order # or Receipt #

    @Column(length = 255)
    private String reason;

    @Column(length = 50)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public StockMovement() {
    }

    public StockMovement(Product product, String movementType, Integer quantity, Integer balanceAfter, 
                         String referenceNumber, String reason, String performedBy) {
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.balanceAfter = balanceAfter;
        this.referenceNumber = referenceNumber;
        this.reason = reason;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

