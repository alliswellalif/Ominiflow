package com.omniflow.controller;

import com.omniflow.dto.StockAdjustDto;
import com.omniflow.model.Product;
import com.omniflow.service.ProductService;
import com.omniflow.service.StockMovementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final StockMovementService stockMovementService;

    public ProductController(ProductService productService, StockMovementService stockMovementService) {
        this.productService = productService;
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public String listProducts(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "lowStock", required = false) Boolean lowStock,
                               Model model) {
        if (Boolean.TRUE.equals(lowStock)) {
            model.addAttribute("products", productService.getLowStockProducts());
            model.addAttribute("filterTitle", "Low Stock & Reorder Alert Items");
        } else if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("products", productService.searchProducts(search));
            model.addAttribute("searchQuery", search);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        return "products/list";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("suppliers", productService.getAllSuppliers());
        model.addAttribute("isNew", true);
        return "products/form";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product p = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        model.addAttribute("product", p);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("suppliers", productService.getAllSuppliers());
        model.addAttribute("isNew", false);
        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Product SKU " + product.getSku() + " successfully saved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/stock-adjust")
    public String stockAdjustForm(@RequestParam(value = "productId", required = false) Long productId, Model model) {
        StockAdjustDto dto = new StockAdjustDto();
        if (productId != null) {
            dto.setProductId(productId);
        }
        model.addAttribute("adjustDto", dto);
        model.addAttribute("products", productService.getAllProducts());
        return "products/stock-adjust";
    }

    @PostMapping("/stock-adjust")
    public String processStockAdjust(@ModelAttribute("adjustDto") StockAdjustDto dto, RedirectAttributes redirectAttributes) {
        try {
            stockMovementService.adjustStock(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Stock successfully adjusted and logged into audit trail.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Adjustment failed: " + e.getMessage());
        }
        return "redirect:/products";
    }
}

