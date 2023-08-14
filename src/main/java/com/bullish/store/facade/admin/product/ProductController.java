package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Create Product"
        , description = "ProductId will be returned.")
    @PostMapping("/products")
    public ResponseEntity<String> createProduct(
        @RequestBody CreateProductRequest productRequest
    ) {
        String productId = productService.create(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @Operation(summary = "List Products")
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productList = productService.findAllProduct();
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Permanently Delete Product")
    @DeleteMapping("/products/{product-id}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable("product-id") String productId,
        @RequestParam(value = "auto-discontinue", defaultValue = "false") boolean isAutoDiscontinue
    ) {
        productService.delete(
            productId,
            isAutoDiscontinue
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Put Product On Shelf"
        , description = "ShelfGoodId will be returned.")
    @PostMapping("/products/{product-id}/launch")
    public ResponseEntity<String> launchProduct(
        @PathVariable("product-id") String productId,
        @RequestBody ProductLaunchRequestDto productLaunchRequest
    ) {
        String shelfGoodId = productService.launch(
            productId,
            productLaunchRequest.currency,
            productLaunchRequest.basePrice
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(shelfGoodId);
    }

    @Operation(summary = "Remove Product From Shelf")
    @DeleteMapping("/products/{product-id}/discontinue/{shelf-good-id}")
    public ResponseEntity<Void> discontinueProduct(
        @PathVariable("product-id") String productId,
        @PathVariable("shelf-good-id") String shelfGoodId
    ) {
        productService.discontinue(
            productId,
            shelfGoodId
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List All Products on Shelf")
    @GetMapping("/products/shelf/goods")
    public ResponseEntity<List<ShelfGoodDto>> getAllShelfGoods() {
        List<ShelfGoodDto> shelfGoods = productService.findAllProductOnSale();
        return ResponseEntity.ok(shelfGoods);
    }

    record CreateProductRequest(
        @Schema(example = "iPhone 13 mini")
        String productName,
        @Schema(example = "Apple 13th small edition")
        String description,
        @Schema(example = "HKD")
        String currency,
        @Schema(example = "5000")
        BigDecimal basePrice
    ) {
    }

    record ProductLaunchRequestDto(
        @Schema(example = "HKD")
        String currency,
        @Schema(example = "5000")
        BigDecimal basePrice
    ) {
    }
}
