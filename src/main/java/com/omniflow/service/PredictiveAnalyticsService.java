package com.omniflow.service;

import com.omniflow.dto.PredictiveRiskDto;
import com.omniflow.model.Product;
import com.omniflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PredictiveAnalyticsService {

    private final ProductRepository productRepository;

    public PredictiveAnalyticsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Evaluates inventory across all products and generates predictive risk metrics and EOQ reorder recommendations.
     */
    public List<PredictiveRiskDto> generatePredictiveRiskMatrix() {
        List<Product> products = productRepository.findAll();
        List<PredictiveRiskDto> matrix = new ArrayList<>();

        for (Product p : products) {
            PredictiveRiskDto dto = new PredictiveRiskDto();
            dto.setProductId(p.getId());
            dto.setSku(p.getSku());
            dto.setProductName(p.getName());
            dto.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : "Uncategorized");
            dto.setCurrentStock(p.getQuantityInStock());
            dto.setReorderThreshold(p.getReorderThreshold());

            double velocity = (p.getDailyVelocity() != null && p.getDailyVelocity() > 0) ? p.getDailyVelocity() : 1.0;
            dto.setDailyVelocity(Math.round(velocity * 100.0) / 100.0);

            int currentStock = (p.getQuantityInStock() != null) ? p.getQuantityInStock() : 0;
            int threshold = (p.getReorderThreshold() != null) ? p.getReorderThreshold() : 10;
            int leadTime = (p.getSupplier() != null && p.getSupplier().getLeadTimeDays() != null) 
                    ? p.getSupplier().getLeadTimeDays() : 7;
            dto.setSupplierLeadTimeDays(leadTime);

            double daysRemaining = currentStock / velocity;
            dto.setDaysRemaining(Math.round(daysRemaining * 10.0) / 10.0);

            // Economic Order Quantity (EOQ) calculation
            // EOQ = sqrt((2 * D * S) / H)
            // D = Annual Demand (velocity * 365)
            // S = Fixed Order Cost (~$50)
            // H = Holding Cost per year (20% of unit cost, min $1.00)
            double annualDemand = velocity * 365.0;
            double fixedOrderCost = 50.0;
            double unitCost = p.getUnitCost() != null ? p.getUnitCost().doubleValue() : 10.0;
            double holdingCost = Math.max(1.0, unitCost * 0.20);

            int eoq = (int) Math.round(Math.sqrt((2.0 * annualDemand * fixedOrderCost) / holdingCost));
            if (eoq < 10) eoq = 10;
            dto.setRecommendedOrderQuantity(eoq);

            BigDecimal estimatedCost = BigDecimal.valueOf(unitCost).multiply(BigDecimal.valueOf(eoq));
            dto.setEstimatedReorderCost(estimatedCost);

            // Classify risk level & urgency score
            if (currentStock <= 0) {
                dto.setRiskLevel("CRITICAL - OUT OF STOCK");
                dto.setRiskBadgeClass("badge-danger");
                dto.setUrgencyScore(100);
            } else if (daysRemaining <= 3.0 || currentStock <= p.getSafetyStock()) {
                dto.setRiskLevel("CRITICAL - STOCKOUT IMMINENT");
                dto.setRiskBadgeClass("badge-danger");
                dto.setUrgencyScore(90);
            } else if (daysRemaining <= (leadTime + 2.0) || currentStock <= threshold) {
                dto.setRiskLevel("WARNING - REORDER WINDOW");
                dto.setRiskBadgeClass("badge-warning");
                dto.setUrgencyScore(65);
            } else if (daysRemaining > 90.0) {
                dto.setRiskLevel("SURPLUS - OVERSTOCKED");
                dto.setRiskBadgeClass("badge-info");
                dto.setUrgencyScore(15);
            } else {
                dto.setRiskLevel("HEALTHY INVENTORY");
                dto.setRiskBadgeClass("badge-success");
                dto.setUrgencyScore(25);
            }

            matrix.add(dto);
        }

        // Sort by highest urgency score first
        matrix.sort(Comparator.comparingInt(PredictiveRiskDto::getUrgencyScore).reversed());
        return matrix;
    }
}

