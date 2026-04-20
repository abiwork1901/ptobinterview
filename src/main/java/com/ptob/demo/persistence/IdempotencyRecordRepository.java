package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecordEntity, String> {}
