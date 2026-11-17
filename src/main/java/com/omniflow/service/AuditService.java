package com.omniflow.service;

import com.omniflow.model.AuditLog;
import com.omniflow.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String action, String entityName, String entityId, String severity, String details) {
        String username = "SYSTEM";
        String role = "SYSTEM";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            username = auth.getName();
            if (!auth.getAuthorities().isEmpty()) {
                role = auth.getAuthorities().iterator().next().getAuthority();
            }
        }

        String ip = "127.0.0.1";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                String header = req.getHeader("X-Forwarded-For");
                if (header != null && !header.isEmpty()) {
                    ip = header.split(",")[0].trim();
                } else {
                    ip = req.getRemoteAddr();
                }
            }
        } catch (Exception ignored) {
        }

        AuditLog log = new AuditLog(action, entityName, entityId, username, role, ip, severity, details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsBySeverity(String severity) {
        return auditLogRepository.findBySeverityOrderByTimestampDesc(severity);
    }

    public long getTotalLogsCount() {
        return auditLogRepository.count();
    }
}

