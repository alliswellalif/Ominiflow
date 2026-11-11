package com.omniflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String action; // CREATE, UPDATE, DELETE, DISPATCH, STATUS_CHANGE, LOGIN, SETTINGS_UPDATE

    @Column(length = 50)
    private String entityName; // Product, Order, User, Setting

    @Column(length = 50)
    private String entityId;

    @Column(nullable = false, length = 50)
    private String performedBy;

    @Column(length = 30)
    private String userRole;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false, length = 20)
    private String severity = "INFO"; // INFO, WARNING, CRITICAL

    @Column(length = 1000)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AuditLog() {
    }

    public AuditLog(String action, String entityName, String entityId, String performedBy, 
                    String userRole, String ipAddress, String severity, String details) {
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.performedBy = performedBy;
        this.userRole = userRole;
        this.ipAddress = ipAddress;
        this.severity = (severity != null) ? severity : "INFO";
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

