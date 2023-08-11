package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

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
    @PostMapping("/products")
    public ResponseEntity<String> createProduct(
        @RequestBody ProductManagement.CreateProductRequest productRequest
    ) {
        String productId = productService.create(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @Operation(summary = "List Products")
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productList = productService.findAll();
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Put Product On Shelf")
    @PostMapping("/products/{product-id}/launch")
    public ResponseEntity<String> createProduct(
        @PathVariable("product-id") String productId,
        @RequestBody ProductLaunchRequestDto productLaunchRequest
    ) {
        String shelfGoodId = productService.launch(productId,
            productLaunchRequest.currency,
            productLaunchRequest.basePrice
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(shelfGoodId);
    }

    record ProductLaunchRequestDto(
        String currency,
        BigDecimal basePrice
    ) {
    }
}
