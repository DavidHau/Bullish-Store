package com.bullish.store.domain.product.api;

import java.util.List;

public interface ProductManagement {

    /**
     * @param request product
     * @return productId
     */
    String create(CreateProductRequest request);

    List<ProductDto> findAll();

    void deleteNotOnSaleProduct(String productId);

    record CreateProductRequest(
        String productName,
        String description
    ) {
    }

}
