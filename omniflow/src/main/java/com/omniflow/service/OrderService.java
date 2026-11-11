package com.omniflow.service;

import com.omniflow.dto.OrderItemDto;
import com.omniflow.dto.OrderRequestDto;
import com.omniflow.model.*;
import com.omniflow.repository.OrderItemRepository;
import com.omniflow.repository.OrderRepository;
import com.omniflow.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final VerificationService verificationService;
    private final StockMovementService stockMovementService;
    private final AuditService auditService;
    private final SystemSettingService settingService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        VerificationService verificationService,
                        StockMovementService stockMovementService,
                        AuditService auditService,
                        SystemSettingService settingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.verificationService = verificationService;
        this.stockMovementService = stockMovementService;
        this.auditService = auditService;
        this.settingService = settingService;
    }

    @PostConstruct
    public void seedInitialOrders() {
        if (orderRepository.count() == 0 && productRepository.count() > 0) {
            List<Product> products = productRepository.findAll();
            if (products.size() >= 2) {
                createSeedOrder("AeroSpace Defense Hub", "procurement@aerospacedefense.com", "+1-202-555-0199",
                        "Building 4, Defense Corridor, Arlington VA", OrderStatus.COMPLETED, products.get(0), 4);

                createSeedOrder("MetroHealth Regional Medical Center", "supply@metrohealth.org", "+1-617-555-0144",
                        "Central Receiving, 500 Medical Way, Boston MA", OrderStatus.DISPATCHED, products.get(2), 2);

                createSeedOrder("RoboLogix Automation Corp", "parts@robologix.net", "+1-408-555-0112",
                        "Dock 2, Innovation Way, San Jose CA", OrderStatus.PENDING, products.get(1), 5);
            }
        }
    }

    private void createSeedOrder(String customer, String email, String phone, String address, 
                                 OrderStatus status, Product p, int qty) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(customer);
        order.setCustomerEmail(email);
        order.setCustomerPhone(phone);
        order.setShippingAddress(address);
        order.setOrderDate(LocalDateTime.now().minusDays(new Random().nextInt(5)));
        order.setStatus(status);
        order.setCreatedBy("admin");

        OrderItem item = new OrderItem(order, p, qty, p.getSellingPrice());
        order.addItem(item);

        double taxPct = Double.parseDouble(settingService.getSetting("DEFAULT_TAX_PERCENT", "7.5"));
        BigDecimal subtotal = item.getTotalPrice();
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(taxPct / 100.0));
        order.setSubtotal(subtotal);
        order.setTaxAmount(tax);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(tax));

        String hash = verificationService.calculateVerificationHash(order);
        order.setVerificationHash(hash);
        String qr = verificationService.generateQrCodeBase64("/verify/" + hash, 200, 200);
        order.setQrCodeBase64(qr);

        orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(OrderRequestDto dto) {
        String username = "SYSTEM";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            username = auth.getName();
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());
        order.setCustomerPhone(dto.getCustomerPhone());
        order.setShippingAddress(dto.getShippingAddress());
        order.setNotes(dto.getNotes());
        order.setCreatedBy(username);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemDto itemDto : dto.getItems()) {
            if (itemDto.getProductId() == null || itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                continue;
            }
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + itemDto.getProductId()));

            BigDecimal price = (itemDto.getUnitPrice() != null) ? itemDto.getUnitPrice() : product.getSellingPrice();
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), price);
            order.addItem(item);
            subtotal = subtotal.add(item.getTotalPrice());
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one valid item.");
        }

        order.setSubtotal(subtotal);
        order.setTaxAmount(dto.getTaxAmount() != null ? dto.getTaxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO);
        order.recalculateTotals();

        // Calculate cryptographic verification hash & QR code
        String hash = verificationService.calculateVerificationHash(order);
        order.setVerificationHash(hash);
        String qrCode = verificationService.generateQrCodeBase64("/verify/" + hash, 200, 200);
        order.setQrCodeBase64(qrCode);

        Order saved = orderRepository.save(order);
        auditService.log("CREATE_ORDER", "Order", saved.getId().toString(), "INFO",
                "New order created: " + saved.getOrderNumber() + " for " + saved.getCustomerName() + " (Total: " + saved.getTotalAmount() + ")");

        return saved;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        if (oldStatus == newStatus) {
            return order;
        }

        // Automated inventory stock deduction when transitioning to DISPATCHED
        if (newStatus == OrderStatus.DISPATCHED && oldStatus != OrderStatus.DISPATCHED && oldStatus != OrderStatus.COMPLETED) {
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                int currentStock = (p.getQuantityInStock() != null) ? p.getQuantityInStock() : 0;
                int reqQty = item.getQuantity();

                if (currentStock < reqQty) {
                    throw new IllegalStateException("Cannot dispatch order " + order.getOrderNumber() + 
                            ": Insufficient stock for product '" + p.getName() + "'. Available: " + currentStock + ", Required: " + reqQty);
                }

                p.setQuantityInStock(currentStock - reqQty);
                productRepository.save(p);

                // Record stock movement
                com.omniflow.dto.StockAdjustDto adjustDto = new com.omniflow.dto.StockAdjustDto();
                adjustDto.setProductId(p.getId());
                adjustDto.setMovementType("OUTBOUND_SALE");
                adjustDto.setQuantity(reqQty);
                adjustDto.setReferenceNumber(order.getOrderNumber());
                adjustDto.setReason("Dispatched for order " + order.getOrderNumber());
                stockMovementService.adjustStock(adjustDto);
            }
        }

        order.setStatus(newStatus);
        
        // Refresh hash on status update
        String hash = verificationService.calculateVerificationHash(order);
        order.setVerificationHash(hash);
        order.setQrCodeBase64(verificationService.generateQrCodeBase64("/verify/" + hash, 200, 200));

        Order saved = orderRepository.save(order);
        auditService.log("ORDER_STATUS_CHANGE", "Order", order.getId().toString(), "INFO",
                "Order " + order.getOrderNumber() + " status transitioned from " + oldStatus + " to " + newStatus);

        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public List<Order> getRecentOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    public List<Order> searchOrders(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllOrders();
        }
        return orderRepository.searchOrders(keyword.trim());
    }

    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateTotalGrossRevenue();
    }

    private synchronized String generateOrderNumber() {
        String ym = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%04d", ym, count);
    }
}

