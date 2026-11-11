package com.omniflow.controller;

import com.omniflow.dto.PredictiveRiskDto;
import com.omniflow.model.OrderStatus;
import com.omniflow.repository.ProductRepository;
import com.omniflow.service.OrderService;
import com.omniflow.service.PredictiveAnalyticsService;
import com.omniflow.service.ProductService;
import com.omniflow.service.StockMovementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final PredictiveAnalyticsService predictiveService;
    private final StockMovementService stockMovementService;

    public DashboardController(ProductService productService, 
                               ProductRepository productRepository, 
                               OrderService orderService, 
                               PredictiveAnalyticsService predictiveService, 
                               StockMovementService stockMovementService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.predictiveService = predictiveService;
        this.stockMovementService = stockMovementService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalProducts = productService.getAllProducts().size();
        long lowStockCount = productService.getLowStockCount();
        long pendingOrders = orderService.countByStatus(OrderStatus.PENDING);
        long completedOrders = orderService.countByStatus(OrderStatus.COMPLETED);
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        Double inventoryVal = productRepository.calculateTotalInventoryValue();
        BigDecimal totalInventoryValue = BigDecimal.valueOf(inventoryVal != null ? inventoryVal : 0.0);

        List<PredictiveRiskDto> riskMatrix = predictiveService.generatePredictiveRiskMatrix();
        long criticalStockoutCount = riskMatrix.stream()
                .filter(r -> r.getUrgencyScore() >= 90)
                .count();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("criticalStockoutCount", criticalStockoutCount);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalInventoryValue", totalInventoryValue);

        model.addAttribute("recentOrders", orderService.getRecentOrders());
        model.addAttribute("recentMovements", stockMovementService.getRecentMovements());
        model.addAttribute("topRisks", riskMatrix.stream().limit(5).toList());

        return "dashboard/index";
    }
}

