package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {}
