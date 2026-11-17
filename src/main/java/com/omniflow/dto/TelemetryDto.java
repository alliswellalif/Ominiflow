package com.omniflow.dto;

public class TelemetryDto {
    private String jvmVersion;
    private String osName;
    private int availableProcessors;
    private long totalMemoryMb;
    private long usedMemoryMb;
    private long freeMemoryMb;
    private int memoryUsagePercent;
    private String serverUptime;
    private long totalAuditEvents;
    private long totalProducts;
    private long totalOrders;
    private String dbStatus;

    public TelemetryDto() {
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public long getTotalMemoryMb() {
        return totalMemoryMb;
    }

    public void setTotalMemoryMb(long totalMemoryMb) {
        this.totalMemoryMb = totalMemoryMb;
    }

    public long getUsedMemoryMb() {
        return usedMemoryMb;
    }

    public void setUsedMemoryMb(long usedMemoryMb) {
        this.usedMemoryMb = usedMemoryMb;
    }

    public long getFreeMemoryMb() {
        return freeMemoryMb;
    }

    public void setFreeMemoryMb(long freeMemoryMb) {
        this.freeMemoryMb = freeMemoryMb;
    }

    public int getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public void setMemoryUsagePercent(int memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }

    public String getServerUptime() {
        return serverUptime;
    }

    public void setServerUptime(String serverUptime) {
        this.serverUptime = serverUptime;
    }

    public long getTotalAuditEvents() {
        return totalAuditEvents;
    }

    public void setTotalAuditEvents(long totalAuditEvents) {
        this.totalAuditEvents = totalAuditEvents;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }
}

