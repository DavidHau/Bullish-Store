package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Discount Management")
@AdminFacadeController
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(
        DiscountService discountService
    ) {
        this.discountService = discountService;
    }

    @Operation(summary = "Create Ratio Discount")
    @PostMapping("/adjustment/discount/ratio")
    public ResponseEntity<String> createProduct(
        @RequestBody DiscountManagement.CreateRatioDiscountRequest ratioDiscountRequest
    ) {
        String discountId = discountService.create(ratioDiscountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(discountId);
    }

}
