package com.omniflow.controller;

import com.omniflow.model.User;
import com.omniflow.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + auth.getName()));
        model.addAttribute("user", user);
        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(Authentication auth,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("email") String email,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(auth.getName(), fullName, email, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Profile and credentials updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Profile update failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}

