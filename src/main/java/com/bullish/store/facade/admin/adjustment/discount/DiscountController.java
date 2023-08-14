package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.facade.admin.AdminFacadeController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @RequestBody CreateRatioDiscountRequest ratioDiscountRequest
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

    record CreateRatioDiscountRequest(
        @Schema(example = "Every item get 30% off")
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        @Schema(example = "0.3", description = "value should be between 0 and 1")
        double offRatio,
        @Schema(example = "1", description = "1 means every item")
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
    }

    record CreateAmountDiscountRequest(
        @Schema(example = "Every 2nd iPhone 13 mini get HKD 500 off")
        String discountName,
        @Schema(example = "false")
        boolean isApplyToAllProduct,
        String shelfGoodId,
        @Schema(example = "HKD")
        String currency,
        @Schema(example = "500")
        BigDecimal discountAmount,
        @Schema(example = "2", description = "1 means every item")
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
    }
}
