package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    String create(ProductController.CreateProductRequest productRequest) {
        String productId = productManagement.create(new ProductManagement.CreateProductRequest(
            productRequest.productName(),
            productRequest.description()
        ));

        // Auto launch product
        if (StringUtils.isNotEmpty(productRequest.currency())) {
            launch(productId, productRequest.currency(), productRequest.basePrice());
        }
        return productId;
    }

    List<ProductDto> findAllProduct() {
        return productManagement.findAll();
    }

    public void delete(String productId, boolean isAutoDiscontinue) {
        if (isAutoDiscontinue) {
            productManagement.deleteOnSaleProduct(productId);
        } else {
            productManagement.deleteNotOnSaleProduct(productId);
        }
    }

    public String launch(String productId, String currency, BigDecimal basePrice) {
        return shelfService.launch(
            productId,
            Money.of(basePrice, currency)
        );
    }

    public void discontinue(String productId, String shelfGoodId) {
        shelfService.discontinue(productId, shelfGoodId);
    }

    public List<ShelfGoodDto> findAllProductOnSale() {
        return shelfService.findAllGoods();
    }
}
