package com.bullish.store.domain.adjustment.usecase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscountAmountRepository extends JpaRepository<DiscountAmountEntity, UUID> {
}
