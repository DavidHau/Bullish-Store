package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingService {
    private final ProductShelfService shelfService;

    public ShoppingService(
        ProductShelfService shelfService
    ) {
        this.shelfService = shelfService;
    }

    public List<ShelfGoodDto> findAllProductOnSale() {
        return shelfService.findAllGoods();
    }
}
