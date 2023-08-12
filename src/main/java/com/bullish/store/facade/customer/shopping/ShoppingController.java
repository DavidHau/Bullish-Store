package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.facade.customer.CustomerFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Shopping")
@CustomerFacadeController
public class ShoppingController {

    private final ShoppingService shoppingService;

    public ShoppingController(
        ShoppingService shoppingService
    ) {
        this.shoppingService = shoppingService;
    }

    @Operation(summary = "List All Products on Shelf")
    @GetMapping("/shelf/goods")
    public ResponseEntity<List<ShelfGoodDto>> getAllShelfGoods() {
        List<ShelfGoodDto> shelfGoods = shoppingService.findAllProductOnSale();
        return ResponseEntity.ok(shelfGoods);
    }

    @Operation(summary = "Add Product to Basket")
    @PostMapping("/basket/{shelf-good-id}")
    public ResponseEntity<String> launchProduct(
        @PathVariable("shelf-good-id") String shelfGoodId,
        @RequestHeader("x-bullish-customer-id") String customerId
    ) {
        shoppingService.addToBasket(customerId, shelfGoodId);
        return ResponseEntity.noContent().build();
    }
}
