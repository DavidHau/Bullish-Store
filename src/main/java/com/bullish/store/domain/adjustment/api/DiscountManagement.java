package com.bullish.store.domain.adjustment.api;


import java.util.List;

public interface DiscountManagement {
    /**
     * @param request
     * @return discountId
     */
    String addRatioDiscount(CreateRatioDiscountRequest request);

    List<DiscountRatioDto> getAllAutoApplyRatioDiscount();

    record CreateRatioDiscountRequest(
        String discountName,
        boolean isApplyToAllProduct,
        String shelfGoodId,
        double offRatio,
        int applyAtEveryNthNumberOfItem
    ) {
    }
}
