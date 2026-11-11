package com.omniflow.controller;

import com.omniflow.service.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/audit")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public String viewLogs(@RequestParam(value = "severity", required = false) String severity, Model model) {
        if (severity != null && !severity.trim().isEmpty()) {
            model.addAttribute("logs", auditService.getLogsBySeverity(severity.trim().toUpperCase()));
            model.addAttribute("selectedSeverity", severity.trim().toUpperCase());
        } else {
            model.addAttribute("logs", auditService.getRecentLogs());
        }
        model.addAttribute("totalLogsCount", auditService.getTotalLogsCount());
        return "audit/logs";
    }
}

