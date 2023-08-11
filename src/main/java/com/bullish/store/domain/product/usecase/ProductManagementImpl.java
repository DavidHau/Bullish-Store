package com.bullish.store.domain.product.usecase;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductManagementImpl implements ProductManagement {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

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

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll()
            .stream().map(productMapper::productEntityToDto)
            .collect(Collectors.toList());
    }
}
