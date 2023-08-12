package com.bullish.store.domain.purchase.api;

public interface BasketManagement {

    /**
     * @param customerId
     * @param shelfGoodId
     * @return basketId
     */
    String addShelfGoodToBasket(String customerId, String shelfGoodId);

}
