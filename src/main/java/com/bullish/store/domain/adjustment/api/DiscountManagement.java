package com.bullish.store.domain.adjustment.api;


import org.javamoney.moneta.Money;

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

    // TODO get all auto apply amount discount

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
    }
}
