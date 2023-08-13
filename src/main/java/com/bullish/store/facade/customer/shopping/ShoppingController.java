package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.facade.customer.CustomerFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> addProductToBasket(
        @PathVariable("shelf-good-id") String shelfGoodId,
        @RequestHeader("x-bullish-customer-id") String customerId
    ) {
        shoppingService.addToBasket(customerId, shelfGoodId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove Product from Basket")
    @DeleteMapping("/basket/{shelf-good-id}")
    public ResponseEntity<String> removeProductFromBasket(
        @PathVariable("shelf-good-id") String shelfGoodId,
        @RequestHeader("x-bullish-customer-id") String customerId
    ) {
        shoppingService.removeFromBasket(customerId, shelfGoodId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get Basket Receipt")
    @GetMapping("/basket/receipt")
    public ResponseEntity<BasketReceiptDto> getReceipt(
        @RequestHeader("x-bullish-customer-id") String customerId
    ) {
        BasketReceiptDto receipt = shoppingService.getReceipt(customerId);
        return ResponseEntity.ok(receipt);
    }
}
