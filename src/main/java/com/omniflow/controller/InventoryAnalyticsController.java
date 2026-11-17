package com.omniflow.controller;

import com.omniflow.dto.PredictiveRiskDto;
import com.omniflow.service.PredictiveAnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/analytics")
public class InventoryAnalyticsController {

    private final PredictiveAnalyticsService predictiveService;

    public InventoryAnalyticsController(PredictiveAnalyticsService predictiveService) {
        this.predictiveService = predictiveService;
    }

    @GetMapping("/predictive")
    public String predictiveMatrix(Model model) {
        List<PredictiveRiskDto> matrix = predictiveService.generatePredictiveRiskMatrix();
        
        long criticalCount = matrix.stream().filter(m -> m.getUrgencyScore() >= 90).count();
        long warningCount = matrix.stream().filter(m -> m.getUrgencyScore() >= 60 && m.getUrgencyScore() < 90).count();
        long healthyCount = matrix.stream().filter(m -> m.getUrgencyScore() < 60).count();

        model.addAttribute("matrix", matrix);
        model.addAttribute("criticalCount", criticalCount);
        model.addAttribute("warningCount", warningCount);
        model.addAttribute("healthyCount", healthyCount);

        return "analytics/predictive-matrix";
    }
}

