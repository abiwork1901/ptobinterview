package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountBalanceRepository extends JpaRepository<AccountBalanceEntity, Long> {
    Optional<AccountBalanceEntity> findByAccountIdAndAsset(String accountId, String asset);
    List<AccountBalanceEntity> findByAccountId(String accountId);
    List<AccountBalanceEntity> findByAsset(String asset);
}
