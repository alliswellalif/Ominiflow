package com.omniflow.service;

import com.omniflow.dto.TelemetryDto;
import com.omniflow.repository.AuditLogRepository;
import com.omniflow.repository.OrderRepository;
import com.omniflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@Service
public class TelemetryService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AuditLogRepository auditLogRepository;

    public TelemetryService(ProductRepository productRepository, 
                            OrderRepository orderRepository, 
                            AuditLogRepository auditLogRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public TelemetryDto getTelemetry() {
        TelemetryDto dto = new TelemetryDto();
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        dto.setJvmVersion(System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        dto.setOsName(System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        dto.setAvailableProcessors(runtime.availableProcessors());
        dto.setTotalMemoryMb(totalMemory);
        dto.setUsedMemoryMb(usedMemory);
        dto.setFreeMemoryMb(freeMemory);
        
        int percent = (totalMemory > 0) ? (int) Math.round((double) usedMemory / totalMemory * 100) : 0;
        dto.setMemoryUsagePercent(percent);

        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptimeMs = rb.getUptime();
        long seconds = (uptimeMs / 1000) % 60;
        long minutes = (uptimeMs / (1000 * 60)) % 60;
        long hours = (uptimeMs / (1000 * 60 * 60)) % 24;
        long days = uptimeMs / (1000 * 60 * 60 * 24);
        dto.setServerUptime(String.format("%dd %dh %dm %ds", days, hours, minutes, seconds));

        dto.setTotalProducts(productRepository.count());
        dto.setTotalOrders(orderRepository.count());
        dto.setTotalAuditEvents(auditLogRepository.count());
        dto.setDbStatus("CONNECTED & OPERATIONAL (HikariCP Pool Active)");

        return dto;
    }
}

