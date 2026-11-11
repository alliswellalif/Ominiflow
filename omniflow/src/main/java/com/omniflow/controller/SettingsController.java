package com.omniflow.controller;

import com.omniflow.service.AuditService;
import com.omniflow.service.SystemSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final SystemSettingService settingService;
    private final AuditService auditService;

    public SettingsController(SystemSettingService settingService, AuditService auditService) {
        this.settingService = settingService;
        this.auditService = auditService;
    }

    @GetMapping
    public String viewSettings(Model model) {
        model.addAttribute("settings", settingService.getAllSettings());
        return "settings/index";
    }

    @PostMapping("/save")
    public String saveSettings(@RequestParam Map<String, String> allParams, RedirectAttributes redirectAttributes) {
        try {
            allParams.remove("_csrf");
            settingService.updateAll(allParams);
            auditService.log("SETTINGS_UPDATE", "SystemSetting", "All", "WARNING", "System branding and global settings updated by administrator.");
            redirectAttributes.addFlashAttribute("successMessage", "System settings and visual branding updated successfully! All pages are refreshed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating settings: " + e.getMessage());
        }
        return "redirect:/settings";
    }
}

