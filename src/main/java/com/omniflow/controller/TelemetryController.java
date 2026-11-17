package com.omniflow.controller;

import com.omniflow.dto.TelemetryDto;
import com.omniflow.service.TelemetryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping
    public String telemetry(Model model) {
        TelemetryDto telemetry = telemetryService.getTelemetry();
        model.addAttribute("telemetry", telemetry);
        return "telemetry/system-health";
    }
}

