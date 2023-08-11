package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductShelfService;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductService {
    private final ProductManagement productManagement;
    private final ProductShelfService shelfService;

    public ProductService(
        ProductManagement productManagement,
        ProductShelfService shelfService
    ) {
        this.productManagement = productManagement;
        this.shelfService = shelfService;
    }

    String create(ProductManagement.CreateProductRequest productRequest) {
        return productManagement.create(productRequest);
    }

    public String launch(String productId, String currency, BigDecimal basePrice) {
        return shelfService.launch(
            productId,
            Money.of(basePrice, currency)
        );
    }
}
