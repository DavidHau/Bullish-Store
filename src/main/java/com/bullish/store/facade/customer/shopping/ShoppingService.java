package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.checkout.api.CheckOutService;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingService {
    private final ProductShelfService shelfService;
    private final BasketManagement basketManagement;
    private final CheckOutService checkOutService;
    private final ShoppingMapper shoppingMapper = Mappers.getMapper(ShoppingMapper.class);

    public ShoppingService(
        ProductShelfService shelfService,
        BasketManagement basketManagement,
        CheckOutService checkOutService
    ) {
        this.shelfService = shelfService;
        this.basketManagement = basketManagement;
        this.checkOutService = checkOutService;
    }

    public List<ShelfGoodDto> findAllProductOnSale() {
        return shelfService.findAllGoods();
    }

    public void addToBasket(String customerId, String shelfGoodId) {
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId);
    }

    public void removeFromBasket(String customerId, String shelfGoodId) {
        basketManagement.removeShelfGoodFromBasket(customerId, shelfGoodId);
    }

    public BasketReceiptDto getReceipt(String customerId) {
        return checkOutService.getReceipt(customerId)
            .map(shoppingMapper::receiptDtoToBasketReceiptDto)
            .orElse(new BasketReceiptDto());
    }
}
