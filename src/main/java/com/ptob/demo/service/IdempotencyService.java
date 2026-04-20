package com.ptob.demo.service;

import com.ptob.demo.persistence.IdempotencyRecordEntity;
import com.ptob.demo.persistence.IdempotencyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class IdempotencyService {
    private final IdempotencyRecordRepository repository;

    public IdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<String> lookup(String idempotencyKey) {
        return repository.findById(idempotencyKey).map(IdempotencyRecordEntity::getReferenceValue);
    }

    @Transactional
    public void record(String idempotencyKey, String referenceValue) {
        repository.save(new IdempotencyRecordEntity(idempotencyKey, referenceValue, Instant.now()));
    }
}
