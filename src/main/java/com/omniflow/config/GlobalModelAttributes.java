package com.omniflow.config;

import com.omniflow.model.User;
import com.omniflow.service.SystemSettingService;
import com.omniflow.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalModelAttributes {

    private final SystemSettingService settingService;
    private final UserService userService;

    public GlobalModelAttributes(SystemSettingService settingService, UserService userService) {
        this.settingService = settingService;
        this.userService = userService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("appName", settingService.getSetting("APP_NAME", "OmniFlow Logistics ERP"));
        model.addAttribute("appTagline", settingService.getSetting("APP_TAGLINE", "Intelligent Supply Chain & Verifiable Inventory"));
        model.addAttribute("appLogoUrl", settingService.getSetting("APP_LOGO_URL", ""));
        model.addAttribute("primaryTheme", settingService.getSetting("PRIMARY_THEME", "theme-indigo"));
        model.addAttribute("currencySymbol", settingService.getSetting("CURRENCY_SYMBOL", "$"));
        model.addAttribute("companyName", settingService.getSetting("COMPANY_NAME", "OmniFlow Global Solutions Ltd."));
        model.addAttribute("companyAddress", settingService.getSetting("COMPANY_ADDRESS", "742 Evergreen Terrace, Logistics Hub, Suite 400"));
        model.addAttribute("companyEmail", settingService.getSetting("COMPANY_EMAIL", "support@omniflow.io"));
        model.addAttribute("companyPhone", settingService.getSetting("COMPANY_PHONE", "+1 (800) 555-0199"));
        model.addAttribute("maintenanceMode", settingService.getSetting("MAINTENANCE_MODE", "false"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("currentUsername", auth.getName());
            Optional<User> userOpt = userService.findByUsername(auth.getName());
            userOpt.ifPresent(user -> {
                model.addAttribute("currentUserFullName", user.getFullName());
                model.addAttribute("currentUserRole", user.getRole().name());
                model.addAttribute("currentUserEmail", user.getEmail());
            });
        }
    }
}

