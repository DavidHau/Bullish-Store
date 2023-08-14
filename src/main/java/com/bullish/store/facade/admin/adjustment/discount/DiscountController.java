package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@Tag(name = "Discount Management")
@AdminFacadeController
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(
        DiscountService discountService
    ) {
        this.discountService = discountService;
    }

    @Operation(summary = "Create Ratio Discount"
        , description = "DiscountId will be returned.")
    @PostMapping("/adjustment/discount/ratio")
    public ResponseEntity<String> createRatioDiscount(
        @RequestBody DiscountManagement.CreateRatioDiscountRequest ratioDiscountRequest
    ) {
        String discountId = discountService.create(ratioDiscountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(discountId);
    }

    @Operation(summary = "Create Amount Discount"
        , description = "DiscountId will be returned.")
    @PostMapping("/adjustment/discount/amount")
    public ResponseEntity<String> createAmountDiscount(
        @RequestBody CreateAmountDiscountRequest amountDiscountRequest
    ) {
        String discountId = discountService.create(amountDiscountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(discountId);
    }

    record CreateAmountDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        String currency,
        BigDecimal discountAmount,
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
    }
}
