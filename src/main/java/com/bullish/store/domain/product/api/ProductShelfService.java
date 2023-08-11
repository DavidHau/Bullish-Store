package com.bullish.store.domain.product.api;

import org.javamoney.moneta.Money;

import java.util.List;

public interface ProductShelfService {

    String launch(String productId, Money basePrice);

    void discontinue(String productId, String shelfGoodId);

    List<ShelfGoodDto> findAllGoods();
}
