package com.bullish.store.domain.product.usecase;

import com.bullish.store.common.exception.ProductOnSaleLockException;
import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductManagementImpl implements ProductManagement {

    private final ProductRepository productRepository;
    private final ShelfRepository shelfRepository;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    public ProductManagementImpl(
        ProductRepository productRepository,
        ShelfRepository shelfRepository
    ) {
        this.productRepository = productRepository;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public String create(CreateProductRequest request) {
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name(request.productName())
            .description(request.description())
            .build());
        return product.getId().toString();
    }

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll()
            .stream().map(productMapper::productEntityToDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteNotOnSaleProduct(String productIdStr) {
        UUID productId = UUID.fromString(productIdStr);
        boolean isProductCurrentlyOnSale = shelfRepository.findByProductId(productId).isPresent();
        if (isProductCurrentlyOnSale) {
            throw new ProductOnSaleLockException("Please discontinue product before removing product.");
        }
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteOnSaleProduct(String productIdStr) {
        UUID productId = UUID.fromString(productIdStr);
        Optional<ShelfGoodEntity> shelfGoodOptional = shelfRepository.findByProductId(productId);
        shelfGoodOptional.ifPresent(shelfRepository::delete);
        productRepository.deleteById(productId);
    }

}
