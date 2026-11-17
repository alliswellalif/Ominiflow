package com.omniflow.service;

import com.omniflow.dto.StockAdjustDto;
import com.omniflow.model.Product;
import com.omniflow.model.StockMovement;
import com.omniflow.repository.ProductRepository;
import com.omniflow.repository.StockMovementRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final AuditService auditService;

    public StockMovementService(StockMovementRepository stockMovementRepository, 
                                ProductRepository productRepository, 
                                AuditService auditService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.auditService = auditService;
    }

    @Transactional
    public StockMovement adjustStock(StockAdjustDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found ID: " + dto.getProductId()));

        int qtyChange = dto.getQuantity();
        String movementType = dto.getMovementType();

        // If reduction type, make quantity negative if positive
        if ("ADJUSTMENT_DAMAGE".equals(movementType) || "OUTBOUND_SALE".equals(movementType)) {
            if (qtyChange > 0) {
                qtyChange = -qtyChange;
            }
        }

        int currentStock = (product.getQuantityInStock() != null) ? product.getQuantityInStock() : 0;
        int newBalance = currentStock + qtyChange;

        if (newBalance < 0) {
            throw new IllegalStateException("Insufficient inventory. Current stock is " + currentStock + 
                    ", requested reduction is " + Math.abs(qtyChange));
        }

        product.setQuantityInStock(newBalance);
        productRepository.save(product);

        String username = "SYSTEM";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            username = auth.getName();
        }

        StockMovement movement = new StockMovement(
                product,
                movementType,
                qtyChange,
                newBalance,
                dto.getReferenceNumber(),
                dto.getReason(),
                username
        );
        StockMovement saved = stockMovementRepository.save(movement);

        auditService.log("STOCK_ADJUSTMENT", "Product", product.getId().toString(), 
                (newBalance <= product.getReorderThreshold() ? "WARNING" : "INFO"),
                "Stock adjusted (" + movementType + ") for " + product.getName() + " by " + qtyChange + ". Balance: " + newBalance);

        return saved;
    }

    public List<StockMovement> getRecentMovements() {
        return stockMovementRepository.findTop20ByOrderByTimestampDesc();
    }

    public List<StockMovement> getMovementsByProduct(Long productId) {
        return stockMovementRepository.findByProductIdOrderByTimestampDesc(productId);
    }
}

