package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.facade.customer.CustomerFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
}
