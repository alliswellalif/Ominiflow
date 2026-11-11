package com.omniflow.controller;

import com.omniflow.dto.VerificationResultDto;
import com.omniflow.service.VerificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicVerificationController {

    private final VerificationService verificationService;

    public PublicVerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify")
    public String verifyForm(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            VerificationResultDto result = verificationService.verify(query);
            model.addAttribute("result", result);
            model.addAttribute("query", query);
        }
        return "verify/public-verify";
    }

    @GetMapping("/verify/{hash}")
    public String verifyDirect(@PathVariable("hash") String hash, Model model) {
        VerificationResultDto result = verificationService.verify(hash);
        model.addAttribute("result", result);
        model.addAttribute("query", hash);
        return "verify/public-verify";
    }
}

