package com.bullish.store.domain.product.usecase;

import com.bullish.store.domain.product.api.ProductShelfService;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProductShelfServiceImpl implements ProductShelfService {

    private final ProductRepository productRepository;
    private final ShelfRepository shelfRepository;

    public ProductShelfServiceImpl(
        ProductRepository productRepository,
        ShelfRepository shelfRepository
    ) {
        this.productRepository = productRepository;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public String launch(String productId, Money basePrice) {
        ProductEntity productEntity = productRepository.findById(UUID.fromString(productId))
            .orElseThrow();
        ShelfGoodEntity shelfGoodEntity = shelfRepository.save(ShelfGoodEntity.builder()
            .productId(productEntity.getId())
            .currency(basePrice.getCurrency().getCurrencyCode())
            .basePrice(basePrice.getNumberStripped())
            .build());
        return shelfGoodEntity.getId().toString();
    }

    @Override
    public void discontinue(String productId) {

    }
}
