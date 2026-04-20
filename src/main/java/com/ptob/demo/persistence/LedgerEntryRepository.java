package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, String> {}
