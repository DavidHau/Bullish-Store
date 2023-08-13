package com.bullish.store.domain.adjustment.api;


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
        double offRatio,
        int applyAtEveryNthNumberOfItem
    ) {
    }

    record CreateAmountDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        Money discountAmount,
        int applyAtEveryNthNumberOfItem
    ) {
        public CreateAmountDiscountRequest(
            String discountName,
            boolean isApplyToAllProduct,
            String shelfGoodId,
            String currency,
            BigDecimal discountAmount,
            int applyAtEveryNthNumberOfItem
        ) {
            this(
                discountName,
                isApplyToAllProduct,
                shelfGoodId,
                Money.of(discountAmount, currency),
                applyAtEveryNthNumberOfItem
            );
        }
    }
}
