package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface TransferRepository extends JpaRepository<TransferEntity, String> {}
