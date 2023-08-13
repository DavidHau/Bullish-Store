package com.bullish.store.domain.purchase.port;

import com.bullish.store.common.exception.ProductNotFoundException;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import org.springframework.stereotype.Service;

@Service
public class PurchaseProductDomainApi {
    private final ProductShelfService shelfService;

    public PurchaseProductDomainApi(
        ProductShelfService shelfService
    ) {
        this.shelfService = shelfService;
    }

    public ShelfGoodDto findGood(String shelfGoodId) {
        return shelfService.findGood(shelfGoodId)
            .orElseThrow(() -> new ProductNotFoundException("shelf good doesn't exist!"));
    }
}
