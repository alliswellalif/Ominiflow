package com.omniflow.controller;

import com.omniflow.model.Order;
import com.omniflow.model.Product;
import com.omniflow.service.OrderService;
import com.omniflow.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {

    private final ProductService productService;
    private final OrderService orderService;

    public ExportController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/products.csv")
    public void exportProductsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"omniflow_products_catalog.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID,SKU,Name,Category,Stock,Threshold,UnitCost,SellingPrice,Location,Status");

        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%d,%d,%s,%s,\"%s\",\"%s\"%n",
                    p.getId(),
                    escapeCsv(p.getSku()),
                    escapeCsv(p.getName()),
                    escapeCsv(p.getCategory() != null ? p.getCategory().getName() : "None"),
                    p.getQuantityInStock(),
                    p.getReorderThreshold(),
                    p.getUnitCost(),
                    p.getSellingPrice(),
                    escapeCsv(p.getWarehouseLocation()),
                    p.getStatus()
            );
        }
        writer.flush();
    }

    @GetMapping("/orders.csv")
    public void exportOrdersCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"omniflow_orders_ledger.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("OrderNumber,CustomerName,Email,Date,Status,TotalAmount,VerificationHash");

        List<Order> orders = orderService.getAllOrders();
        for (Order o : orders) {
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\"%n",
                    escapeCsv(o.getOrderNumber()),
                    escapeCsv(o.getCustomerName()),
                    escapeCsv(o.getCustomerEmail()),
                    o.getOrderDate(),
                    o.getStatus(),
                    o.getTotalAmount(),
                    o.getVerificationHash()
            );
        }
        writer.flush();
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        return val.replace("\"", "\"\"");
    }
}

