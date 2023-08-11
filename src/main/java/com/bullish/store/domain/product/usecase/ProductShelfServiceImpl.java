package com.bullish.store.domain.product.usecase;

import com.bullish.store.common.exception.DataInconsistentException;
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
        // TODO: throw custom exception when product's not found
        ShelfGoodEntity shelfGoodEntity = shelfRepository.save(ShelfGoodEntity.builder()
            .product(productEntity)
            .currency(basePrice.getCurrency().getCurrencyCode())
            .basePrice(basePrice.getNumberStripped())
            .build());
        return shelfGoodEntity.getId().toString();
    }

    @Override
    public void discontinue(String productId, String shelfGoodId) {
        ShelfGoodEntity shelfGoodEntity = shelfRepository.findByProductId(UUID.fromString(productId)).orElseThrow();
        if (!shelfGoodEntity.getId().toString().equals(shelfGoodId)) {
            throw new DataInconsistentException("Product in shelf is outdated.");
        }

        shelfRepository.delete(shelfGoodEntity);
    }
}
