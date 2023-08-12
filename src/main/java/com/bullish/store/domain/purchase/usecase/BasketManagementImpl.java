package com.bullish.store.domain.purchase.usecase;

import com.bullish.store.common.exception.ProductNotFoundException;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BasketManagementImpl implements BasketManagement {
    private final ProductShelfService shelfService;
    private final BasketRepository basketRepository;
    private final LineItemRepository lineItemRepository;

    public BasketManagementImpl(
        ProductShelfService shelfService,
        BasketRepository basketRepository,
        LineItemRepository lineItemRepository
    ) {
        this.shelfService = shelfService;
        this.basketRepository = basketRepository;
        this.lineItemRepository = lineItemRepository;
    }

    @Override
    public String addShelfGoodToBasket(String customerId, String shelfGoodId) {
        ShelfGoodDto good = shelfService.findGood(shelfGoodId)
            .orElseThrow(() -> new ProductNotFoundException("shelf good doesn't exist!"));
        BasketEntity basketEntity = basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId(customerId)
            .build());
        lineItemRepository.save(LineItemEntity.builder()
            .basket(basketEntity)
            .productId(good.getProduct().getProductId())
            .shelfGoodId(good.getId())
            .build());
        return basketEntity.getId().toString();
    }

    // TODO: after removing all items, auto delete basket
}
