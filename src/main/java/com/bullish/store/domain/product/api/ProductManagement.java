package com.bullish.store.domain.product.api;

public interface ProductManagement {

    /**
     * @param request product
     * @return productId
     */
    String create(CreateProductRequest request);


    record CreateProductRequest(
        String productName,
        String description
    ) {
    }

}
