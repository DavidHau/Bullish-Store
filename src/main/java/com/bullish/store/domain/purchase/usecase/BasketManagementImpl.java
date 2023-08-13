package com.bullish.store.domain.purchase.usecase;

import com.bullish.store.common.exception.ProductNotFoundException;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import com.bullish.store.domain.purchase.api.LineItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            .shelfGoodId(good.getShelfGoodId())
            .build());
        return basketEntity.getId().toString();
    }

    @Override
    public void removeShelfGoodFromBasket(String customerId, String shelfGoodId) {
        BasketEntity basketEntity = basketRepository.findByCustomerId(customerId)
            .orElseThrow();
        List<LineItemEntity> allItemsInBasket = lineItemRepository.findAllByBasket(basketEntity);
        final boolean isDeletingLastItemInBasket = allItemsInBasket.size() == 1;

        LineItemEntity toBeRemovedLineItemEntity = allItemsInBasket.stream()
            .filter(item -> shelfGoodId.equals(item.getShelfGoodId()))
            .findFirst()
            .orElseThrow();
        lineItemRepository.delete(toBeRemovedLineItemEntity);
        if (isDeletingLastItemInBasket) {
            basketRepository.delete(basketEntity);
        }
    }

    @Override
    public Optional<BasketDto> getBasket(String customerId) {
        Optional<BasketEntity> basketEntityOptional = basketRepository.findByCustomerId(customerId);
        if (basketEntityOptional.isEmpty()) {
            return Optional.empty();
        }
        BasketEntity basketEntity = basketEntityOptional.get();
        List<LineItemEntity> lineItems = lineItemRepository.findAllByBasket(basketEntity);

        return Optional.of(new BasketDto(
            basketEntity.getId().toString(),
            basketEntity.getCustomerId(),
            lineItems.stream()
                .map(item -> new LineItemDto(item.getId().toString(),
                    item.getShelfGoodId(),
                    item.getProductId()))
                .toList()
        ));
    }

}
