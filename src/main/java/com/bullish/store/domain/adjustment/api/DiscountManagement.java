package com.bullish.store.domain.adjustment.api;


import io.swagger.v3.oas.annotations.media.Schema;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountManagement {
    /**
     * @param request
     * @return discountId
     */
    String addRatioDiscount(CreateRatioDiscountRequest request);

    /**
     * @param request
     * @return discountId
     */
    String addAmountDiscount(CreateAmountDiscountRequest request);

    List<DiscountRatioDto> getAllAutoApplyRatioDiscount();

    List<DiscountAmountDto> getAllAutoApplyAmountDiscount();

    // TODO get all discounts

    record CreateRatioDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        @Schema(name = "Discount ratio", example = "0.3", description = "value should be between 0 and 1")
        double offRatio,
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
    }

    record CreateAmountDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        Money discountAmount,
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
        public CreateAmountDiscountRequest(
            String discountName,
            boolean isApplyToAllProduct,
            String shelfGoodId,
            String currency,
            BigDecimal discountAmount,
            int applyAtEveryNthNumberOfIdenticalItem
        ) {
            this(
                discountName,
                isApplyToAllProduct,
                shelfGoodId,
                Money.of(discountAmount, currency),
                applyAtEveryNthNumberOfIdenticalItem
            );
        }
    }
}
