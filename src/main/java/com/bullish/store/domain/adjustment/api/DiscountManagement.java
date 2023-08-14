package com.bullish.store.domain.adjustment.api;


import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.List;

public interface DiscountManagement {
    /**
     * @param request
     * @return discountId
     */
    String addRatioDiscount(CreateRatioDiscount request);

    /**
     * @param request
     * @return discountId
     */
    String addAmountDiscount(CreateAmountDiscount request);

    List<DiscountRatioDto> getAllAutoApplyRatioDiscount();

    List<DiscountAmountDto> getAllAutoApplyAmountDiscount();

    // TODO get all discounts

    record CreateRatioDiscount(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        double offRatio,
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
    }

    record CreateAmountDiscount(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        Money discountAmount,
        int applyAtEveryNthNumberOfIdenticalItem
    ) {
        public CreateAmountDiscount(
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
