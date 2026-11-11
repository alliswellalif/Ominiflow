package com.omniflow.repository;

import com.omniflow.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop100ByOrderByTimestampDesc();
    List<AuditLog> findBySeverityOrderByTimestampDesc(String severity);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    long count();
}

