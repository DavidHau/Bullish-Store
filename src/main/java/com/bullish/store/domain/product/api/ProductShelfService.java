package com.bullish.store.domain.product.api;

import org.javamoney.moneta.Money;

public interface ProductShelfService {

    String launch(String productId, Money basePrice);

    void discontinue(String productId);
}
