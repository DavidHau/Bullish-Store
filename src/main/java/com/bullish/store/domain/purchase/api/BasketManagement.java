package com.bullish.store.domain.purchase.api;

import java.util.Optional;

public interface BasketManagement {

    /**
     * @param customerId
     * @param shelfGoodId
     * @return basketId
     */
    String addShelfGoodToBasket(String customerId, String shelfGoodId);

    void removeShelfGoodFromBasket(String customerId, String shelfGoodId);

    Optional<BasketDto> getBasket(String customerId);
}
