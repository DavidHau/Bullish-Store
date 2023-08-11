package com.bullish.store.domain.product.usecase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShelfRepository extends JpaRepository<ShelfGoodEntity, UUID> {
    Optional<ShelfGoodEntity> findByProductId(UUID productId);
}
