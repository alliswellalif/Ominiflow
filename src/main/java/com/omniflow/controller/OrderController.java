package com.omniflow.controller;

import com.omniflow.dto.OrderRequestDto;
import com.omniflow.model.Order;
import com.omniflow.model.OrderStatus;
import com.omniflow.service.OrderService;
import com.omniflow.service.ProductService;
import com.omniflow.service.SystemSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final SystemSettingService settingService;

    public OrderController(OrderService orderService, 
                           ProductService productService, 
                           SystemSettingService settingService) {
        this.orderService = orderService;
        this.productService = productService;
        this.settingService = settingService;
    }

    @GetMapping
    public String listOrders(@RequestParam(value = "search", required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("orders", orderService.searchOrders(search));
            model.addAttribute("searchQuery", search);
        } else {
            model.addAttribute("orders", orderService.getAllOrders());
        }
        return "orders/list";
    }

    @GetMapping("/new")
    public String newOrderForm(Model model) {
        OrderRequestDto dto = new OrderRequestDto();
        double defaultTax = Double.parseDouble(settingService.getSetting("DEFAULT_TAX_PERCENT", "7.5"));
        dto.setTaxAmount(BigDecimal.valueOf(defaultTax));

        model.addAttribute("orderRequest", dto);
        model.addAttribute("products", productService.getAllProducts());
        return "orders/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute("orderRequest") OrderRequestDto dto, RedirectAttributes redirectAttributes) {
        try {
            Order created = orderService.createOrder(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Order " + created.getOrderNumber() + " created successfully with cryptographic QR seal!");
            return "redirect:/orders/view/" + created.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create order: " + e.getMessage());
            return "redirect:/orders/new";
        }
    }

    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "orders/view";
    }

    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));
        model.addAttribute("order", order);
        return "orders/invoice";
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") Long id, 
                               @RequestParam("status") OrderStatus newStatus, 
                               RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order status successfully transitioned to " + newStatus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Status transition failed: " + e.getMessage());
        }
        return "redirect:/orders/view/" + id;
    }
}

