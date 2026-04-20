package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface TradeRepository extends JpaRepository<TradeEntity, String> {}
