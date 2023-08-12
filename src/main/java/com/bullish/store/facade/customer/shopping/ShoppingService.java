package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingService {
    private final ProductShelfService shelfService;
    private final BasketManagement basketManagement;

    public ShoppingService(
        ProductShelfService shelfService,
        BasketManagement basketManagement
    ) {
        this.shelfService = shelfService;
        this.basketManagement = basketManagement;
    }

    public List<ShelfGoodDto> findAllProductOnSale() {
        return shelfService.findAllGoods();
    }

    public void addToBasket(String customerId, String shelfGoodId) {
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId);
    }
}
