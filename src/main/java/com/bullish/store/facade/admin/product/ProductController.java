package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Product Management")
@AdminFacadeController
public class ProductController {

    private final ProductService productService;

    public ProductController(
        ProductService productService
    ) {
        this.productService = productService;
    }

    @Operation(summary = "Create Product")
    @PostMapping("/product")
    public ResponseEntity<String> createProduct(
        @RequestBody ProductManagement.CreateProductRequest productRequest
    ) {
        String productId = productService.create(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
