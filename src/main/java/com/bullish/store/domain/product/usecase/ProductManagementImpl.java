package com.bullish.store.domain.product.usecase;

import com.bullish.store.domain.product.api.ProductManagement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductManagementImpl implements ProductManagement {

    private final ProductRepository productRepository;

    public ProductManagementImpl(
        ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    @Override
    public String create(CreateProductRequest request) {
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name(request.productName())
            .description(request.description())
            .build());
        return product.getId().toString();
    }
}
