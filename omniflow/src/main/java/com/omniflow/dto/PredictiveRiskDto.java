package com.omniflow.dto;

import java.math.BigDecimal;

public class PredictiveRiskDto {
    private Long productId;
    private String sku;
    private String productName;
    private String categoryName;
    private Integer currentStock;
    private Integer reorderThreshold;
    private Double dailyVelocity;
    private Double daysRemaining;
    private String riskLevel; // CRITICAL, WARNING, OPTIMAL, OVERSTOCK
    private String riskBadgeClass; // badge-danger, badge-warning, badge-success, badge-info
    private Integer recommendedOrderQuantity; // EOQ based
    private Integer supplierLeadTimeDays;
    private Integer urgencyScore; // 0 to 100
    private BigDecimal estimatedReorderCost;

    public PredictiveRiskDto() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Double getDailyVelocity() {
        return dailyVelocity;
    }

    public void setDailyVelocity(Double dailyVelocity) {
        this.dailyVelocity = dailyVelocity;
    }

    public Double getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Double daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskBadgeClass() {
        return riskBadgeClass;
    }

    public void setRiskBadgeClass(String riskBadgeClass) {
        this.riskBadgeClass = riskBadgeClass;
    }

    public Integer getRecommendedOrderQuantity() {
        return recommendedOrderQuantity;
    }

    public void setRecommendedOrderQuantity(Integer recommendedOrderQuantity) {
        this.recommendedOrderQuantity = recommendedOrderQuantity;
    }

    public Integer getSupplierLeadTimeDays() {
        return supplierLeadTimeDays;
    }

    public void setSupplierLeadTimeDays(Integer supplierLeadTimeDays) {
        this.supplierLeadTimeDays = supplierLeadTimeDays;
    }

    public Integer getUrgencyScore() {
        return urgencyScore;
    }

    public void setUrgencyScore(Integer urgencyScore) {
        this.urgencyScore = urgencyScore;
    }

    public BigDecimal getEstimatedReorderCost() {
        return estimatedReorderCost;
    }

    public void setEstimatedReorderCost(BigDecimal estimatedReorderCost) {
        this.estimatedReorderCost = estimatedReorderCost;
    }
}

