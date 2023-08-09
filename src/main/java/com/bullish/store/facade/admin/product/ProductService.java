package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductManagement;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductManagement productManagement;

    public ProductService(
        ProductManagement productManagement
    ) {
        this.productManagement = productManagement;
    }

    String create(ProductManagement.CreateProductRequest productRequest) {
        return productManagement.create(productRequest);
    }
}
