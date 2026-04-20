package com.ptob.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    Optional<PositionEntity> findByAccountIdAndAsset(String accountId, String asset);
}
