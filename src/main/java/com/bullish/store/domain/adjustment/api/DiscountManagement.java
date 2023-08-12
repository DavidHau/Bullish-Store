package com.bullish.store.domain.adjustment.api;


public interface DiscountManagement {
    /**
     * @param request
     * @return discountId
     */
    String addRatioDiscount(CreateRatioDiscountRequest request);

    record CreateRatioDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        double offRatio,
        int applyAtEveryNthNumberOfItem
    ) {
    }
}
