package com.ptob.demo.service;

import com.ptob.demo.model.PositionLot;
import com.ptob.demo.persistence.PositionEntity;
import com.ptob.demo.persistence.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CostBasisService {
    private final PositionRepository positionRepository;

    public CostBasisService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional
    public void recordBuy(String accountId, String asset, BigDecimal quantity, BigDecimal quoteAmount) {
        PositionEntity position = positionRepository.findByAccountIdAndAsset(accountId, asset)
                .orElseGet(() -> new PositionEntity(accountId, asset));
        position.setQuantity(position.getQuantity().add(quantity));
        position.setTotalCost(position.getTotalCost().add(quoteAmount));
        positionRepository.save(position);
    }

    @Transactional
    public void recordSell(String accountId, String asset, BigDecimal quantity) {
        PositionEntity position = positionRepository.findByAccountIdAndAsset(accountId, asset)
                .orElseGet(() -> new PositionEntity(accountId, asset));
        if (position.getQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient position quantity for weighted-average sell");
        }
        BigDecimal avg = average(position);
        position.setQuantity(position.getQuantity().subtract(quantity));
        position.setTotalCost(position.getTotalCost().subtract(avg.multiply(quantity)));
        if (position.getQuantity().signum() == 0) {
            position.setTotalCost(BigDecimal.ZERO);
        }
        positionRepository.save(position);
    }

    @Transactional(readOnly = true)
    public PositionLot getPosition(String accountId, String asset) {
        PositionEntity position = positionRepository.findByAccountIdAndAsset(accountId, asset)
                .orElseGet(() -> new PositionEntity(accountId, asset));
        return new PositionLot(asset, position.getQuantity(), position.getTotalCost(), average(position));
    }

    private BigDecimal average(PositionEntity position) {
        return position.getQuantity().signum() == 0 ? BigDecimal.ZERO :
                position.getTotalCost().divide(position.getQuantity(), 8, RoundingMode.HALF_UP);
    }
}
