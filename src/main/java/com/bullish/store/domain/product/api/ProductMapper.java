package com.bullish.store.domain.product.api;

import com.bullish.store.domain.product.usecase.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "productId", source = "id")
    ProductDto productEntityToDto(ProductEntity entity);
}
