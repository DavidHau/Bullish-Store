package com.bullish.store.domain.product.usecase;

import com.bullish.store.common.exception.DataInconsistentException;
import com.bullish.store.domain.product.api.ProductMapper;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import org.javamoney.moneta.Money;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductShelfServiceImpl implements ProductShelfService {

    private final ProductRepository productRepository;
    private final ShelfRepository shelfRepository;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

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

    @Override
    public List<ShelfGoodDto> findAllGoods() {
        List<ShelfGoodEntity> shelfGoodEntities = shelfRepository.findAll();
        return shelfGoodEntities.stream()
            .map(productMapper::shelfGoodEntityToDto)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ShelfGoodDto> findGood(String shelfGoodId) {
        return shelfRepository.findById(
            UUID.fromString(shelfGoodId)
        ).map(productMapper::shelfGoodEntityToDto);
    }
}
