package com.bullish.store.domain.purchase.usecase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LineItemRepository extends JpaRepository<LineItemEntity, UUID> {
    List<LineItemEntity> findAllByBasket(BasketEntity basket);
}
