package com.bullish.store.domain.purchase.usecase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BasketRepository extends JpaRepository<BasketEntity, UUID> {
    Optional<BasketEntity> findByCustomerId(String customerId);

    default BasketEntity saveIfNotExist(BasketEntity basketEntity) {
        Optional<BasketEntity> existingEntityOptional = findByCustomerId(basketEntity.getCustomerId());
        if (existingEntityOptional.isPresent()) {
            basketEntity.setId(existingEntityOptional.get().getId());
        }
        return this.save(basketEntity);
    }
}
